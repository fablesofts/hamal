/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.trigger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.model.config.metadata.Column;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.utils.constant.DbType;
import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.node.common.db.utils.SqlUtils;
import com.fable.hamal.node.core.load.Loader;
/**
 * @author xieruidong 2013年11月6日 下午4:28:03
 */
public class AddTableLoader implements Loader {

	private static final Logger logger	= LoggerFactory.getLogger(AddTableLoader.class);
	private volatile boolean running = false;
	private Pump pump;
	private Pair pair;
	private List<Pair> pairList;
	private List<DbDialect> dbDialect;
	//<url,<tableName,SQL>>
    private List<String> insertSqlMap = new ArrayList<String>();
    private List<String> updateSqlMap = new ArrayList<String>();
    private List<String> deleteSqlMap = new ArrayList<String>();
	private String dbType;
    private List<String[]> keysMap = new ArrayList<String[]>();
    private List<List<Column>> columnsMap = new ArrayList<List<Column>>();
	private int num;
	private int num1;
	private static final String INSERTSTATE = "1";
	private static final String BEFOREUPDATE = "2";
	private static final String UPDATESTATE = "3";
	private static final String DELETESTATE = "4";
    private static final String COMMA=",";
	private static final String COL_FABLEETLSTATE = "FABLEETLSTATE";
	private DbDialect sourceDbDialect;
    private boolean soucreIsDM7 = false;
    private boolean targetIsDM7 = false;
    private String schema ="";
    private Connection conn = null;
    private String [] tableNames;
    
