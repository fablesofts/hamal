package com.fable.hamal.log.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.syslog.intf.RowLevelLogIntf;


public class RowLevelLogImpl implements RowLevelLogIntf {

    private JdbcTemplate jdbcTemplate;
    
    private String sql = "insert into ROW_LEVEL_LOG (ID,SYS_LOG_DETAIL_ID,OPERATION_DATA) values(?,?,?)";
    
    @Override
    public void selectDate(BatchData batchData,Long sysLogDetailId) {
        for(RowData rowData : batchData.getRdb().getBatch()) {
            String row = getRowData(rowData);
            Long id = getSequence(); 
            Object[] args = {id,sysLogDetailId,row};
            this.getJdbcTemplate().update(sql, args);
        }
    }

    @Override
    public void insertDate(RowData rowData,Long sysLogDetailId) {
        String rowdate = getRowData(rowData);
        Long id = getSequence(); 
        Object[] args = {id,sysLogDetailId,rowdate};
        this.getJdbcTemplate().update(sql, args);
    }

    @Override
    public void updateDate(RowData rowData,Long sysLogDetailId) {
        String rowdate = getRowData(rowData);
        Long id = getSequence(); 
        Object[] args = {id,sysLogDetailId,rowdate};
        this.getJdbcTemplate().update(sql, args);

    }

    @Override
    public void deleteDate(RowData rowData,Long sysLogDetailId) {
        String rowdate = getRowData(rowData);
        Long id = getSequence(); 
        Object[] args = {id,sysLogDetailId,rowdate};
        this.getJdbcTemplate().update(sql, args);

    }

    @Override
    public void filterDate(BatchData batchData,Long sysLogDetailId) {
        for(RowData rowData : batchData.getRdb().getBatch()) {
            String row = getRowData(rowData);
            Long id = getSequence(); 
            Object[] args = {id,sysLogDetailId,row};
            this.getJdbcTemplate().update(sql, args);
        }

    }

    
    public String getRowData(RowData rowData) {
        StringBuffer row = new StringBuffer();
        if(null != rowData) {
            row.append("tableName : ")
               .append(rowData.getTableName())
               .append(", ");
            for(CellData cellDate : rowData.getCellData()) {
                row.append("columnName : ")
                   .append(cellDate.getColumnName())
                   .append(" ,columnValue : ")
                   .append(cellDate.getColumnValue())
                   .append(" , ");
            }
            row.deleteCharAt(row.length() - 2);
        }
        return row.toString();
    }
    
    
    
    public JdbcTemplate getJdbcTemplate() {
        ApplicationContext ac = new FileSystemXmlApplicationContext("E:\\workspace1\\hamal\\node\\core\\src\\main\\resources\\spring\\hamal.node.core-factory.xml"); 
        return (JdbcTemplate)ac.getBean("jdbcTemplate");
    }

    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Long getSequence(){
        String sql = "select ROWLEVELLOG_SEQ.nextval from dual";
        Long id = this.getJdbcTemplate().queryForLong(sql);
        return id;
    }
}
