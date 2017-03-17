package com.fable.hamal.shuttle.common.syslog.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fable.hamal.shuttle.common.syslog.SysLogId;
import com.fable.hamal.shuttle.common.syslog.intf.SyslogIntf;


public class SysLogImpl implements SyslogIntf {

    private JdbcTemplate jdbcTemplate;

    private static final String dbTypeOrcl = "oracle";

    // private static final String dbTypeMysql = "mysql";
  
    @Override
    public void countSelect(int count, int flag) throws SQLException {
        Long id = getSequence("oracle");
        Long taskid = SysLogId.getSysLogId().get("taskId");
        SysLogId.setSysLogId(taskid, id);
        Date startDate = new Date();
        StringBuffer insertSql = new StringBuffer() 
                    .append("insert into SYS_LOG (ID,TASK_ID,SELECT_COUNT,SELECT_DATA,")
                    .append("LOAD_COUNT,LOAD_DATA,OPERATION_RESULTS,START_TIME,END_TIME) ")
                    .append("values(?,?,?,?,?,?,?,?,?)");

        Object[] args =
            { id, taskid, count, "", "", "", flag, startDate, "" };
        this.getJdbcTemplate().update(insertSql.toString(), args);
    }

    @Override
    public void countLoad(int count, int flag) {
        Long id = SysLogId.getSysLogId().get("sysLogId");
        Date endDate = new Date();
        Object[] args = { count, flag, endDate, id };
        StringBuffer updateSql = new StringBuffer()
            .append("update SYS_LOG set LOAD_COUNT= ? , ")
            .append("OPERATION_RESULTS = ? , END_TIME= ? where ID = ?");
        this.getJdbcTemplate().update(updateSql.toString(), args);
    }

    public Long getSequence(String dbType) throws SQLException {
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


    public JdbcTemplate getJdbcTemplate() {
        ApplicationContext ac =
            new FileSystemXmlApplicationContext(
                "E:\\workspace1\\hamal\\node\\core\\src\\main\\resources\\spring\\hamal.node.core-factory.xml");
        return (JdbcTemplate)ac.getBean("jdbcTemplate");
    }


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
