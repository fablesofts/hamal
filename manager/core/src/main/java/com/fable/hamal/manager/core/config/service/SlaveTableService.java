package com.fable.hamal.manager.core.config.service;

import com.fable.hamal.shuttle.common.model.config.metadata.Db;


public interface SlaveTableService {
    /**
     * 查询是否有主从表关系
     * @param db 数据源
     * @param tableName 表名，多个按"，"隔开
     * @return
     */
    public boolean isSlaveTable (Db db,String tableName);
}
