/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.select;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.common.model.config.metadata.Column;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 数据库表的Selector
 * @author xieruidong 2013年11月5日 下午4:52:54
 */
public class SqlTableSelector extends AbstractSelector implements Selector {

	private static final Logger logger = LoggerFactory.getLogger(SqlTableSelector.class);
	private volatile boolean ruuning = false;
	private Pump pump;
	private StringBuffer selectSql = new StringBuffer();
	private StringBuffer selectCountSql = new StringBuffer("select count(1) from ");
	private DbDialect dbDialect;
	private List<String> columnNameList = new ArrayList<String>();;
	
	//分页处理to do
    private static final int pageSize = 1000;
    private int pagenum = 1; 
	
	public SqlTableSelector() {
		
	} 
	
	public SqlTableSelector(Pump pump) {
		this.pump = pump;
	}

	public void start() {
		final Table originTable = (Table)pump.getSource(); 
		List<Column> columns = originTable.getColumns();
		ResultSet rs = null;
		try {
			DatabaseMetaData databaseMetaData = dbDialect.getJdbcTemplate().getDataSource().getConnection().getMetaData();
			Assert.notNull(databaseMetaData);
			String tableName = originTable.getTableName();
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
				    columnNameList.add(columnName);
					Column col = new Column();
					col.setName(columnName);
					col.setColumnName(columnName);
					col.setColumnType(columnType);
					columns.add(col);
				} else {
					for (Column column : columns) {
					    columnNameList.add(column.getName());
						if (column.getName().equalsIgnoreCase(columnName)) {
							column.setColumnType(columnType);
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.error("Read the metadata error! the error message is: {}", e.getMessage());
			throw new RuntimeException("Read the metadata error!");
		}
		for (Column column : columns) {
			selectSql.append(column.getName()).append(",");
		}
		selectSql = selectSql.deleteCharAt(selectSql.length() - 1);
		selectCountSql.append(originTable.getTableName());
		selectSql.append(" from ").append(originTable.getTableName());
		ruuning = true;
		if (logger.isDebugEnabled()) {
			logger.debug("The init sql is: [{}]",selectSql.toString());
		}
	}

	public boolean isStart() {
		return ruuning;
	}

	public BatchData select() {
		String oldThreadName = Thread.currentThread().getName();
		StringBuffer currentName = new StringBuffer(pump.getJobId().toString());
		currentName.append(BAR_LINE).append(pump.getPipelineId()).append(BAR_LINE).append(pump.getId())
		.append(BAR_LINE).append(pump.getSerial()).append(BAR_LINE).append(SELECTOR);
		Thread.currentThread().setName(currentName.toString());
		
		if (logger.isInfoEnabled()) {
			logger.info("Selector start working~!");
		}
		
		final Table originTable = (Table)pump.getSource(); 
		RowDataBatch rdb = new RowDataBatch();
		BatchData bd = new BatchData();
		
		//查询总条数
		int count = dbDialect.getJdbcTemplate().queryForInt(selectCountSql.toString());
		if ((pagenum-1)*pageSize < count) {
		    selectSql = paging(originTable.getDb().getDbType(),selectSql);
		    
		    if (logger.isDebugEnabled()) {
	            logger.debug("The init sql is: [{}]",selectSql.toString());
	        }
		    
			List<RowData> rows = dbDialect.getJdbcTemplate().query(selectSql.toString(), new RowMapper<RowData>() {

				public RowData mapRow(ResultSet rs, int index) throws SQLException {
					List<Column> columns = originTable.getColumns();
					RowData rd = new RowData();
					int size = columns.size();
					for (int i = 0; i < size; i++) {
						CellData cd = new CellData();
						cd.setColumnIndex(new Long(i));
						cd.setColumnName(columns.get(i).getColumnName());
						cd.setColumnType(columns.get(i).getColumnType());
						cd.setColumnValue(rs.getString(i+1));
						rd.getCellData().add(cd);
					}
					return rd;
				}
			});
			rdb.setBatch(rows);
			bd.setRdb(rdb);
			pagenum++;
		} else {
			return null;
		}
		Thread.currentThread().setName(oldThreadName);
		return bd;
	}

	public void stop() {
		
	}

	public void rollback() {
		
	}

	/**
     * 该方法处理sql分页查询.
     * 
     * @param dataType
     *        数据库类型
     * @param sql
     *        未加分页操作的查询语句
     * @return 添加分页后的查询语句
     */
//	SELECT * FROM (
//	       select *,row_number() over (order by names) as rownum from "fable"."wuhao"
//	)  as t where t.rank between 3 and 4
	 

    public StringBuffer paging(String dataType, StringBuffer sql) {
        StringBuffer pagingSql = null;
        if (DbType.MSSQL.equalsIgnoreCase(dataType) ||
            DbType.DAMENG7.equalsIgnoreCase(dataType)) {
            pagingSql = new StringBuffer("select ");
            for(int i=0;i<columnNameList.size();i++){
                pagingSql.append(columnNameList.get(i))
                .append(" ,");
            }
            pagingSql = pagingSql.deleteCharAt(pagingSql.length() - 1);
            pagingSql.append(" from (select row_number() over (order by ")
                     .append(columnNameList.get(0)) 
                     .append(") as rownum ,")
                     .append(sql)
                     .append(")  as t where t.rownum between ")
                     .append((pagenum-1)*pageSize)
                     .append(" and")
                     .append(pagenum*pageSize);
        }
        else {
            pagingSql = new StringBuffer("select ");
        }
        pagingSql.append(sql);
        if (DbType.ORACLE.equalsIgnoreCase(dataType)) {
            pagingSql.append(" where  rownum<=")
                     .append(pagenum*pageSize)
                     .append(" and rownum>=")
                     .append((pagenum-1)*pageSize+1);
        }
        pagingSql.append(" order by ")
                 .append(columnNameList.get(0));
                

        if (DbType.MYSQL.equalsIgnoreCase(dataType)) {
            pagingSql.append(" limit ")
                     .append((pagenum-1)*pageSize)
                     .append(" , ")
                     .append(pagenum*pageSize);
        }

        return pagingSql;
    }
	

	//----------------------------------------------------setter&&getter-----------------------------------------------
	public boolean isRuuning() {
		return ruuning;
	}

	public void setRuuning(boolean ruuning) {
		this.ruuning = ruuning;
	}


	public StringBuffer getSelectSql() {
		return selectSql;
	}

	public void setSelectSql(StringBuffer selectSql) {
		this.selectSql = selectSql;
	}

	public Pump getPump() {
		return pump;
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
