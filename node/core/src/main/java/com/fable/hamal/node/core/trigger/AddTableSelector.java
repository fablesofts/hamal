/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.trigger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.node.core.select.AbstractSelector;
import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.common.model.config.metadata.Column;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 数据库表的Selector
 * 
 * @author zhangl
 */
public class AddTableSelector extends AbstractSelector implements Selector {

    private static final Logger logger = LoggerFactory
        .getLogger(AddTableSelector.class);
    private volatile boolean running = false;
    private Pump pump;
    private List<StringBuffer> selectSqlList = new ArrayList<StringBuffer>();
    private static final int pageSize = 1000;
    private int pagenum = 1; 
    private String lastseqid = "-1";
    private DbDialect dbDialect;
    private List<List<Column>> columnsList = new ArrayList<List<Column>>();
    private List<List<Column>> columnsaddList = new ArrayList<List<Column>>();
    private static final String BEFOREUPDATESTATE = "2";
    private static final String DELETESTATE = "4";
    private static final String FABLEETLID = "fableetlid";
    private static final String COMMA=",";
    private List<String []> keys = new ArrayList<String []>();
    private boolean isDM7 = false;
    private String schema ="";
    private Connection conn = null;
    private String [] tableNames;
    private int num;
    
    public AddTableSelector() {

    }

    public AddTableSelector(Pump pump) {
        this.pump = pump;
    }

