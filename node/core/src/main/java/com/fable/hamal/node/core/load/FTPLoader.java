package com.fable.hamal.node.core.load;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.common.datasource.file.FtpConnectionParameter;
import com.fable.hamal.node.common.datasource.file.OperateFile;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.FileData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.config.metadata.File;
import com.fable.hamal.shuttle.common.model.config.metadata.Ftp;

public class FTPLoader implements Loader {

    private final static Logger logger = LoggerFactory
        .getLogger(FTPLoader.class);
    private static final String SEPARATOR = "/";
    private static final String TEMPFOLDER = "/SSTemp/";
    private static final String BAKFOLDER = "/sjjhBak/";
    private static final int OPERATEFILE = 1; // 0 : 跳过同名文件；1 : 覆盖同名文件
    private static final int COPYFILE = 1; // 0 ：抽取后删除 ；1 ：抽取后另存为

    private static final String SUCCESS = "成功";
    private static final String FAILED = "失败";
    private static final String ERROR = "报错";
    
    private Map<String,List<String>> filesMap = new HashMap<String,List<String>>();
    private LinkedList<FTPClient> m_ftpTargetList = new LinkedList<FTPClient>();
    private LinkedList<FTPClient> m_ftpSourceList = new LinkedList<FTPClient>();
    private List<String> m_deleteFile = new ArrayList<String>();
    private List<String> m_floderList = new ArrayList<String>();
    private List<String> m_deleteIllegal = new ArrayList<String>();
    private FtpConnectionParameter sourceConnParam =
        new FtpConnectionParameter();
    private FtpConnectionParameter targetConnParam =
        new FtpConnectionParameter();
    private Thread[] m_threads = null;
    private Pump pump;
    private Pair pair;
    private String m_sourcePath;
    private String m_path;
    private boolean m_loading;
    
    public void load(BatchData data) {
        
        if(logger.isInfoEnabled()){
            logger.info("FTP---Loader--开始！");
        }
        m_loading = true;
        List<FileData> listdata = data.getFdb().getList();
        final LinkedList<FileData> fileDataList = new LinkedList<FileData>();
        fileDataList.addAll(listdata);
        if (fileDataList.size() <= 0)
            return;
        m_threads = new Thread[fileDataList.size()];
        try {
            while (m_loading) {
                for (int i = 0; i < m_threads.length; i++) {
                    this.m_threads[i] = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            
                            FileData fd = null;
                            synchronized (fileDataList) {
                                if (!fileDataList.isEmpty()) {
                                    fd = fileDataList.removeFirst();
                                }
                                else {
                                    return;
                                }
                            }
                            
                            processOne(fd);
                            
                        }
                    }, "FTPLoader Thread" + i);
                    this.m_threads[i].start();
                }

                try {
                    for (int i = 0; i < m_threads.length; i++) {
                        if (this.m_threads[i] != null) {
                            this.m_threads[i].join();
                        }
                    }
                }
                catch (InterruptedException e) {

                    e.printStackTrace();
                }

                for (int i = 0; i < m_threads.length; i++) {
                    if (this.m_threads[i] != null) {
                        this.m_threads[i] = null;
                    }
                }
                