	public void start() {
		if (!running) {
		    
		    pairList = pump.getPairs();
		    for (int z=0;z<pairList.size();z++){
		        
		        Table targetTable = (Table)pairList.get(z).getTarget();
		        conn = conn =DataSourceUtils.getConnection(dbDialect.get(z).getJdbcTemplate().getDataSource());
		        String url = targetTable.getDb().getConnectUrl();
		        if(targetTable.getTableName().contains(COMMA)){
	                tableNames = targetTable.getTableName().split(COMMA);
	            } else {
	                tableNames = new String [1];
	                tableNames[0] = targetTable.getTableName();
	            }
	            for (int x=0;x < tableNames.length;x++){
	                //获取目的表的主键
	                String [] key = null;
	                try {
	                    String[] pk  = getPremaryKey(targetTable.getDb(),tableNames[x]);
	                    if (pk.length>0) {
	                        key = pk;
	                    } else {
	                        key = findTableUDX(targetTable.getDb(), tableNames[x],z);
	                    }
	                    keysMap.add(key);
	                }
	                catch (SQLException e1) {
	                    logger.error("Read primery key error the error message is: {}",e1.getMessage());
	                }
	                List<Column> columns = new ArrayList<Column>();
	               
	                ResultSet rs = null;
	                try {
	                    if (conn.getMetaData().storesLowerCaseIdentifiers()) {
	                        tableNames[x] = tableNames[x].toLowerCase();
	                    }
	                    if (conn.getMetaData().storesUpperCaseIdentifiers() &&
	                                    !DbType.DAMENG7.equals(targetTable.getDb().getDbType())) {
	                        tableNames[x] = tableNames[x].toUpperCase();
	                    }
	                    if(DbType.DAMENG7.equals(((Table)pump.getSource()).getDb().getDbType())) {
	                        soucreIsDM7 = true;
	                    }
	                    if(DbType.DAMENG7.equals(targetTable.getDb().getDbType())) {
	                        targetIsDM7 = true;
	                    } 
	                    rs = conn.getMetaData().getColumns(null, null, tableNames[x], null);
	                    boolean flag = 0 == columns.size();
	                    while (rs.next()) {
	                        String columnName = rs.getString("COLUMN_NAME");
	                        int columnType = rs.getInt("DATA_TYPE");
	                        //如果没有表示columns则表示包括全部列名
	                        if (flag) {
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
	                    columnsMap.add(columns);
	                } catch (SQLException e) {
	                    logger.error("Read columns error the error message is: {}",e.getMessage());
	                }
	                
	                
	                //拼接insert语句
	                int size =0;
	                StringBuffer insertsql = new StringBuffer("insert into ");
	                if (targetIsDM7) {
	                    insertsql.append("\"")
	                             .append(schema)
	                             .append("\".\"")
	                             .append(tableNames[x])
	                             .append("\" (");
	                } else {
	                    insertsql.append(tableNames[x])
	                             .append("(");
	                }
	                for (Column column : columns) {
	                    if (targetIsDM7) {
	                        insertsql.append("\"")
	                                 .append(column.getColumnName())
	                                 .append("\",");
	                    } else {
	                        insertsql.append(column.getColumnName()).append(","); 
	                    }
	                }
	                insertsql = insertsql.deleteCharAt(insertsql.length() - 1);
	                insertsql.append(")values(");
	                size = columns.size();
	                for (int i = 0; i < size; i++) {
	                    insertsql.append("?,");
	                }
	                insertsql = insertsql.deleteCharAt(insertsql.length() - 1);
	                insertsql.append(")");
	                insertSqlMap.add(insertsql.toString());
	                //拼接update语句
	                StringBuffer updatesql = new StringBuffer("update ");
	                if (targetIsDM7) {
	                    updatesql.append("\"")
	                             .append(schema)
	                             .append("\".\"")
	                             .append(tableNames[x])
	                             .append("\" set ");
	                } else {
	                    updatesql.append(tableNames[x])
	                             .append(" set ");
	                }
	                for (Column column : columns) {
	                    if (targetIsDM7) {
	                        updatesql.append("\"")
	                                 .append(column.getColumnName())
	                                 .append("\"=?,");
	                    } else {
	                        updatesql.append(column.getColumnName())
	                                 .append("=?,");
	                    }
	                }
	                updatesql = updatesql.deleteCharAt(updatesql.length() - 1);
	                updatesql.append(" where ");
	                for (String pk : key) {
	                    if (targetIsDM7) {
	                        updatesql.append("\"")
	                                 .append(pk)
	                                 .append("\"=? and ");
	                    } else {
	                        updatesql.append(pk)
	                                 .append("=? and ");
	                    }
	                    
	                    
	                }
	                updatesql = updatesql.delete(updatesql.length() - 5,updatesql.length());
	                updateSqlMap.add(updatesql.toString());
	                
	                
	                //拼接delete语句
	                StringBuffer deletesql = new StringBuffer("delete from ");
	                if (targetIsDM7) {
	                    deletesql.append("\"")
	                             .append(schema)
	                             .append("\".\"")
	                             .append(tableNames[x])
	                             .append("\" where ");
	                } else {
	                    deletesql.append(tableNames[x])
	                             .append(" where ");
	                }
	                for (String col : key) {
	                    if (targetIsDM7) {
	                        deletesql.append("\"")
	                                 .append(col)
	                                 .append("\"=? and ");
	                    } else {
	                        deletesql.append(col)
	                                 .append("=? and ");
	                    }
	                }
	                deletesql = deletesql.delete(deletesql.length() - 5,deletesql.length());
	                
	                deleteSqlMap.add(deletesql.toString());
	                
	                this.dbType = targetTable.getDb().getDbType();
	                running = true;
	                if (logger.isDebugEnabled()) {
	                    logger.debug("the sql insertsql is: {}",insertsql.toString());
	                    logger.debug("the sql updatesql is: {}",updatesql.toString());
	                    logger.debug("the sql deletesql is: {}",deletesql.toString());
	                }
	            }
	            try {
	                if(conn!=null){
	                    conn.close();
	                }
	            }
	            catch (SQLException e) {
	                logger.error("Connection close error the error message is: {}",e.getMessage());
	            }
		    
		    }
		}
	}
	
	public void load(final BatchData data) {
		
	    /**
	     * 更具增量表里的状态先判断需要执行的是那种语句 
	     * 
	     */
	    List<Pair> pairs = pump.getPairs();
	    List<RowData> rows = data.getRdb().getBatch();
	    Long lastSeq = rows.get(rows.size()-1).getSeq();
	    if(rows.size()>0) {
    	    for(int z=0;z<pairs.size();z++){
    	        if (logger.isDebugEnabled()) {
    	            logger.debug("load tableName: {}",((Table)pairs.get(z).getTarget()).getTableName());
    	        }
    	        num1 = z;

	            //循环遍历多行记录
	            try {
	                for(int x=0;x<rows.size();x++) {
	                    num = x;
	                    String state="";
	                   //遍历每行记录中的字段
	                   for(int i=0;i<rows.get(x).getCellData().size();i++){
	                       //判断获取增量表中,每条数据的状态
	                       if(COL_FABLEETLSTATE.equalsIgnoreCase(rows.get(x).getCellData().get(i).getColumnName())){
	                           state = (String)rows.get(x).getCellData().get(i).getColumnValue();
	                       }
	                   }
	                   if(INSERTSTATE.equals(state)) {
	                       dbDialect.get(z).getJdbcTemplate().update(insertSqlMap.get(z), new PreparedStatementSetter() {
	                        
	                            @Override
	                            public void setValues(PreparedStatement ps) throws SQLException {
	                                dealPreparedStatement(ps,data.getRdb().getBatch().get(num),
	                                    columnsMap.get(num1),
	                                    keysMap.get(num1),"1"); 
	                            }
	                       });
	                   }
	                   else if(UPDATESTATE.equals(state)) {
	                       int rsnum = 0;
	                       rsnum =dbDialect.get(z).getJdbcTemplate().update(updateSqlMap.get(z), new PreparedStatementSetter() {
	                           
	                           @Override
	                           public void setValues(PreparedStatement ps) throws SQLException {
	                               dealPreparedStatement(ps,data.getRdb().getBatch().get(num),
	                                   columnsMap.get(num1),
	                                   keysMap.get(num1),"3"); 
	                           }
	                       });
	                       if(rsnum==0){
	                           dbDialect.get(z).getJdbcTemplate().update(insertSqlMap.get(z), new PreparedStatementSetter() {
	                               
	                                @Override
	                                public void setValues(PreparedStatement ps) throws SQLException {
	                                    dealPreparedStatement(ps,data.getRdb().getBatch().get(num),
	                                        columnsMap.get(num1),
	                                        keysMap.get(num1),"1"); 
	                                }
	                           });
	                        }
	                   }
	                   else if(DELETESTATE.equals(state)  ||
	                                    BEFOREUPDATE.equals(state)) {
	                       dbDialect.get(z).getJdbcTemplate().update(deleteSqlMap.get(z), new PreparedStatementSetter() {
	                           
	                           @Override
	                           public void setValues(PreparedStatement ps) throws SQLException {
	                               dealPreparedStatement(ps,data.getRdb().getBatch().get(num),
	                                   columnsMap.get(num1),
	                                   keysMap.get(num1),"4"); 
	                           }
	                       });
	                   }
	                }
	            } catch (Exception e) {
	                try {
	                    logger.error("触发器主从表加载异常，异常信息为："+e.getMessage());
	                }
	                catch (Exception e1) {
	                    logger.error("触发器主从表加载异常，异常信息为："+e.getMessage());
	                }
	            }  
	        }
    	}
	    //所有数据加载成功后删除增量表数据
        Table sourceTable = (Table)pump.getSource();
        String tableName = sourceTable.getTableName();
        Object[] args = {lastSeq};
        if (soucreIsDM7) {
            String sourceSchemas= "";
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = sourceDbDialect.getJdbcTemplate().getDataSource().getConnection();
                rs = conn.getMetaData().getSchemas();
                while(rs.next()) {
                    sourceSchemas = rs.getString("TABLE_SCHEM");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(rs!=null) {
                    try {
                        rs.close();
                    }
                    catch (SQLException e) {
                        logger.error("触发器主从表加载异常，异常信息为："+e.getMessage());
                    }
                } 
                if(conn!=null) {
                    try {
                        conn.close();
                    }
                    catch (SQLException e) {
                        logger.error("触发器主从表加载异常，异常信息为："+e.getMessage());
                    }
                }
            }
                StringBuffer deleteSql = new StringBuffer("delete from \"")
                .append(sourceSchemas)
                .append("\".\"fl_")
                .append(tableName)
                .append("\" where fableetlid <=?");
                sourceDbDialect.getJdbcTemplate().update(deleteSql.toString(),args); 
        } else {
                String deleteSql = "delete from fl_" +tableName+ " where fableetlid <=?";
                sourceDbDialect.getJdbcTemplate().update(deleteSql,args);    
                if (logger.isDebugEnabled()) {
                    logger.debug("删除表: {}","fl"+tableName);
                    logger.debug("id小于等于: {}","fl"+lastSeq);
                }
        }
	}

	public void setPump(Pump pump) {
		this.pump = pump;
	}
	
	
	private void dealPreparedStatement(PreparedStatement ps, RowData rd,List<Column> cols,String[]keys, String type) throws SQLException {
        //mark for：insert/update/delete for triggers
        List<CellData> cells = new ArrayList<CellData>();
        List<String> cellName = new ArrayList<String>();
        //-------按照 param数据 获取 想要的cells
        for (Column col : cols) {
            //添加 insert 和 update的 cells
            for (CellData cd: rd.getCellData()) {
                if (cd.getColumnName().equals(col.getColumnName()) && 
                                !cellName.contains(cd.getColumnName())) {
                    cellName.add(cd.getColumnName());
                    cells.add(cd);
                }
            }
        }
        //如果是delete 则清空 cells
        if (DELETESTATE.equals(type)) {
            cells.clear();
        }
        cellName.clear();
        if (!INSERTSTATE.equals(type)) {
            for (String key : keys) {
                for (CellData cd: rd.getCellData()) {
                    if (cd.getColumnName().equals(key) && cd.getColumnValue()!=null &&
                                    cd.getColumnValue()!="" && !cellName.contains(cd.getColumnName())) {
                        cellName.add(cd.getColumnName());
                        cells.add(cd);
                    }
                }
            }
        }
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
	
	
	/**
     * 只查询表中主键 .
     * @param dsdto 数据源对象
     * @param tablename 表名
     * @return
     */
    public String[] getPremaryKey (Db db,String tablename) {
        List<String> pkcols = new ArrayList<String>();
        ResultSet rs = null;
//        Connection conn = null;
        try {
//            conn = dbDialect.getJdbcTemplate().getDataSource().getConnection();
            if (DbType.ORACLE.equals(db.getDbType())) {
                schema = db.getUsername().toUpperCase();
            } else if(DbType.MSSQL.equals(db.getDbType())) {
                schema = null;
            } else {
                schema = db.getDbName();
            }
            rs = conn.getMetaData().getPrimaryKeys(null, schema, tablename);
            while (rs.next()) {
                pkcols.add(rs.getString("COLUMN_NAME"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
           if(rs!=null) {
               try {
                rs.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
           }
//           if(conn!=null) {
//               try {
//                conn.close();
//            }
//            catch (SQLException e) {
//                e.printStackTrace();
//            }
//           }
        }
        return pkcols.toArray(new String[pkcols.size()]);
    }
	
	
	
	/**
     * 获取各个数据库相应表的 主键和唯一约束.
     * @param db 目标表数据源对象
     * @param tableName 表名
     * @return 返回各个数据库对应表名的主键或唯一约束
     * @throws SQLException 查询失败
     * @throws ClassNotFoundException 驱动未找到
     */
    private String[] findTableUDX(Db db , String tableName,Integer index) throws SQLException {
        StringBuffer  selectkey = null;
//        Connection conn = null;
//        try {
//           conn = dbDialect.getJdbcTemplate().getDataSource().getConnection();
//        } catch (SQLException e) {
//            logger.error("Connect database failed ! the error message is: {}", e.getMessage());
//        }
        if (DbType.ORACLE.equalsIgnoreCase(db.getDbType())) {
            selectkey = new StringBuffer("select b.column_name from sys.user_indexes a,")
            .append(" sys.user_ind_columns b where")
            .append(" a.index_name = b.index_name")
            .append(" and a.uniqueness = 'UNIQUE'")
            .append(" and upper(a.table_name) = upper('")
            .append(tableName)
            .append("') and upper(a.table_owner) = upper('")
            .append(db.getUsername())
            .append("')");
        }
        else if (DbType.MYSQL.equalsIgnoreCase(db.getDbType())) {
            selectkey = new StringBuffer("SELECT K.COLUMN_NAME FROM")
            .append(" information_schema.TABLE_CONSTRAINTS T,")
            .append(" information_schema.KEY_COLUMN_USAGE K")
            .append(" where T.TABLE_NAME = K.TABLE_NAME and")
            .append(" T.CONSTRAINT_NAME = K.CONSTRAINT_NAME")
            .append(" and upper(T.TABLE_NAME) = upper('")
            .append(tableName)
            .append("') and upper(T.CONSTRAINT_SCHEMA)=upper('")
            .append(db.getDbName())
            .append("') and T.CONSTRAINT_SCHEMA=K.CONSTRAINT_SCHEMA")
            .append(" and T.CONSTRAINT_TYPE in ('PRIMARY KEY','UNIQUE')");
        }
        else  if (DbType.MSSQL.equalsIgnoreCase(db.getDbType())) {
            selectkey = new StringBuffer("select c.name from sysindexkeys a,sysindexes b ,")
            .append(" syscolumns c ,sysobjects d")
            .append(" where a.id=b.id and a.id=c.id")
            .append(" and a.colid=c.colid and a.id=d.id")
            .append(" and a.indid=b.indid and upper(d.name)=upper('")
            .append(tableName)
            .append("')");
        }
        else if (DbType.DAMENG6.equals(db.getDbType())) {
            selectkey = new StringBuffer("select COLUMN_NAME from(")
                .append("SELECT A.NAME AS CONST7RAINT_NAME,B.NAME AS TABLE_NAME,E.NAME AS COLUMN_NAME ,'P/U' AS CONSTRAINT_TYPE ")
                .append("FROM SYSDBA.SYSTABLES B,SYSDBA.SYSCONSTRAINTS A,SYSDBA.SYSINDEXES C,SYSDBA.SYSINDEXKEYS D,SYSDBA.SYSCOLUMNS E ")
                .append("WHERE A.TABLEID=B.ID ")
                .append("AND A.TABLEID=C.TABLEID ")
                .append("AND C.ID=D.ID ")
                .append("AND D.COLID=E.COLID ")
                .append("AND A.TABLEID=E.ID ")
                .append("AND (A.TYPE ='U'or A.TYPE = 'p' ) ")
                .append("AND A.NAME=C.NAME AND B.TYPE='U' AND B.NAME = '")
                .append(tableName)
                .append("'")
                .append(" UNION ALL ")
                .append("select a.name as constraint_name,c.name as table_name,")
                .append("d.name as col_name,'R' as constraint_type ")
                .append("from (select name,id,tableid from sysdba.sysindexes where exists")
                .append("(select 1 from sysdba.sysrefconstraints where ")
                .append("exists (select 1 from sysdba.sysindexes where ")
                .append("exists (select 1 from sysdba.sysconstraints j where ")
                .append("j.type='P' and j.tableid   >999 and name =sysdba.sysindexes.name ")
                .append(") and id= sysdba.sysrefconstraints.rid)")
                .append("and fid= sysdba.sysindexes.id ")
                .append("))a,sysdba.sysindexkeys b,sysdba.systables c,sysdba.syscolumns d ")
                .append("where a.id = b.id and a.tableid = c.id and c.id = d.id ")
                .append("and b.colid = d.colid and c.type='U' UNION ALL ")
                .append("SELECT A.NAME AS CONSTRAINT_NAME,B.NAME AS TABLE_NAME,D.NAME AS COLUMN_NAME,")
                .append("'C' AS CONSTRAINT_TYPE FROM ")
                .append("SYSDBA.SYSCONSTRAINTS A,SYSDBA.SYSTABLES B,SYSDBA.SYSCHECKS C,SYSDBA.SYSCOLUMNS D ")
                .append("WHERE A.TABLEID=B.ID AND A.ID =C.ID AND C.COLID=D.COLID AND A.TABLEID=D.ID AND A.TYPE ='C' AND B.TYPE='U')");
        } 
        else if (DbType.DAMENG7.equals(db.getDbType())){
            selectkey = new StringBuffer("select b.COLUMN_NAME from USER_CONSTRAINTS a,user_cons_columns b where  a.CONSTRAINT_TYPE='P'")
                        .append(" and a.CONSTRAINT_NAME =b.CONSTRAINT_NAME")
                        .append(" and a.TABLE_NAME = '")
                        .append(tableName)
                        .append("'");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("the sql selectkey is: {}",selectkey.toString());
        }
        if (selectkey != null) {
            final List<Map<String, Object>> resultList =
                            dbDialect.get(index).getJdbcTemplate().queryForList(selectkey.toString());
            final String[] result = new String[resultList.size()];
            int i = 0;
            for (final Map<String, Object> objects : resultList){
                //因为只有col_name这一列所以MAP中只有一个键值对 该for循环只会执行一次
                for (Map.Entry<String, Object> MapString : objects.entrySet()) { 
                    result[i++]=(String)MapString.getValue();//次方法获取键值对的值 
                } 
            }
            return result;
        }
        return new String[] {};
    }
	
    public DbDialect getSourceDbDialect() {
        return sourceDbDialect;
    }
    
    public void setSourceDbDialect(DbDialect sourceDbDialect) {
        this.sourceDbDialect = sourceDbDialect;
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
