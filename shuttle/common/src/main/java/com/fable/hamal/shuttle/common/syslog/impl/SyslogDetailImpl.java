package com.fable.hamal.shuttle.common.syslog.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fable.hamal.shuttle.common.model.envelope.et.filter.ColumnFilter;
import com.fable.hamal.shuttle.common.syslog.SysLogId;
import com.fable.hamal.shuttle.common.syslog.intf.SysLogDetailLogIntf;


public class SyslogDetailImpl implements SysLogDetailLogIntf {

    private JdbcTemplate jdbcTemplate;
    
    private static final String dbTypeOrcl = "oracle";
    
 // private static final String dbTypeMysql = "mysql";
    
    private String sql ="insert into SYS_LOG_DETAIL (ID,SYS_LOG_ID,OPERATION_TYPE,OPERATION_DETAIL,OPERATION_RESULTS) values (?,?,?,?,?)";

    @Override
    public Long selectSql(String selectSql,int flag) throws SQLException {
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,0,selectSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long insertSql(String insertSql,int flag) throws SQLException {
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,3,insertSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long updateSql(String updateSql,int flag) throws SQLException {
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,3,updateSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long deleteSql(String deleteSql,int flag) throws SQLException {
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,3,deleteSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }
    

    @Override
    public Long filterExtracter(List<ColumnFilter> columnFilters,int flag) throws SQLException {
        String extracter = getExtracter(columnFilters);
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,1,extracter,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long convertExtracter(List<ColumnFilter> columnFilters,int flag) throws SQLException {
        String extracter = getExtracter(columnFilters);
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,2,extracter,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }
    
    @Override
    public long recordErrorLog(String depictMessage, String errorMessage) throws SQLException {
        Long id = getSequence(dbTypeOrcl);
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        String depict = "异常描述 ： "+depictMessage+",异常信息 ： "+errorMessage;
        Object[] args = {id,sysLogID,4,depict,1};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    
    //解析Extracter策略
    public String getExtracter(List<ColumnFilter> columnFilters) {
        
        StringBuffer Extracter = new StringBuffer();
        if(null != columnFilters && columnFilters.size()>0) {
            for(ColumnFilter columnFilter: columnFilters) {
                int i=1;
                Extracter.append("EXTRACT")
                         .append(i)
                         .append(" : keyword = ")
                         .append(columnFilter.getKeyword())
                         .append(" , name = ")
                         .append(columnFilter.getName())
                         .append(" , operator = ")
                         .append(columnFilter.getOperator())
                         .append(" , value = ");
                         
                for(String value : columnFilter.getValue()) {
                    Extracter.append(value)
                             .append(" , ");
                }
                Extracter.deleteCharAt(Extracter.length() - 2);
            }
        }
        return Extracter.toString();
    }
    
    public Long getSequence(String dbType) throws SQLException{
        if (dbTypeOrcl.equals(dbType)) {
            String sql = "select SYSLOGID_SEQ.nextval from dual";
            Long id = this.getJdbcTemplate().queryForLong(sql);
            return id;
        }
        else {
            Statement stmt = null;
            ResultSet rs = null;
//            ResultSet rs1 = null;
            stmt =
                getJdbcTemplate()
                    .getDataSource()
                    .getConnection()
                    .createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                        java.sql.ResultSet.CONCUR_UPDATABLE);
//            rs1 =
//                getJdbcTemplate().getDataSource().getConnection()
//                    .getMetaData()
//                    .getTables(null, null, "autoIncTutorial", null);
//            // 判断是否有序列表
//            if (!rs1.next()) {
                // 创建demo表
            StringBuffer createSql = new StringBuffer()
                        .append("CREATE TABLE autoIncTutorial (")
                        .append("priKey INT NOT NULL AUTO_INCREMENT, ")
                        .append("dataField VARCHAR(64), PRIMARY KEY (priKey))");
            stmt.executeUpdate(createSql.toString());
//            }

            stmt.executeUpdate("INSERT INTO autoIncTutorial (dataField) "
                + "values ('')", Statement.RETURN_GENERATED_KEYS); // 向驱动指明需要自动获取generatedKeys！
            Long autoIncKeyFromApi = -1L;
            rs = stmt.getGeneratedKeys(); // 获取自增主键！
            if (rs.next()) {
                autoIncKeyFromApi = rs.getLong(1);
            }
            else {
                // throw an exception from here
            }
            rs.close();
//            rs1.close();
            return autoIncKeyFromApi;
        }
    }
    
//    /**
//     * 增加并且获取主键
//     * @param sql sql语句
//     * @param params 参数列表
//     * @return 主键
//     */
//    public Object insertAndGetKey(final String sql, final Object... params) {
//        
//        final KeyHolder key = new GeneratedKeyHolder();
//
//        jdbcTemplate.update(new PreparedStatementCreator() {
//
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(sql, 
//                        PreparedStatement.RETURN_GENERATED_KEYS);
//                PreparedStatementSetter pss = newArgPreparedStatementSetter(params);
//                try {
//                    if (pss != null) {
//                        pss.setValues(ps);
//                    }
//                } finally {
//                    if (pss instanceof ParameterDisposer) {
//                        ((ParameterDisposer) pss).cleanupParameters();
//                    }
//                }
//                return ps;
//            }
//
//            private PreparedStatementSetter newArgPreparedStatementSetter(
//                Object[] params) {
//                // TODO Auto-generated method stub
//                return null;
//            }
//            
//        }, key);
//        
//        return key.getKey();
//    }
    
    
    public JdbcTemplate getJdbcTemplate() {
        ApplicationContext ac = new FileSystemXmlApplicationContext("E:\\workspace1\\hamal\\node\\core\\src\\main\\resources\\spring\\hamal.node.core-factory.xml"); 
        return (JdbcTemplate)ac.getBean("jdbcTemplate");
    }

    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
