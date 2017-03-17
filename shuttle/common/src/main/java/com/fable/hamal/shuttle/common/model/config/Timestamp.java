package com.fable.hamal.shuttle.common.model.config;

import java.io.Serializable;
import java.util.Date;


public class Timestamp implements Serializable {

    private static final long serialVersionUID = -1853047923588526986L;
    
    private Long taskId;
    
    private Long dataSourceId;
    
    private String tableName;
    
    private Date switchTime;
    
    private String dataColumn;

    
    public Long getTaskId() {
        return taskId;
    }

    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    
    public Long getDataSourceId() {
        return dataSourceId;
    }

    
    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    
    public String getTableName() {
        return tableName;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    
    public Date getSwitchTime() {
        return switchTime;
    }

    
    public void setSwitchTime(Date switchTime) {
        this.switchTime = switchTime;
    }


    public String getDataColumn() {
        return dataColumn;
    }

    
    public void setDataColumn(String dataColumn) {
        this.dataColumn = dataColumn;
    }
    
    
}
