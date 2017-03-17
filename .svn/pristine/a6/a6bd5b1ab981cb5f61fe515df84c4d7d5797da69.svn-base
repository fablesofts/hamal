/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.Assert;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.model.config.metadata.Column;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.utils.constant.DbType;
import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.node.common.db.utils.PreparedStatementCreatorUtils;
import com.fable.hamal.node.common.db.utils.SqlUtils;
/**
 * @author xieruidong 2013年11月6日 下午4:28:03
 */
public class SqlTableLoader extends AbstractLoader implements Loader {

	private static final Logger logger	= LoggerFactory.getLogger(SqlTableLoader.class);
	private volatile boolean ruuning = false;
	private Pump pump;
	private Pair pair;
	private List<Pair> pairs;
	private List<DbDialect> dbDialect;
	private List<String> sql = new ArrayList<String>();
	private String dbType;
	
	public void start() {
		if (!ruuning) {
		    pairs = pump.getPairs();
		    if(null != pairs){
		        
		        for(int z=0;z<pairs.size();z++){
		            
		            final Table targetTable = (Table)pairs.get(z).getTarget();
		            List<Column> columns = targetTable.getColumns();
		            ResultSet rs = null;
		            try {
		                DatabaseMetaData databaseMetaData = dbDialect.get(z).getJdbcTemplate().getDataSource().getConnection().getMetaData();
		                Assert.notNull(databaseMetaData);
		                String tableName = targetTable.getTableName();
		                Assert.notNull(tableName);
		                
		                if (databaseMetaData.storesLowerCaseIdentifiers()) {
		                    tableName = tableName.toLowerCase();
		                }
		                
		                if (databaseMetaData.storesUpperCaseIdentifiers()) {
		                    tableName = tableName.toUpperCase();
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
		            sql.append(targetTable.getTableName()).append("(");
		            for (Column column : targetTable.getColumns()) {
		                sql.append(column.getName()).append(",");
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
		            ruuning = true;
		        }
		        
		    }
		}
	}
	
	public void load(final BatchData data) {
		String oldThreadName = Thread.currentThread().getName();
		StringBuffer currentName = new StringBuffer(pump.getJobId().toString());
		currentName.append(BAR_LINE).append(pump.getPipelineId()).append(BAR_LINE).append(pump.getId())
		.append(BAR_LINE).append(pump.getSerial()).append(BAR_LINE).append(LOADER);
		Thread.currentThread().setName(currentName.toString());
		
		for(int i=0;i<pairs.size();i++){
		    
		    dbDialect.get(i).getJdbcTemplate().batchUpdate(sql.get(i), new BatchPreparedStatementSetter(){
		        
		        public void setValues(PreparedStatement ps, int i) throws SQLException {
		            dealPreparedStatement(ps, data.getRdb().getBatch().get(i));
		        }
		        
		        public int getBatchSize() {
		            return data.getRdb().getBatch().size();
		        }
		    });
		    Thread.currentThread().setName(oldThreadName);
		}
	}

	public void setPump(Pump pump) {
		this.pump = pump;
	}
	
	private void dealPreparedStatement(PreparedStatement ps, RowData rd) throws SQLException {
		//mark for：insert/update/delete for triggers
		List<CellData> cells = new ArrayList<CellData>();
        List<Column> columns = ((Table)pair.getTarget()).getColumns();
		for(Column col : columns) {
		    for (CellData cd: rd.getCellData()) {
                if (cd.getColumnName().equalsIgnoreCase((col.getColumnName()))) {
                    cells.add(cd);
                }
            }
		}
		
//		cells.addAll(rd.getCellData());
		
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
                        	PreparedStatementCreatorUtils.setParameterValue(ps, paramIndex, sqlType, null, param);
                        }
                        break;
                    case Types.BIT:
                        // 只处理mysql的bit类型，bit最多存储64位，所以需要使用BigInteger进行处理才能不丢精度,mysql driver将bit按照setInt进行处理，会导致数据越界
                        if (DbType.MYSQL.equals(this.dbType)) {
                        	PreparedStatementCreatorUtils.setParameterValue(ps, paramIndex, Types.DECIMAL, null, param);
                        } else {
                        	PreparedStatementCreatorUtils.setParameterValue(ps, paramIndex, sqlType, null, param);
                        }
                        break;
                    default:
                    	PreparedStatementCreatorUtils.setParameterValue(ps, paramIndex, sqlType, null, param);
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
}