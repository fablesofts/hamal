/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.select;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.node.core.trigger.AddFPTableSelector;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.common.model.config.metadata.Column;
import com.fable.hamal.shuttle.common.model.config.metadata.TimestampTable;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 
 * @author xieruidong 2013年11月15日 上午10:09:16
 */
public class SqlTableTimestampSelector extends AbstractSelector implements Selector {

    private static final Logger logger = LoggerFactory.getLogger(AddFPTableSelector.class);
    private volatile boolean running = false;
    private Pump pump;
    private StringBuffer selectSql = new StringBuffer(); 
    private StringBuffer countSql = new StringBuffer(); 
    private List<Date> lastDate = new ArrayList<Date>();
    private DbDialect dbDialect;
    //paging
    private static final int pageSize = 1000;
    private int pagenum = 1; 
    private int count;
    private boolean flag = true;
    private String tableName;
    private String timeColName;
    private Date time ;
    private Date lasttime ;
    private boolean isDM7 = false;
    private String schema ="";
    private Connection conn = null;
    public SqlTableTimestampSelector() {
        
    }
    
    public SqlTableTimestampSelector(Pump pump) {
        this.pump = pump;
    }

    public void start() {
        conn =DataSourceUtils.getConnection(dbDialect.getJdbcTemplate().getDataSource());
        final TimestampTable originTable = (TimestampTable)pump.getSource();
        //接受的是有依赖关系的表
        tableName = originTable.getSourceTableName();
        timeColName = originTable.getTimestampColumn();
        time = originTable.getTimes();
        List<Column> columns = originTable.getColumns();
        if(tableName!=null){
                //默认不传colums过来
                ResultSet rs = null;
                try {
                    DatabaseMetaData databaseMetaData = conn.getMetaData();
                    if (databaseMetaData.storesLowerCaseIdentifiers()) {
                        tableName = tableName.toLowerCase();
                    }
                    if (databaseMetaData.storesUpperCaseIdentifiers() &&
                                    !DbType.DAMENG7.equals(originTable.getDb().getDbType())) {
                        
                        tableName = tableName.toUpperCase();
                    }
                    if(DbType.DAMENG7.equals(originTable.getDb().getDbType())) {
                        isDM7 = true;
                    } 
                    //对schema进行赋值
                    if (DbType.ORACLE.equals(originTable.getDb().getDbType())) {
                        schema = originTable.getDb().getUsername().toUpperCase();
                    } else if(DbType.MSSQL.equals(originTable.getDb().getDbType())) {
                        schema = null;
                    } else {
                        schema = originTable.getDb().getDbName();
                    }
                    rs = databaseMetaData.getColumns(null, null, tableName, null);
                    boolean isColumns = 0 == columns.size();
                    while (rs.next()) {
                        String columnName = rs.getString("COLUMN_NAME");
                        int columnType = rs.getInt("DATA_TYPE");
                        //如果没有表示columns则表示包括全部列名
                        if (isColumns) {
                            Column col = new Column();
                            col.setColumnName(columnName);
                            col.setColumnType(columnType);
                            columns.add(col);
                        } else {
                            for (Column column : columns) {
                                if (column.getColumnName().equalsIgnoreCase(columnName)) {
                                    column.setColumnType(columnType);
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    logger.error("Read columns error the error message is: {}",e.getMessage());
                }
                if (isDM7) {
                    //添加主表中所以需要交换的字段
                      for (Column column : columns) {
                          selectSql.append("\"")
                                   .append(tableName)
                                   .append("\".\"")
                                   .append(column.getColumnName())
                                   .append("\",");
                      }
                      selectSql = selectSql.deleteCharAt(selectSql.length() - 1);

                      //添加增量表的序列和状态字段
                      selectSql.append(" from \"")
                               .append(schema)
                               .append("\".\"")
                               .append(tableName)
                               .append("\" where \"")
                               .append(tableName)
                               .append("\".\"")
                               .append(timeColName)
                               .append("\" >= ?");
                      
                      //查询总条数seql
                      countSql.append("select count(1) from \"")
                              .append(schema)
                              .append("\".\"")
                              .append(tableName)
                              .append("\" where \"")
                              .append(tableName)
                              .append("\".\"")
                              .append(timeColName)
                              .append("\" > ?");
//                              .append(time)
//                              .append("','yyyy-MM-dd hh24:mi:ss')");
                  
                } else {
                  //添加主表中所以需要交换的字段
                    for (Column column : columns) {
                        selectSql.append(tableName)
                                 .append(".")
                                 .append(column.getColumnName())
                                 .append(",");
                    }
                    selectSql = selectSql.deleteCharAt(selectSql.length() - 1);

                    //添加增量表的序列和状态字段
                    selectSql.append(" from ")
                             .append(tableName)
                             .append(" where ")
                             .append(timeColName)
                             .append(" >= ?");
                    
                    //查询总条数seql
                    countSql.append("select count(1) from ")
                            .append(tableName)
                            .append(" where ")
                            .append(timeColName)
                            .append(" > ?");
//                            .append(time)
//                            .append("','yyyy-MM-dd hh24:mi:ss')");
                }
                
                running = true;
                if (logger.isDebugEnabled()) {
                    logger.debug("the sql selectSql is {}",selectSql.toString());
                }
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            logger.error(
                "Connection close error the error message is: {}",
                e.getMessage());
        }
    }

    public boolean isStart() {
        return running;
    }

    public BatchData select() {
        if (logger.isInfoEnabled()) {
            logger.info("TimestampSelector start working~!");
        }
        final TimestampTable originTable = (TimestampTable)pump.getSource();
        RowDataBatch rdb = new RowDataBatch();
        BatchData bd = new BatchData();
        if(pagenum==1) {
            //获取当前这批要传的这张表中符合标准的数据个数
            lasttime = time;
            Object[] date ={lasttime};   
            count = dbDialect.getJdbcTemplate().queryForInt(countSql.toString(),date);
            selectSql = paging(originTable.getDb().getDbType(),selectSql);
        }
        //是否还有数据需要传
        if (flag) {
            if(pagenum*pageSize>count) {
                flag = false;
            } 
            Object[] args = {lasttime};
            List<RowData> rows = dbDialect.getJdbcTemplate().query(selectSql.toString(),args, new RowMapper<RowData>() {
                public RowData mapRow(ResultSet rs, int index) throws SQLException {
                    List<Column> columns = originTable.getColumns();
                    RowData rd = new RowData();
                    int size = columns.size();
                    for (int i = 0;i<size; i++) {
                        CellData cd = new CellData();
                        cd.setColumnIndex(new Long(i));
                        cd.setColumnName(columns.get(i).getColumnName());
                        cd.setColumnType(columns.get(i).getColumnType());
                        if (timeColName.equals(columns.get(i).getColumnName())) {
                            lastDate.add(rs.getTimestamp(i+1));
                        }
                        cd.setColumnValue(rs.getString(i+1));
                        rd.getCellData().add(cd);
                    }
                    rd.setTableName(tableName);
                    return rd;
                }
            });
            rdb.setBatch(rows);
            //将这次传输的最后条数据的time存下来 作为下一批数据的开始time
            lasttime = lastDate.get(lastDate.size()-1);
            rdb.setLastDataDate(lasttime);
            bd.setRdb(rdb);
            pagenum++;
            return bd;
        } else {
                flag = true;
                return null;
        }
    }

    public void stop() {
        
    }

    public void rollback() {
        
    }

    
    
    /**
     * 该方法处理sql分页查询.
     * @param dataType 数据库类型
     * @param sql 未加分页操作的查询语句
     * @return 添加分页后的查询语句
     */
    public StringBuffer paging (String dataType,StringBuffer sql){
        StringBuffer pagingSql = null;
        if(DbType.MSSQL.equalsIgnoreCase(dataType) ||
                        DbType.DAMENG6.equalsIgnoreCase(dataType) ||
                        DbType.DAMENG7.equalsIgnoreCase(dataType)) {
            pagingSql = new StringBuffer("select top ");
            pagingSql.append(pageSize);
        } else {
            pagingSql =  new StringBuffer("select ");
        }
        pagingSql.append(sql);
        if(DbType.ORACLE.equalsIgnoreCase(dataType)){
            pagingSql.append(" and  rownum<=")
                     .append(pageSize)
                     .append(" and rownum>=1");
        }
        pagingSql.append(" order by ")
                 .append(timeColName);
        
        if (DbType.MYSQL.equalsIgnoreCase(dataType)){
            pagingSql.append(" limit 0,")
                     .append(pageSize);
        }
        
        return pagingSql;
    }
    
    //----------------------------------------------------setter&&getter-----------------------------------------------
    public boolean isrunning() {
        return running;
    }

    public void setrunning(boolean running) {
        this.running = running;
    }



    public Pump getPump() {
        return pump;
    }

    
    public StringBuffer getSelectSql() {
        return selectSql;
    }

    
    public void setSelectSql(StringBuffer selectSql) {
        this.selectSql = selectSql;
    }

    public void setPump(Pump pump) {
        this.pump = pump;
    }

    public DbDialect getDbDialect() {
        return dbDialect;
    }

    public void setDbDialect(DbDialect dbDialect) {
        this.dbDialect = dbDialect;
    }
}
