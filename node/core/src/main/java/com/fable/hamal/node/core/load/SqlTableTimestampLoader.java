/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.model.config.metadata.Column;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.config.metadata.TimestampTable;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.utils.constant.DbType;
import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.node.common.db.utils.SqlUtils;
/**
 * @author xieruidong 2013年11月6日 下午4:28:03
 */
public class SqlTableTimestampLoader extends AbstractLoader implements Loader {

	private static final Logger logger	= LoggerFactory.getLogger(SqlTableTimestampLoader.class);
	private volatile boolean running = false;
	private Pump pump;
	private Pair pair;
	private List<Pair> pairs = new ArrayList<Pair>();
	private List<DbDialect> dbDialect;
	private List<String> sql = new ArrayList<String>();
	private String dbType;
	private JdbcTemplate jdbcTemplate;
    private boolean targetIsDM7 = false;
    private String schema ="";
    private Connection conn = null;
    
    public void start() {
		if (!running) {
		    
		    pairs = pump.getPairs();
		    for(int z=0;z<pairs.size();z++){
		        
		        final TimestampTable targetTable = (TimestampTable)pairs.get(z).getTarget();
	            List<Column> columns = targetTable.getColumns();
	            ResultSet rs = null;
	            String tableName = "";
	            try {
	                conn =DataSourceUtils.getConnection(dbDialect.get(z).getJdbcTemplate().getDataSource());
	                DatabaseMetaData databaseMetaData = conn.getMetaData();
	                Assert.notNull(databaseMetaData);
	                tableName = targetTable.getTargetTableName();
	                Assert.notNull(tableName);
	                
	                if (databaseMetaData.storesLowerCaseIdentifiers()) {
	                    tableName = tableName.toLowerCase();
	                }
	                
	                if (databaseMetaData.storesUpperCaseIdentifiers() && 
	                                !DbType.DAMENG7.equals(targetTable.getDb().getDbType())) {
	                    tableName = tableName.toUpperCase();
	                }
	                if(DbType.DAMENG7.equals(targetTable.getDb().getDbType())) {
	                    targetIsDM7 = true;
	                } 
	                //对schema进行赋值
	                if (DbType.ORACLE.equals(targetTable.getDb().getDbType())) {
	                    schema = targetTable.getDb().getUsername().toUpperCase();
	                } else if(DbType.MSSQL.equals(targetTable.getDb().getDbType())) {
	                    schema = null;
	                } else {
	                    schema = targetTable.getDb().getDbName();
	                }
	                rs = databaseMetaData.getColumns(null, null, tableName, null);
	                boolean flag = 0 == columns.size();
	                while (rs.next()) {
	                    String columnName = rs.getString("COLUMN_NAME");
	                    int columnType = rs.getInt("DATA_TYPE");
	                    //如果没有表示columns则表示包括全部列名
	                    if (flag) {
	                        Column col = new Column();
	                        col.setName(columnName);
	                        col.setColumnName(columnName);
	                        col.setColumnType(columnType);
	                        columns.add(col);
	                    } else {
	                        for (Column column : columns) {
	                            if (column.getName().equalsIgnoreCase(columnName)) {
	                                column.setColumnType(columnType);
	                            }
	                        }
	                    }
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	                logger.error(e.getMessage());
	            }
	            
	            StringBuffer sql = new StringBuffer("insert into ");
	            if (targetIsDM7) {
	                sql.append("\"")
	                   .append(schema)
	                   .append("\".\"")
	                   .append(tableName)
	                   .append("\" (");
	            } else {
	                sql.append(tableName)
	                   .append("(");
	            }
	            for (Column column : targetTable.getColumns()) {
	                if (targetIsDM7) {
	                    sql.append("\"")
	                       .append(column.getColumnName())
	                       .append("\",");
	                } else {
	                    sql.append(column.getColumnName()).append(","); 
	                }
	            }
	            sql.deleteCharAt(sql.length() - 1);
	            sql.append(")values(");
	            int size = targetTable.getColumns().size();
	            for (int i = 0; i < size; i++) {
	                sql.append("?,");
	            }
	            sql.deleteCharAt(sql.length() - 1);
	            sql.append(")");
	            this.sql.add(sql.toString());
	            this.dbType = targetTable.getDb().getDbType();
	            
	            if (logger.isDebugEnabled()) {
	                logger.debug(sql.toString());
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
	            
	            running = true;
		        
		    }
		}
	}
	
	public void load(final BatchData data) {
		
	    for(int z=0;z<pairs.size();z++){
	        try {
	            dbDialect.get(z).getJdbcTemplate().batchUpdate(sql.get(z), new BatchPreparedStatementSetter(){

	                public void setValues(PreparedStatement ps, int i) throws SQLException {
	                    dealPreparedStatement(ps, data.getRdb().getBatch().get(i));
	                }

	                public int getBatchSize() {
	                    return data.getRdb().getBatch().size();
	                }
	            });
	        }
	        catch (DataAccessException e) {
	            e.printStackTrace();
	            try {
	                throw new Exception("执行加载时间戳数据库时失败！");
	            }
	            catch (Exception e1) {
	                e1.printStackTrace();
	            } 
	        }
	    }
	    //当一批数据加载完成后  修改时间戳表中时间
        Date lastDate = data.getRdb().getLastDataDate();
        Long taskId = ((TimestampTable)pair.getTarget()).getTaskId();
        Long dataSourceId = ((TimestampTable)pair.getTarget()).getDataSourceId();
        String sourceTableName = ((TimestampTable)pair.getTarget()).getSourceTableName();
        Object[] args = {lastDate,taskId,dataSourceId,sourceTableName};
        jdbcTemplate.update("update fable_switch_timestamp set SWITCH_TIME = ? " +
                        "where TASK_ID = ? and DATA_SOURCE_ID = ? and TABLE_NAME = ?",args);
	}

	public void setPump(Pump pump) {
		this.pump = pump;
	}
	
	private void dealPreparedStatement(PreparedStatement ps, RowData rd) throws SQLException {
		//mark for：insert/update/delete for triggers
		List<CellData> cells = new ArrayList<CellData>();
		cells.addAll(rd.getCellData());
		
		for (int i = 0; i < cells.size(); i++) {
            int paramIndex = i + 1;
            CellData cell = cells.get(i);
            int sqlType = cell.getColumnType();

//            Boolean isRequired = isRequiredMap.get(StringUtils.lowerCase(column.getColumnName()));
//            if (isRequired == null) {
//                throw new LoadException(String.format("column name %s is not found in Table[%s]",
//                    column.getColumnName(),
//                    table.toString()));
//            }

            boolean isEmptyStringNulled = false;
            if (DbType.MYSQL.equals(this.dbType)) {
            	isEmptyStringNulled = false;
            }
            if (DbType.ORACLE.equals(this.dbType)) {
            	isEmptyStringNulled = true;
            }
            
            
            Object param = SqlUtils.stringToSqlValue(cell.getColumnValue(), sqlType, true, isEmptyStringNulled);
            try {
                switch (sqlType) {
                    case Types.TIME:
                    case Types.TIMESTAMP:
                    case Types.DATE:
                        // 只处理mysql的时间类型，oracle的进行转化处理
                        if (DbType.MYSQL.equals(this.dbType)) {
                            // 解决mysql的0000-00-00 00:00:00问题，直接依赖mysql,driver进行处理，如果转化为Timestamp会出错
                            ps.setObject(paramIndex, cell.getColumnValue());
                        } else {
                            StatementCreatorUtils.setParameterValue(ps, paramIndex, sqlType, null, param);
                        }
                        break;
                    case Types.BIT:
                        // 只处理mysql的bit类型，bit最多存储64位，所以需要使用BigInteger进行处理才能不丢精度,mysql driver将bit按照setInt进行处理，会导致数据越界
                        if (DbType.MYSQL.equals(this.dbType)) {
                            StatementCreatorUtils.setParameterValue(ps, paramIndex, Types.DECIMAL, null, param);
                        } else {
                            StatementCreatorUtils.setParameterValue(ps, paramIndex, sqlType, null, param);
                        }
                        break;
                    default:
                        StatementCreatorUtils.setParameterValue(ps, paramIndex, sqlType, null, param);
                        break;
                }
            } catch (SQLException ex) {
            	ex.printStackTrace();
            	if (logger.isErrorEnabled()) {
            		logger.error(ex.getMessage());
            	}
                throw ex;
            }
        }
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}
	
    public List<DbDialect> getDbDialect() {
        return dbDialect;
    }
    
    public void setDbDialect(List<DbDialect> dbDialect) {
        this.dbDialect = dbDialect;
    }

    public Pump getPump() {
		return pump;
	}
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
