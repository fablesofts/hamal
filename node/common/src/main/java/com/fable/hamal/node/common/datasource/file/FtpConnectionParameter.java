/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.datasource.file;

/**
 * 
 * @author xieruidong 2013年12月24日 上午10:44:19
 */
public class FtpConnectionParameter {
    
    private String ip;
    
    private int port;
    
    private String username;
    
    private String password;
    
    private String rootPath;

    
    public String getIp() {
        return ip;
    }

    
    public void setIp(String ip) {
        this.ip = ip;
    }

    
    public int getPort() {
        return port;
    }

    
    public void setPort(int port) {
        this.port = port;
    }

    
    public String getUsername() {
        return username;
    }

    
    public void setUsername(String username) {
        this.username = username;
    }

    
    public String getPassword() {
        return password;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }

    
    public String getRootPath() {
        return rootPath;
    }

    
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    
}