    public void start() {
        
        final Table originTable = (Table)pump.getSource();
//        if (DbType.DAMENG6.equals(originTable.getDb().getDbType())){
//            try {
//                Thread.sleep(1000);
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        System.out.println("~~~~~~~~~~~~~");
        conn =DataSourceUtils.getConnection(dbDialect.getJdbcTemplate().getDataSource());
        System.out.println("!!!!!!!!!!!!!!!!!!!!"+conn);
        if(originTable.getTableName().contains(COMMA)){
            tableNames = originTable.getTableName().split(COMMA);
        } else {
            tableNames = new String [1];
            tableNames[0] = originTable.getTableName();
        }
        if(tableNames!=null){
            for (int i = 0;i<tableNames.length;i++){
                List<Column> columns = new ArrayList<Column>();
                ArrayList<Column> columnsadd = new ArrayList<Column>();
                StringBuffer selectSql = new StringBuffer();
                String [] key = null;
                try {
                    String[] pk  = getPremaryKey(originTable.getDb(),tableNames[i]);
                    if (pk.length>0) {
                        key = pk;
                    } else {
                        key = findTableUDX(originTable.getDb() , tableNames[i]);
                    }
                    keys.add(key);
                }
                catch (SQLException e1) {
                    logger.error(
                        "Read the primery key error! the error message is: {}",
                        e1.getMessage());
                }
                if (key != null) {
                    ResultSet rs = null;
                    ResultSet rsadd = null;
                    try {
                        DatabaseMetaData databaseMetaData = conn.getMetaData();
                        if (databaseMetaData.storesLowerCaseIdentifiers()) {
                            tableNames[i] = tableNames[i].toLowerCase();
                        }
                        if (databaseMetaData.storesUpperCaseIdentifiers() &&
                                        !DbType.DAMENG7.equals(originTable.getDb().getDbType())) {
                            tableNames[i] = tableNames[i].toUpperCase();
                        }
                        rs = databaseMetaData.getColumns(null, null, tableNames[i], null);
                        boolean flag = 0 == columns.size();
                        while (rs.next()) {
                            String columnName = rs.getString("COLUMN_NAME");
                            int columnType = rs.getInt("DATA_TYPE");
                            // 如果没有表示columns则表示包括全部列名
                            if (flag) {
                                Column col = new Column();
                                col.setColumnName(columnName);
                                col.setColumnType(columnType);
                                columns.add(col);
                            }
                            else {
                                for (Column column : columns) {
                                    if (column.getColumnName().equalsIgnoreCase(
                                        columnName)) {
                                        column.setColumnType(columnType);
                                    }
                                }
                            }
                        }
                        
                        //从增量表中获取需要交换的数据的主键和唯一约束字段
                        if(DbType.DAMENG7.equals(originTable.getDb().getDbType())) {
                            rsadd = databaseMetaData.getColumns(null, null, "fl_"+tableNames[i], null);
                            isDM7 = true;
                        } else{
                            rsadd = databaseMetaData.getColumns(null, null, "FL_"+tableNames[i], null);
                        }
                        // 封装增量表中的主键
                        while (rsadd.next()) {
                            String columnName = rsadd.getString("COLUMN_NAME");
                            int columnType = rsadd.getInt("DATA_TYPE");
                            // 如果没有表示columns则表示包括全部列名
                            Column col = new Column();
                            col.setColumnName(columnName);
                            col.setColumnType(columnType);
                            columnsadd.add(col);
                        }
                        columnsaddList.add(columnsadd);
                        columnsList.add(columns);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        logger.error("Read columns error the error message is: {}",
                            e.getMessage());
                    }
                    if (isDM7) {
                     // 添加主表中所以需要交换的字段
                        for (Column column : columns) {
                            selectSql.append("\"")
                                     .append(tableNames[i])
                                     .append("\".\"")
                                     .append(column.getColumnName())
                                     .append("\",");
                        }

                        // 添加增量表的col
                        for (Column column : columnsadd) {
                            selectSql.append("\"fl_")
                                     .append(tableNames[i])
                                     .append("\".\"")
                                     .append(column.getColumnName())
                                     .append("\",");
                        }
                        selectSql = selectSql.deleteCharAt(selectSql.length() - 1);
                        // 添加增量表的序列和状态字段
                        selectSql.append(" from \"")
                                 .append(schema)
                                 .append("\".\"")
                                 .append(tableNames[i])
                                 .append("\" right join \"")
                                 .append(schema)
                                 .append("\".\"fl_")
                                 .append(tableNames[i])
                                 .append("\" on ");
                        for (String column : key) {
                            selectSql.append("\"")
                                     .append(tableNames[i])
                                     .append("\".\"")
                                     .append(column)
                                     .append("\" = \"")
                                     .append("fl_")
                                     .append(tableNames[i])
                                     .append("\".\"")
                                     .append(column)
                                     .append("\" and ");
                        }
                    } else {
                        // 添加主表中所以需要交换的字段
                        for (Column column : columns) {
                            selectSql.append(tableNames[i])
                                     .append(".")
                                     .append(column.getColumnName())
                                     .append(",");
                        }

                        // 添加增量表的col
                        for (Column column : columnsadd) {
                            selectSql.append("fl_")
                                     .append(tableNames[i])
                                     .append(".")
                                     .append(column.getColumnName())
                                     .append(",");
                        }
                        selectSql = selectSql.deleteCharAt(selectSql.length() - 1);
                        // 添加增量表的序列和状态字段
                        selectSql.append(" from ")
                                 .append(tableNames[i])
                                 .append(" right join fl_")
                                 .append(tableNames[i])
                                 .append(" on ");
                        for (String column : key) {
                            selectSql.append(tableNames[i])
                                     .append(".")
                                     .append(column)
                                     .append("=")
                                     .append("fl_")
                                     .append(tableNames[i])
                                     .append(".")
                                     .append(column)
                                     .append(" and ");
                        }
                    }
                    
                    selectSql =
                        selectSql.delete(selectSql.length() - 4, selectSql.length());
//                    selectSql.append("order by fableetlid");
                    selectSqlList.add(selectSql);
                    running = true;

                    if (logger.isDebugEnabled()) {
                        logger.debug("the sql selectSql is {}", selectSql.toString());
                    }
                }
            }
        }
        else {
//            try {
//                throw new Exception("未找到主键");
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
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
            logger.info("Selector start working~! ");
        }
        final Table originTable = (Table)pump.getSource(); 
        
        //循环遍历一批普通表  查询第一张表 每次取前1000条交换 直至取不到时  开始取下一张表  全部结束后 return null；
        for (int x=0;x < tableNames.length;) {
            RowDataBatch rdb = new RowDataBatch();
            BatchData bd = new BatchData();
            StringBuffer sql = new StringBuffer()
                            .append(selectSqlList.get(x))
                            .append(" where fableetlid >  ")
                            .append(lastseqid);
                // 添加分页
            StringBuffer pagingSql = paging(originTable.getDb().getDbType(), sql);
            if (logger.isDebugEnabled()) {
                logger.debug("the sql pagingSql is {}", pagingSql.toString());
            }
            num = x;
            List<RowData> rows = dbDialect.getJdbcTemplate().query(pagingSql.toString(), new RowMapper<RowData>() {
                public RowData mapRow(ResultSet rs, int index) throws SQLException {
                    List<Column> columns = columnsList.get(num);
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
                    for (int i = columns.size(); i<(columns.size()+columnsaddList.get(num).size());i++){
                        CellData cd = new CellData();
                        cd.setColumnIndex(new Long(i));
                        cd.setColumnName(columnsaddList.get(num).get(i-columns.size()).getColumnName());
                        cd.setColumnType(columnsaddList.get(num).get(i-columns.size()).getColumnType());
                        cd.setColumnValue(rs.getString(i+1));
                        rd.getCellData().add(cd);
                    }
                    rd.setTableName(tableNames[num]);
                    rd.setSeq(rs.getLong("fableetlid"));
                    return rd;
                }
            });
            if(rows.size()>0) {
                List<RowData> needloadrows = new ArrayList<RowData>();
              //过滤掉已经删除的 同时是增改操作的数据
                for (RowData row :rows){
                    int count=0;
                    String state = "";
                    for(CellData celldate : row.getCellData()){
                        for(int i=0;i<keys.get(x).length;i++){
                            //与源表的主键字段做判断 
                            if(celldate.getColumnName().equalsIgnoreCase(keys.get(x)[i])){
                                if(celldate.getColumnValue() == null ||
                                               "".equals(celldate.getColumnValue())){
                                    count++;
                                }
                            }
                            if("fableetlstate".equalsIgnoreCase(celldate.getColumnName())){
                                state = celldate.getColumnValue(); 
                            }
                        }
                    }
                    /*
                     * 过滤数据.
                     * 需要传的数据包括 1、更具增量表能到源表中查到对应数据的 增，删，改（后）的数据（不包括能查到的修改前数据）
                     *           2、或者被删除 无法到原表中查询的删除前数据
                     *           3、或者被修改 无法到原表中查询的修改前数据  
                     */
                    if((count!=keys.get(x).length && !BEFOREUPDATESTATE.equals(state)) ||
                                    (count==keys.get(x).length && BEFOREUPDATESTATE.equals(state)) ||
                                        DELETESTATE.equals(state) ){
                        needloadrows.add(row);
                    }
                }
                if(needloadrows.size()>0) {
                    for(CellData celldate :needloadrows.get(needloadrows.size()-1).getCellData()) {
                        if(FABLEETLID.equalsIgnoreCase(celldate.getColumnName())) {
                            lastseqid = celldate.getColumnValue();
                        }
                    }
                }
                rdb.setBatch(needloadrows);
                if (logger.isDebugEnabled()) {
                    logger.debug("rows size : {}", needloadrows.size());
                }
                bd.setRdb(rdb);
                pagenum++;
                return bd;
            } else {
                List<String> li = new ArrayList<String>();
                for(int j=0;j<tableNames.length;j++){
                    if(j!=x){
                        li.add(tableNames[j]);
                    }
                }
                columnsList.remove(x);
                columnsaddList.remove(x);
                keys.remove(x);
                selectSqlList.remove(x);
                tableNames = li.toArray(new String[li.size()]);    
                lastseqid = "-1";
            }
        }
        return null;
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
    public StringBuffer paging(String dataType, StringBuffer sql) {
        StringBuffer pagingSql = null;
        if (DbType.MSSQL.equalsIgnoreCase(dataType) ||
            DbType.DAMENG7.equalsIgnoreCase(dataType)) {
            pagingSql = new StringBuffer("select top ");
            pagingSql.append(pageSize)
                     .append(" ");
        }
        else {
            pagingSql = new StringBuffer("select ");
        }
        pagingSql.append(sql);
        if (DbType.ORACLE.equalsIgnoreCase(dataType)) {
            pagingSql.append(" and  rownum<=")
                     .append(pageSize)
                     .append(" and rownum>=1");
        }
        pagingSql.append(" order by fableetlid ");

        if (DbType.MYSQL.equalsIgnoreCase(dataType)) {
            pagingSql.append(" limit 0,")
                     .append(pageSize);
        }

        return pagingSql;
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
     * 
     * @param db
     *        目标表数据源对象
     * @param tableName
     *        表名
     * @return 返回各个数据库对应表名的主键或唯一约束
     * @throws SQLException
     *         查询失败
     * @throws ClassNotFoundException
     *         驱动未找到
     */
    private String[] findTableUDX(Db db, String tableName)
        throws SQLException {
//        Connection conn = null;
//        try {
//            conn =
//                dbDialect.getJdbcTemplate().getDataSource().getConnection();
//        }
//        catch (SQLException e) {
//            logger.error(
//                "Connect database failed ! the error message is: {}",
//                e.getMessage());
//        }
        StringBuffer selectkey = null;
        if (DbType.ORACLE.equalsIgnoreCase(db.getDbType())) {
            selectkey =
                new StringBuffer(
                    "select b.column_name from sys.user_indexes a,")
                    .append(" sys.user_ind_columns b where")
                    .append(" a.index_name = b.index_name")
                    .append(" and a.uniqueness = 'UNIQUE'")
                    .append(" and upper(a.table_name) = upper('")
                    .append(tableName)
                    .append("') and upper(a.table_owner) = upper('")
                    .append(db.getUsername()).append("')");
        }
        else if (DbType.MYSQL.equalsIgnoreCase(db.getDbType())) {
            selectkey =
                new StringBuffer("SELECT K.COLUMN_NAME FROM")
                    .append(" information_schema.TABLE_CONSTRAINTS T,")
                    .append(" information_schema.KEY_COLUMN_USAGE K")
                    .append(" where T.TABLE_NAME = K.TABLE_NAME and")
                    .append(" T.CONSTRAINT_NAME = K.CONSTRAINT_NAME")
                    .append(" and upper(T.TABLE_NAME) = upper('")
                    .append(tableName)
                    .append("') and upper(T.CONSTRAINT_SCHEMA)=upper('")
                    .append(db.getDbName())
                    .append("') and T.CONSTRAINT_SCHEMA=K.CONSTRAINT_SCHEMA")
                    .append(
                        " and T.CONSTRAINT_TYPE in ('PRIMARY KEY','UNIQUE')");
        }
        else if (DbType.MSSQL.equalsIgnoreCase(db.getDbType())) {
            selectkey =
                new StringBuffer(
                    "select c.name from sysindexkeys a,sysindexes b ,")
                    .append(" syscolumns c ,sysobjects d")
                    .append(" where a.id=b.id and a.id=c.id")
                    .append(" and a.colid=c.colid and a.id=d.id")
                    .append(" and a.indid=b.indid and upper(d.name)=upper('")
                    .append(tableName).append("')");
        }
        else if (DbType.DAMENG6.equals(db.getDbType())) {
            selectkey =
                new StringBuffer("select COLUMN_NAME from(")
                    .append(
                        "SELECT A.NAME AS CONST7RAINT_NAME,B.NAME AS TABLE_NAME,E.NAME AS COLUMN_NAME ,'P/U' AS CONSTRAINT_TYPE ")
                    .append(
                        "FROM SYSDBA.SYSTABLES B,SYSDBA.SYSCONSTRAINTS A,SYSDBA.SYSINDEXES C,SYSDBA.SYSINDEXKEYS D,SYSDBA.SYSCOLUMNS E ")
                    .append("WHERE A.TABLEID=B.ID ")
                    .append("AND A.TABLEID=C.TABLEID ")
                    .append("AND C.ID=D.ID ")
                    .append("AND D.COLID=E.COLID ")
                    .append("AND A.TABLEID=E.ID ")
                    .append("AND (A.TYPE ='U'or A.TYPE = 'p' ) ")
                    .append(
                        "AND A.NAME=C.NAME AND B.TYPE='U' AND B.NAME = '")
                    .append(tableName)
                    .append("'")
                    .append(" UNION ALL ")
                    .append(
                        "select a.name as constraint_name,c.name as table_name,")
                    .append("d.name as col_name,'R' as constraint_type ")
                    .append(
                        "from (select name,id,tableid from sysdba.sysindexes where exists")
                    .append("(select 1 from sysdba.sysrefconstraints where ")
                    .append("exists (select 1 from sysdba.sysindexes where ")
                    .append(
                        "exists (select 1 from sysdba.sysconstraints j where ")
                    .append(
                        "j.type='P' and j.tableid   >999 and name =sysdba.sysindexes.name ")
                    .append(") and id= sysdba.sysrefconstraints.rid)")
                    .append("and fid= sysdba.sysindexes.id ")
                    .append(
                        "))a,sysdba.sysindexkeys b,sysdba.systables c,sysdba.syscolumns d ")
                    .append(
                        "where a.id = b.id and a.tableid = c.id and c.id = d.id ")
                    .append(
                        "and b.colid = d.colid and c.type='U' UNION ALL ")
                    .append(
                        "SELECT A.NAME AS CONSTRAINT_NAME,B.NAME AS TABLE_NAME,D.NAME AS COLUMN_NAME,")
                    .append("'C' AS CONSTRAINT_TYPE FROM ")
                    .append(
                        "SYSDBA.SYSCONSTRAINTS A,SYSDBA.SYSTABLES B,SYSDBA.SYSCHECKS C,SYSDBA.SYSCOLUMNS D ")
                    .append(
                        "WHERE A.TABLEID=B.ID AND A.ID =C.ID AND C.COLID=D.COLID AND A.TABLEID=D.ID AND A.TYPE ='C' AND B.TYPE='U')");
        }
        else if (DbType.DAMENG7.equals(db.getDbType())) {
            selectkey =
                new StringBuffer(
                    "select b.COLUMN_NAME from USER_CONSTRAINTS a,user_cons_columns b where  a.CONSTRAINT_TYPE='P'")
                    .append(" and a.CONSTRAINT_NAME =b.CONSTRAINT_NAME")
                    .append(" and a.TABLE_NAME = '").append(tableName)
                    .append("'");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("the sql selectkey is: {}", selectkey.toString());
        }
        if (selectkey != null) {
            final List<Map<String, Object>> resultList =
                dbDialect.getJdbcTemplate().queryForList(
                    selectkey.toString());
            final String[] result = new String[resultList.size()];
            int i = 0;
            for (final Map<String, Object> objects : resultList) {
                // 因为只有col_name这一列所以MAP中只有一个键值对 该for循环只会执行一次
                for (Map.Entry<String, Object> MapString : objects
                    .entrySet()) {
                    result[i++] = (String)MapString.getValue();// 次方法获取键值对的值
                }
            }
            return result;
        }
        return new String[] {};
    }

    // ----------------------------------------------------setter&&getter-----------------------------------------------
    public boolean isrunning() {
        return running;
    }

    public void setrunning(boolean running) {
        this.running = running;
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
