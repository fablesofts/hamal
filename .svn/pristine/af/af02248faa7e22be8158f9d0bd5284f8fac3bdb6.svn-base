package com.fable.hamal.node.core.select;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.preprocess.file.ftp.FileHeadFilter;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.FileData;
import com.fable.hamal.shuttle.common.data.envelope.FileDataBatch;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.config.metadata.File;
import com.fable.hamal.shuttle.common.model.config.metadata.Ftp;

/**
 * FTP抽取.
 * 
 * @author Administrator
 */
public class FTPSelector extends AbstractSelector implements Selector {

    private final static Logger logger = LoggerFactory
        .getLogger(FTPSelector.class);

    private LinkedList<String> m_fileList = new LinkedList<String>();
    private LinkedList<FTPClient> m_ftpList = new LinkedList<FTPClient>();
    private List<String> m_floderList = new ArrayList<String>();

    private static final int COUNT = 20;
    private static final int ISDIRECTORY = 1; // 0 ：抽取根目录 ；1 ：抽取子文件夹
    private static final int HEADFILTER = 1; // 是否需要过滤文件头。需要：0； 不需要：1；
    public static final int HEAD_RESULT_OFFICE = 0;
    public static final int HEAD_RESULT_IMG = 1;
    public static final int HEAD_RESULT_XML = 2;
    public static final int HEAD_RESULT_RAR = 3;
    public static final int HEAD_RESULT_ZIP = 4;
    public static final int HEAD_RESULT_EXE = 5;

    private static final String SEPARATOR = "/";
    private static final String BAKFOLDER = "/sjjhBak/";

    private boolean m_running = false;
    private Pump m_pump;
    private String m_username = null;
    private String m_password = null;
    private int m_port = 21;
    private String m_ip = null;
    private String m_ftpPath = "/";
    private String m_setinfo;
    private int m_count = 0;

    /**
     * 无参构造方法.
     */
    public FTPSelector() {

    }

    /**
     * 构造方法pump.
     * 
     * @param pump
     */
    public FTPSelector(Pump pump) {
        this.m_pump = pump;
    }

    /**
     * 启动状态.
     * 
     * @return boolean
     */
    public boolean isStart() {
        return this.m_running;
    }


    public void rollback() {

    }

