/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.TimestampService;
import com.fable.hamal.shuttle.common.model.config.Timestamp;

/**
 * 
 * @author xieruidong 2013年11月21日 下午4:33:34
 */
public class TimestampServiceImpl extends BaseServiceImpl implements TimestampService {


    @Override
    public Timestamp getTimestamp(Timestamp ts) {
        return this.getJdbcTemplate().queryForObject("select TASK_ID,DATA_SOURCE_ID,TABLE_NAME,DATA_COLUMN,SWITCH_TIME from dsp_timestamp "
                        +"where TASK_ID ="+ts.getTaskId()+" AND DATA_SOURCE_ID = "+ts.getDataSourceId()+" AND TABLE_NAME = '"+ts.getTableName()+"'",
                        new RowMapper<Timestamp>() {
            @Override
            public Timestamp mapRow(ResultSet rs, int rowNum) throws SQLException {
                Timestamp timestamp = new Timestamp();
                timestamp.setTaskId(rs.getLong("TASK_ID"));
                timestamp.setDataSourceId(rs.getLong("DATA_SOURCE_ID"));
                timestamp.setTableName(rs.getString("TABLE_NAME"));
                timestamp.setDataColumn(rs.getString("DATA_COLUMN"));
                timestamp.setSwitchTime(rs.getTimestamp("SWITCH_TIME"));
                return timestamp;
            }
        });
    }

    @Override
    public int updateTimestamp(Timestamp ts) {
        Object [] args = {ts.getSwitchTime(),ts.getTaskId(),ts.getDataSourceId(),ts.getTableName()};
        return this.getJdbcTemplate().update("update dsp_timestamp set SWITCH_TIME = ? where TASK_ID = ? and DATA_SOURCCE_ID = ? and TABLE_NAME = ?;", args);
    }
}
