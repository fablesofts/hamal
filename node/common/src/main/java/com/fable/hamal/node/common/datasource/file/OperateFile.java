package com.fable.hamal.node.common.datasource.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FTP抽取的后续操作
 * 
 * @author Administrator
 */
public class OperateFile {

    private final static Logger logger = LoggerFactory.getLogger(OperateFile.class);
    private static final String SEPARATOR = "/";

    /**
     * FTP抽取后删除文件.
     * @param ftp
     * @param dir
     * @param fileNameList
     * @return
     * @throws IOException
     */
    public List<String> deleteFtpFile(final FTPClient ftp, final List<String> deleteFileList)
        throws IOException {
        final List<String> deleteError = new ArrayList<String>();
        if (deleteFileList.isEmpty())
            return deleteError;
        synchronized (deleteFileList) {
            for (final String url : deleteFileList) {
                try {
                    if (!ftp.deleteFile(replaceSeparator(url)))
                        deleteError.add("Delete failed " + url);
                }
                catch (Exception e) {
                    deleteError.add("Delete failed " + url);
                }
            }
        }
        return deleteError;  
    }

    /**
     * FTP抽取后另存为文件.
     * @param ftp
     * @param fileNameList
     * @return
     * @throws IOException
     */
    public void renameFtpFile(final String ftpPath, final FTPClient ftp,
        final List<String> floderFile, String bakFloder, final List<String> deleteFileList) {

        for (final String url : floderFile) {
            final StringBuffer sb = new StringBuffer();
            for (final String temp : replaceSeparator(
                ftpPath + bakFloder + url).split(SEPARATOR)) {
                try {
                    if ("".equals(temp) || SEPARATOR.equals(temp))
                        continue;
                    sb.append(SEPARATOR).append(temp);
                    ftp.makeDirectory(sb.toString());
                }
                catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("另存为时，创建文件夹{}发生异常:{}", sb.toString(),e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        }

        synchronized (deleteFileList) {
            for (final String path : deleteFileList) {
                try {
                    String newPath = replaceSeparator(ftpPath + bakFloder + path);
                    //每次另存为时，都先删除备份文件夹里的文件
                    boolean bool = ftp.deleteFile(newPath);
                    if(logger.isInfoEnabled()){
                        logger.info("另存为时，删除同名文件{}", bool ? "成功" : "失败");
                    }
                    bool = ftp.rename(path, newPath);
                    
                        if (logger.isInfoEnabled()) {
                            logger.info("另存为文件{}", bool == true ? "成功" : "失败");
                        }
                        
                }
                catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("文件{}另存为时发生异常:{}", path, e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    private String replaceSeparator(String path) {
        return path.replace("//", "/");
    }
}
