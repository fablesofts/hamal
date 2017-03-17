package com.fable.hamal.log.service.impl;

import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fable.hamal.shuttle.common.syslog.SysLogId;
import com.fable.hamal.shuttle.common.syslog.intf.SyslogIntf;


public class SysLogImpl implements SyslogIntf {
    
    private JdbcTemplate jdbcTemplate;
    
    private String insertSql = "insert into SYS_LOG (ID,TASK_ID,SELECT_COUNT,SELECT_DATA," +
        "LOAD_COUNT,LOAD_DATA,OPERATION_RESULTS,START_TIME,END_TIME) " +
        "values(?,?,?,?,?,?,?,?,?)";
    
    private String updateSql = "update SYS_LOG set LOAD_COUNT= ? , " +
        "OPERATION_RESULTS = ? , END_TIME= ? where ID = ?";
    
    @Override
    public void countSelect(int count, int flag) {
        Long id = getSequence();
        Long taskid = SysLogId.getSysLogId().get("taskId");
        SysLogId.setSysLogId(taskid, id);
        Date startDate = new Date();
        Object[] args = {id,taskid,count,"","","",flag,startDate,""};
        this.getJdbcTemplate().update(insertSql,args);
    }

    @Override
    public void countLoad(int count, int flag) {
        Long id = SysLogId.getSysLogId().get("sysLogId");
        Date endDate = new Date();
        Object[] args = {count,flag,endDate,id};
        this.getJdbcTemplate().update(updateSql,args);
    }

    public Long getSequence(){
        String sql = "select SYSLOGID_SEQ.nextval from dual";
        Long id = this.getJdbcTemplate().queryForLong(sql);
        return id;
    }

    
    public JdbcTemplate getJdbcTemplate() {
        ApplicationContext ac = new FileSystemXmlApplicationContext("E:\\workspace1\\hamal\\node\\core\\src\\main\\resources\\spring\\hamal.node.core-factory.xml"); 
        return (JdbcTemplate)ac.getBean("jdbcTemplate");
    }

    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
}