    /**
     * FTP抽取主方法.
     * 
     * @return BatchData
     */
    public BatchData select() {
        BatchData batchData = null;
        if(logger.isInfoEnabled()){
            logger.info("文件数量：" + this.m_fileList.size());
        }
        if (this.m_fileList.size() <= 0) {
            disConnect();
            if (logger.isInfoEnabled()) {
                logger.info("FTP抽取端，任务结束！！");
            }
            return batchData = null;
        }
        try {
            List<FileData> fileDataList = new ArrayList<FileData>();
            InputStream in = null;

            for (int i = 0; i < COUNT; i++) {
                String fileName = null;
                synchronized (this.m_fileList) {
                    if (this.m_fileList.size() > 0) {
                        fileName = this.m_fileList.removeFirst();
                    }
                }
                if (fileName == null)
                    break;
                FTPClient ftp = null;
                synchronized (this.m_ftpList) {
                    if (this.m_ftpList.size() > 0)
                        ftp = this.m_ftpList.removeFirst();
                    if (ftp == null)
                        ftp = this.ftpConn();
                }
                System.out.println(fileName);
                in = ftp.retrieveFileStream(fileName);
                if (in == null) {
                    if (logger.isInfoEnabled()) {
                        logger.info("抽取文件，{}获取文件流为NULL", fileName);
                    }
                }
                InputStream ain = null;

                // 是否过滤文件头
                if (HEADFILTER == 0) {
                    final PushbackInputStream pIn =
                        FileHeadFilter.headFilter(in, fileName,
                            HEAD_RESULT_IMG);
                    if (pIn == null) {
                        continue;
                    }
                    else {
                        ain = new AutoCloseInputStream(pIn, ftp, fileName);
                    }
                }
                else {
                    if (in != null)
                        ain = new AutoCloseInputStream(in, ftp, fileName);
                }

                FileData fd = new FileData();
                fd.setInputdata(ain);
                fd.setFilename(replaceSeparator(fileName));
                String dirPath =
                    fileName
                        .replace(fileName.split(SEPARATOR)[fileName
                            .split(SEPARATOR).length - 1], "");
                m_floderList.add(dirPath);
                fd.setDirPath(dirPath);
                fileDataList.add(fd);

            }
            FileDataBatch fdb = new FileDataBatch();
            fdb.setList(fileDataList);
            fdb.setSourcePath(m_ftpPath);
            batchData = new BatchData();
            batchData.setFdb(fdb);
        }
        catch (IOException e) {
            logger.debug("获取文件流失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return batchData;
    }


    /**
     * 任务启动.
     */
    public void start() {
        File file = (File)this.m_pump.getSource();
        Ftp ftp = file.getFtp();
        this.m_ftpPath = file.getPath();
        this.m_ip = ftp.getServerIp();
        this.m_port = ftp.getPort();
        this.m_username = ftp.getUsername();
        this.m_password = ftp.getPassword();

        
        try {
            getFiles();
        }
        catch (IOException e1) {

            e1.printStackTrace();
        }
        this.m_running = true;
        if (logger.isInfoEnabled()) {
            logger.info("FTP抽取任务启动！");
        }

    }

    /**
     * 获取所有抽取的文件.
     * 
     * @throws IOException
     */
    private void getFiles() throws IOException {
        final FTPClient ftp = this.ftpConn();
        if (ISDIRECTORY == 0) {
            try {
                final FTPFile[] ftpfiles = ftp.listFiles(this.m_ftpPath);
                for (final FTPFile ftpFile : ftpfiles) {
                    if (!ftpFile.isDirectory()) {
                        this.m_fileList
                            .add(replaceSeparator((this.m_ftpPath +
                                SEPARATOR + ftpFile.getName())));
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            getDirFiles(this.m_ftpPath, ftp, BAKFOLDER);
        }
        
        synchronized (FTPSelector.this.m_ftpList) {
            FTPSelector.this.m_ftpList.addLast(ftp);
        }
    }

    /**
     * 获取子文件夹下的文件.
     * 
     * @param path
     * @param ftp
     * @throws IOException
     */
    private void getDirFiles(String path, FTPClient ftp, String bakFloder)
        throws IOException {
        if (ftp == null) {
            ftp = this.ftpConn();
        }
        m_count++;
        final FTPFile[] ftpfiles = ftp.listFiles(path);
        if (ftpfiles == null)
            return;
        for (final FTPFile ftpFile : ftpfiles) {
            if (ftpFile.isFile()) {
                if (!ftpFile.getName().contains("Thumbs.db")) {

                    this.m_fileList.add(replaceSeparator(path + SEPARATOR +
                        ftpFile.getName())); 
                }
            }
            else if (ftpFile.isDirectory() &&
                !ftpFile.getName().endsWith(".") &&
                !ftpFile.getName().contains(
                    bakFloder.replace(SEPARATOR, "").trim()))
                this.getDirFiles(
                    replaceSeparator(path + SEPARATOR + ftpFile.getName()),
                    ftp, bakFloder);
        }
    }

    /**
     * 创建ftp连接.
     * 
     * @return
     */
    private FTPClient ftpConn() {

        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.setDataTimeout(120000 * 1000);
            ftpClient.setDefaultTimeout(12000 * 1000);
            ftpClient.connect(this.m_ip, this.m_port);
            ftpClient.login(this.m_username, this.m_password);
            ftpClient.pasv();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            
//            StringBuffer sb = new StringBuffer();
//            sb.append("源端数据源参数：IP：").append(this.m_ip).append(" 端口:")
//                .append(this.m_port).append(" 用户名：").append(this.m_username)
//                .append(" 密码：").append(this.m_password).append("  路径：").append(this.m_ftpPath);
//            this.m_setinfo = sb.toString(); //保存连接FTP参数字符串
            
        }
        catch (IOException e) {
           
            if (logger.isDebugEnabled()) {
                logger.debug("文件抽取时，连接FTP服务器出现异常：{} {}", e.getMessage(), this.m_setinfo);
            }
            e.printStackTrace();
        }

        return ftpClient;
    }

    /**
     * 断开源端FTP连接.
     * 
     * @param ftp
     */
    private void disConnect() {
        try {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        for (FTPClient ftp : this.m_ftpList) {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                }
                catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("断开源端FTP连接异常:{}", e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 格式化分割线.
     * 
     * @param path
     * @return
     */
    private String replaceSeparator(String path) {
        return path.replace("//", "/");
    }

    public void stop() {


    }



    /**
     * 封装的InputStream,用于关闭FTPClinet.
     * 
     * @author Administrator
     */
    public class AutoCloseInputStream extends InputStream {

        private InputStream m_in;
        private FTPClient m_ftp;

        /**
         * Constructs a AutoCloseInputStream.
         * 
         * @param in
         * @param ftp
         * @param fileFullPath
         */
        private AutoCloseInputStream(final InputStream in,
            final FTPClient ftp, final String fullPath) {
            super();
            if (in == null)
                throw new NullPointerException("InputStream in == null");
            this.m_in = in;
            this.m_ftp = ftp;
        }

        /**
         * available.
         * 
         * @return
         * @throws IOException
         * @see java.io.InputStream#available()
         */
        @Override
        public int available() throws IOException {
            return this.m_in.available();
        }

        /**
         * close.
         * 
         * @throws IOException
         * @see java.io.InputStream#close()
         */
        @Override
        public void close() throws IOException {
            try {
                this.m_in.close();
            }
            catch (final Exception e) {}
            synchronized (FTPSelector.this.m_ftpList) {
                if (this.m_ftp.completePendingCommand())
                    FTPSelector.this.m_ftpList.addLast(this.m_ftp);
            }
        }

        /**
         * equals.
         * 
         * @param obj
         * @return
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            return this.m_in.equals(obj);
        }

        /**
         * hashCode.
         * 
         * @return
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.m_in.hashCode();
        }

        /**
         * mark.
         * 
         * @param readlimit
         * @see java.io.InputStream#mark(int)
         */
        @Override
        public void mark(final int readlimit) {
            this.m_in.mark(readlimit);
        }

        /**
         * markSupported.
         * 
         * @return
         * @see java.io.InputStream#markSupported()
         */
        @Override
        public boolean markSupported() {
            return this.m_in.markSupported();
        }

        /**
         * read.
         * 
         * @return
         * @throws IOException
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() throws IOException {
            return this.m_in.read();
        }

        /**
         * read.
         * 
         * @param b
         * @param off
         * @param len
         * @return
         * @throws IOException
         * @see java.io.InputStream#read(byte[], int, int)
         */
        @Override
        public int read(final byte[] b, final int off, final int len)
            throws IOException {
            return this.m_in.read(b, off, len);
        }

        /**
         * read.
         * 
         * @param b
         * @return
         * @throws IOException
         * @see java.io.InputStream#read(byte[])
         */
        @Override
        public int read(final byte[] b) throws IOException {
            return this.m_in.read(b);
        }

        /**
         * reset.
         * 
         * @throws IOException
         * @see java.io.InputStream#reset()
         */
        @Override
        public void reset() throws IOException {
            try {
                this.m_in.close();
            }
            catch (final Exception e) {}
            if (this.m_in == null)
                throw new IOException("RESET ERROR. RESULT == NULL");
        }

        /**
         * skip.
         * 
         * @param n
         * @return
         * @throws IOException
         * @see java.io.InputStream#skip(long)
         */
        @Override
        public long skip(final long n) throws IOException {
            return this.m_in.skip(n);
        }

        /**
         * toString.
         * 
         * @return
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.m_in.toString();
        }
    }



    public void setPump(Pump pump) {
        this.m_pump = pump;
    }


}
