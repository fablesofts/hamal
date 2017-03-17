package com.fable.hamal.node.core.preprocess.file.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.common.datasource.file.HeadFilter;

/**
 * 过滤文件头.
 * @author Administrator
 */
public class FileHeadFilter {
    
    private final static Logger logger = LoggerFactory.getLogger(FileHeadFilter.class);
    
    public static PushbackInputStream headFilter(final InputStream in , final String fileName, final int filterType){
        
        final PushbackInputStream pIn = new PushbackInputStream(in, 512);
        try {
            final int iHeadType = HeadFilter.checkHead(pIn);
            if(iHeadType == filterType){
                return pIn;
            }
        }
        catch (IOException e) {
            if(logger.isDebugEnabled()){
                logger.debug("{}文件，分析文件头发生异常：{}", fileName, e.getMessage());
            }
            e.printStackTrace();
        }
        
        return null;
    }
}
