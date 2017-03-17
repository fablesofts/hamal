package com.fable.hamal.log.service;

import java.util.List;

import com.fable.hamal.shuttle.common.model.envelope.et.filter.ColumnFilter;


public interface SysLogDetailLogIntf {
    
    /**
     * 记录查询操作的具体sql.
     * @param selectSql 查询语句
     * @param flag 是否成功
     * @return 返回本次记录的ID
     */
    public Long selectSql(String selectSql,int flag);
    
    /**
     * 记录插入操作的具体sql.
     * @param insertSql 
     * @return 返回本次记录的ID
     */
    public Long insertSql(String insertSql,int flag);
    /**
     * 记录修改操作的具体sql.
     * @param updateSql
     * @param flag 是否成功
     * @return 返回本次记录的ID
     */
    public Long updateSql(String updateSql,int flag);
    /**
     * 记录删除操作的具体sql.
     * @param deleteSql
     * @param flag 是否成功
     * @return 返回本次记录的ID   
     */
    public Long deleteSql(String deleteSql,int flag);
    
    /**
     * 记录过滤具体信息
     * @param flag 是否成功
     * @param columnFilters 过滤策略
     * @return 返回本次记录的ID   
     */
    public Long filterExtracter(List<ColumnFilter> columnFilters,int flag);
    
    /**
     * 记录转换策略
     * @param flag 是否成功
     * @param columnFilters 转换策略
     * @return 返回本次记录的ID   
     */
    public Long convertExtracter(List<ColumnFilter> columnFilters,int flag);
}
