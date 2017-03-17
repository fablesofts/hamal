/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.data.envelope;

import java.io.InputStream;

/**
 * 文件数据封装
 * 
 * @author xieruidong 2013年11月13日 下午2:32:26
 */
public class FileData {

    private InputStream inputdata;

    private String filename;

    private String dirPath;
    
    private boolean endFileName;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InputStream getInputdata() {
        return inputdata;
    }

    public void setInputdata(InputStream inputdata) {
        this.inputdata = inputdata;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    
    public boolean isEndFileName() {
        return endFileName;
    }

    
    public void setEndFileName(boolean endFileName) {
        this.endFileName = endFileName;
    }

    

    


}
