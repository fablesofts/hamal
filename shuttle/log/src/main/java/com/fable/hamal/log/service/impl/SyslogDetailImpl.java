package com.fable.hamal.log.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fable.hamal.shuttle.common.model.envelope.et.filter.ColumnFilter;
import com.fable.hamal.shuttle.common.syslog.SysLogId;
import com.fable.hamal.shuttle.common.syslog.intf.SysLogDetailLogIntf;


public class SyslogDetailImpl implements SysLogDetailLogIntf {

    private JdbcTemplate jdbcTemplate;
    
    private String sql ="insert into SYS_LOG_DETAIL (ID,SYS_LOG_ID,OPERATION_TYPE,OPERATION_DETAIL,OPERATION_RESULTS) values (?,?,?,?,?)";

    @Override
    public Long selectSql(String selectSql,int flag) {
        Long id = getSequence();
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,0,selectSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long insertSql(String insertSql,int flag) {
        Long id = getSequence();
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,3,insertSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long updateSql(String updateSql,int flag) {
        Long id = getSequence();
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,3,updateSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long deleteSql(String deleteSql,int flag) {
        Long id = getSequence();
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,3,deleteSql,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }
    

    @Override
    public Long filterExtracter(List<ColumnFilter> columnFilters,int flag) {
        String extracter = getExtracter(columnFilters);
        Long id = getSequence();
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,1,extracter,flag};
        this.getJdbcTemplate().update(sql, args);
        return id;
    }

    @Override
    public Long convertExtracter(List<ColumnFilter> columnFilters,int flag) {
        String extracter = getExtracter(columnFilters);
        Long id = getSequence();
        Long sysLogID = SysLogId.getSysLogId().get("sysLogId");
        Object[] args = {id,sysLogID,2,extracter,flag};
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
    
    public Long getSequence(){
        String sql = "select SYSLOGDETAIL_SEQ.nextval from dual";
        Long id = this.getJdbcTemplate().queryForLong(sql);
        return id;
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

    @Override
    public long recordErrorLog(String depictMessage, String errorMessage)
        throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

}
