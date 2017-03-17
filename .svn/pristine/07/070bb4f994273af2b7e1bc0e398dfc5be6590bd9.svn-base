package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.SlaveTableService;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.utils.ListFlPlTableUtil;


public class SlaveTableServiceImpl extends BaseServiceImpl implements SlaveTableService {

    @Override
    public boolean isSlaveTable(Db db, String tableName) {
        String [] tables;
        if(tableName.contains(",")){
            tables = tableName.split(",");
        } else {
            tables = new String [1];
            tables[0] = tableName;
        }
        List<String> li = new ArrayList<String>(); 
        for (String tablename : tables) {
            String f_sql = ListFlPlTableUtil.findFTableSQL(db, tablename);
            String p_sql = ListFlPlTableUtil.findPTableSQL(db, tablename);
            li = (this.getJdbcTemplate().query(f_sql, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            }));
            if(null != li) {
                if(li.size()>0) {
                    return true;
                } 
            }
            li = (this.getJdbcTemplate().query(p_sql, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            }));
            if(null != li) {
                if(li.size()>0) {
                    return true;
                } 
            }
        }
        return false;
    }
}
