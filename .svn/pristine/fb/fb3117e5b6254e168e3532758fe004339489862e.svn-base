package com.fable.hamal.shuttle.common.syslog.intf;

import java.sql.SQLException;


public interface SyslogIntf {
    
    /**
     * 记录交换时抽取的条数
     * @param count 抽取的条数
     * @param flag 抽取状态 0：成功，1：失败
     * @throws SQLException 
     */
    public void countSelect(int count,int flag) throws SQLException;
    
    /**
     * 记录交换时加载的条数
     * @param count 加载的条数   
     * @param flag 加载状态 0：成功，1：失败
     */
    public void countLoad(int count,int flag);
}