                if(fileDataList.size() == 0){
                    this.m_loading = false;    
                }
                
            }
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("加载文件时发生异常:{}", e.getMessage());
            }
            e.printStackTrace();
        }
        finally {
            synchronized (this) {
                //删除非法文件
                this.deleteIllegalFile();
                //执行另存为或删除源数据操作
                this.operteFile();
                if (logger.isInfoEnabled()) {
                    logger.info("FTP加载端，任务结束！！");
                }
            }
        }
    }

    private void processOne(final FileData fd) {

        final byte[] buf = new byte[1024 * 64];
        OutputStream m_out = null;
        InputStream inputdata = null;
        String fileTempName = null;
        String newFileName = null;
        String newWorkDir = null;
        String fileName = null;
        
        FTPClient ftp = null;
        
        synchronized (m_ftpTargetList) {

            synchronized (m_ftpTargetList) {
                
                if (m_ftpTargetList.size() > 0)
                    ftp = m_ftpTargetList.removeFirst();
                if (ftp == null)
                    ftp = this.ftpConn(targetConnParam);
            }
            
        }
        
        newFileName = fd.getFilename();
        newWorkDir = fd.getDirPath();
        this.m_floderList.add(newWorkDir);
        newFileName = newFileName.replaceFirst(this.m_sourcePath, SEPARATOR);
        newWorkDir = newWorkDir.replaceFirst(this.m_sourcePath, SEPARATOR);
        
        
        try {
            // 创建子文件夹
            createFolder(newWorkDir, ftp);
            fileName = replaceSeparator(this.m_path + newFileName);
            fileTempName =
                replaceSeparator(this.m_path + TEMPFOLDER + newFileName);
            inputdata = fd.getInputdata(); // 取出Selector的InputStream
            
            
            m_out = ftp.storeFileStream(fileTempName);
            if (m_out == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("获取文件{}输出流为NULL", fileTempName);
                }
            }

            long lReadSize = 0;
            for (int iRead = inputdata.read(buf); iRead >= 0; iRead =
                inputdata.read(buf)) {
                lReadSize += iRead;
                m_out.write(buf, 0, iRead);
            }
            logger.info("加载{}文件字节数:{}", fileTempName, lReadSize);
            inputdata.close();
            m_out.close();
            ftp.completePendingCommand();
            // 做是否覆盖还是跳过的处理
            this.operateFile(ftp, replaceSeparator(fileName),
                replaceSeparator(fileTempName), OPERATEFILE);

            boolean isRename =
                ftp.rename(replaceSeparator(fileTempName),
                    replaceSeparator(fileName));
            
            if (logger.isDebugEnabled()) {
                logger.debug("文件{}从SSTemp文件夹迁移:{}", fileName, isRename
                    ? SUCCESS : FAILED);
            }
            // 将交换完毕的文件保存到删除列表
            if (m_out != null) {
                synchronized (this.m_deleteFile) {
                    this.m_deleteFile
                        .add(replaceSeparator(fd.getFilename()));
                    if (logger.isInfoEnabled()) {
                        logger.info("将文件{}添加到删除列表", fd.getFilename());
                    }
                }
            }
        }
        catch (IOException e1) {
            logger.info(e1.getMessage());
            if(e1.getMessage().contains("illegal") && e1.getMessage().contains("SJJH")){
                if(logger.isInfoEnabled()){
                    logger.info("文件{}存在非法文件，即将被删除.", fileName);
                }
                //扫描到了非法文件，将非法文件报到集合里，事后一并删除
                synchronized (m_deleteIllegal) {
                    m_deleteIllegal.add(fileTempName);
                }
                try {
                    closeStream(inputdata, m_out);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if(logger.isDebugEnabled()){
                    logger.debug("FTP文件{}在数据加载时，发生异常:{}", fileName, e1.getMessage());
                }
                e1.printStackTrace();
            }
            disConnect(ftp);
        }
        finally {
            
                synchronized (m_ftpTargetList) {
                    
                    m_ftpTargetList.addLast(ftp);
                }
        }
    }

    public void start() {
        
        // 保存源端FTP连接信息
        File file = (File)pump.getSource();
        Ftp ftp = file.getFtp();
        this.m_sourcePath = file.getPath();
        sourceConnParam.setIp(ftp.getServerIp());
        sourceConnParam.setPort(ftp.getPort());
        sourceConnParam.setUsername(ftp.getUsername());
        sourceConnParam.setPassword(ftp.getPassword());
        sourceConnParam.setRootPath(file.getPath());

        // 保存目标端FTP连接信息
        file = (File)pair.getTarget();
        ftp = file.getFtp();
        this.m_path = file.getPath();
        targetConnParam.setIp(ftp.getServerIp());
        targetConnParam.setPort(ftp.getPort());
        targetConnParam.setUsername(ftp.getUsername());
        targetConnParam.setPassword(ftp.getPassword());
        targetConnParam.setRootPath(file.getPath());
    }

    private String replaceSeparator(String path) {
        return path.replace("//", "/");
    }

    /**
     * 建立FTP连接.
     * 
     * @return
     */
    private FTPClient ftpConn(final FtpConnectionParameter param) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.setDataTimeout(120000 * 1000);
            ftpClient.setDefaultTimeout(12000 * 1000);
            ftpClient.connect(param.getIp(), param.getPort());
            ftpClient.login(param.getUsername(), param.getPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.pasv();
            ftpClient.enterLocalPassiveMode();
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("文件加载时，连接FTP服务器出现异常：{}", e.getMessage());
            }
            e.printStackTrace();
            ftpClient = null;
        }
        return ftpClient;
    }

    /**
     * 创建子文件夹.
     * 
     * @param newWorkDir
     * @param ftp
     */
    private void createFolder(final String newWorkDir, final FTPClient ftp) throws IOException {
        final StringBuffer sb = new StringBuffer();
        try {
            for (final String dirPath : replaceSeparator(
                this.m_path + TEMPFOLDER + newWorkDir).split(SEPARATOR)) {
                if (SEPARATOR.equals(dirPath) || "".equals(dirPath) ||
                    dirPath == null)
                    continue;
                sb.append(SEPARATOR).append(dirPath);
                ftp.makeDirectory(sb.toString());

            }
            sb.setLength(0);
            for (final String dirPath : replaceSeparator(
                this.m_path + newWorkDir).split(SEPARATOR)) {
                if (SEPARATOR.equals(dirPath) || "".equals(dirPath) ||
                    dirPath == null)
                    continue;
                sb.append(SEPARATOR).append(dirPath);
                ftp.makeDirectory(sb.toString());
            }
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("创建子文件夹{}时发生异常:{}", sb.toString(),
                    e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 处理同名文件是覆盖还是跳过.
     * 
     * @param ftp
     * @param fileName
     * @param state
     */
    private void operateFile(final FTPClient ftp, final String fileName,
        final String fileTempName, final int state) throws IOException {
        boolean isExist = false;
        boolean bool = false;
        String folderName =
            fileName
                .replace(
                    fileName.split(SEPARATOR)[fileName.split(SEPARATOR).length - 1],
                    "");
        
        this.putFilePath(fileName, folderName);
        
            List<String> fileList = null;
            synchronized (filesMap) {
                fileList = filesMap.get(folderName);
            }
            
            if(null == fileList){
                logger.info("######不应该进来######");
                return;
            }
            if(fileList.size() == 0){
                return;
            }
            if(fileList.contains(fileName)){
                isExist = true;
            }

        if (state == 0) { // 等于0，跳过同名文件
            try {
                if (isExist)
                    bool = ftp.deleteFile(fileTempName);
                
                if (logger.isDebugEnabled()) {
                    logger.info("目的端(跳过同名文件)，删除临时文件{}", bool ? SUCCESS
                        : ERROR);
                }
            }
            catch (IOException e) {
                
                throw e;
            }
        }
        else { // 覆盖同名文件
            try {
                if (isExist) {
                    bool = ftp.deleteFile(fileName);
                }
                
                if (logger.isDebugEnabled()) {
                    logger.info("目的端(覆盖同名文件)，删除临时文件{}", bool ? SUCCESS
                        : ERROR);
                }
            }
            catch (IOException e) {
                
                throw e;
            }
        }
    }

    /**
     * 抽取后对文件的操作（删除，另存为）.
     */
    private void operteFile() {
        if (this.m_deleteFile.isEmpty())
            return;
        
        FTPClient ftp = null;
        synchronized (m_ftpSourceList) {
            if(m_ftpSourceList.size() > 0){
                ftp = m_ftpSourceList.removeFirst();
            }else{
                ftp = ftpConn(sourceConnParam);
            }
        }
        
        OperateFile of = new OperateFile();
        try {
            if (COPYFILE == 0) {
                // errorDel内保存为异常文件名
                List<String> errorDel =
                    of.deleteFtpFile(ftp, this.m_deleteFile);
            }
            else {
                of.renameFtpFile(this.m_sourcePath, ftp, this.m_floderList,
                    BAKFOLDER, this.m_deleteFile);
            }
        }
        catch (IOException e) {
            if(logger.isDebugEnabled()){
                logger.debug("文件删除或另存为时报错:{}", e.getMessage());
            }
            try {
                ftp.disconnect();
            }
            catch (IOException e1) {
                if(logger.isDebugEnabled()){
                    logger.debug("文件删除或另存为时，断开FTP连接报错:{}", e1.getMessage());
                }
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            m_deleteFile.clear();
            this.m_floderList.clear();
            m_ftpSourceList.addLast(ftp);
        }
    }

    /**
     * 断开目标FTP连接.
     * @param ftp
     */
    private void disConnect(final FTPClient ftp){
        if(ftp == null)
            return;
        if (ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            }
            catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("断开目标端FTP连接报错:{}", e.getMessage());
                }
            }
        }
        
    }
    
    /**
     * 扫描是非法文件时，删除.
     * @param ftp
     * @param fileName
     */
    private void deleteIllegalFile(){
        
        if(this.m_deleteIllegal.size() <= 0)
            return;
        String fileName = null;
        final FTPClient ftp = ftpConn(targetConnParam);
        try {
            for(String illegalFile : m_deleteIllegal){
                fileName = illegalFile;
                boolean bool = ftp.deleteFile(illegalFile);
                if(logger.isInfoEnabled()){
                    logger.info("删除非法文件{}：{}", illegalFile, bool == true ? "成功" : "失败");
                }
            }
        }
        catch (Exception e) {
            if(logger.isDebugEnabled()){
                logger.debug("删除非法文件{}时，发生异常：{}", fileName, e.getMessage());
            }
            e.printStackTrace();
        }finally{
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                }
                catch (IOException e) {
                    if(logger.isDebugEnabled()){
                        logger.debug("文件删除非法文件时，断开FTP连接报错:{}", e.getMessage());
                    }
                }
            }
        }
    }
    
    
    /**
     * 关闭流连接
     * @param input
     * @param out
     * @throws IOException
     */
    private void closeStream(final InputStream input, final OutputStream out) throws IOException{
        
        if(input != null)
            input.close();
        if(out != null)
            out.close();
        
    }
    
    /**
     * 保存文件路径.
     * @param fileName
     */
    private void putFilePath(final String fileName, final String folderName){
        
        synchronized (filesMap) {
            
            List<String> fileList = filesMap.get(folderName);
            if(null == fileList){
                fileList = new ArrayList<String>();
                fileList.add(fileName);
            }else{
                fileList.add(fileName); 
            }
            filesMap.put(folderName, fileList);
        }
        
    }
    

    /**
     * 流程结束，销毁FTP连接.
     */
    public void stop() {
        
        //销毁连接目标FTP服务器连接
        for(final FTPClient ftp : m_ftpTargetList){
            
            try {
                if(ftp.isConnected()){
                    
                    ftp.disconnect();
                }
            }
            catch (IOException e) {
                
                e.printStackTrace();
            }
        }
        
        //销毁连接源FTP服务器连接
        for(final FTPClient ftp : m_ftpSourceList){
            
            try {
                if(ftp.isConnected()){
                    
                    ftp.disconnect();
                }
            }
            catch (IOException e) {
                
                e.printStackTrace();
            }
        }

    }

    
    
    public void setPair(Pair pair) {
        this.pair = pair;
    }


    public void setPump(Pump pump) {
        this.pump = pump;
    }



}
