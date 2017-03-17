/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.config.metadata;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fable.hamal.shuttle.common.model.envelope.data.Source;
import com.fable.hamal.shuttle.common.model.envelope.data.Target;

/**
 * 
 * @author zhangl
 */
public class TimestampTable implements Source, Target {

    private static final long serialVersionUID = 2341528494278199146L;
    
    private String factoryPrefix = null;
    
    private Long taskId;
    
    private Long dataSourceId;
    
    private Db db;
    
    private String sourceTableName = "";
    private String targetTableName = "";
    
    private String timestampColumn = "";
    
    private List<Column> columns =  new LinkedList<Column>();
    
    private Date times = new Date();
    
    private static final String FACTORY_PREFIX_DEFAULT = "sqlTableTimestamp";
    
 //-------------------------------set / get ------------------   
    public Db getDb() {
        return db;
    }

    
    public void setDb(Db db) {
        this.db = db;
    }

    
    public String getSourceTableName() {
        return sourceTableName;
    }

    
    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    
    public String getTargetTableName() {
        return targetTableName;
    }

    
    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }


    public String getTimestampColumn() {
        return timestampColumn;
    }


    
    public void setTimestampColumn(String timestampColumn) {
        this.timestampColumn = timestampColumn;
    }


    
    public List<Column> getColumns() {
        return columns;
    }


    
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }


    
    public Date getTimes() {
        return times;
    }


    
    public void setTimes(Date times) {
        this.times = times;
    }

    
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


    public void setFactoryPrefix(String factoryPrefix) {
        this.factoryPrefix = factoryPrefix;
    }

    @Override
    public String getFactoryPrefix() {
        return (null == factoryPrefix || "".equals(factoryPrefix)) ? FACTORY_PREFIX_DEFAULT : factoryPrefix;
    }
	
}
