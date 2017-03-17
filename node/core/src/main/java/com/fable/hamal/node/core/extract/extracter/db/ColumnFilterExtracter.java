/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.extracter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.common.enums.Operator;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.ColumnFilter;
//import com.fable.hamal.shuttle.common.syslog.impl.RowLevelLogImpl;
//import com.fable.hamal.shuttle.common.syslog.impl.SyslogDetailImpl;
import com.fable.hamal.shuttle.common.syslog.intf.RowLevelLogIntf;
import com.fable.hamal.shuttle.common.syslog.intf.SysLogDetailLogIntf;

/**
 * @author xieruidong 2013年12月18日 下午5:03:58
 */
public class ColumnFilterExtracter implements Extracter {

    private final static Logger logger = LoggerFactory.getLogger(ColumnFilterExtracter.class);
//    
//    private static final RowLevelLogIntf rowLog = new RowLevelLogImpl();
//    
//    private static final SysLogDetailLogIntf detailLog = new SyslogDetailImpl();
    
    private Map<String, ColumnFilter> columnFilterMap;
    
    private List<ColumnFilter> columnFilters;
    
    private Pump pump;
    
    private Long sysLogDetailId = null;

    public ColumnFilterExtracter(List<ColumnFilter> columnFilters) {
        this.columnFilters = columnFilters;
    }

    public ColumnFilterExtracter() {

    }

    @Override
    public void start() {

    }

    @Override
    public boolean isStart() {
        return false;
    }

    @Override
    public void doExtract(BatchData data, ExtracterChain chain) {
        if (columnFilterMap.isEmpty()) {
            chain.doExtract(data);
            return;
        }
        RowDataBatch rdb = data.getRdb();
        if (null == rdb) {
            chain.doExtract(data);
            return;
        }
        List<RowData> rows = rdb.getBatch();
        if (null == rows || 0 == rows.size()) {
            chain.doExtract(data);
            return;
        }
        //记录需要即将删除的数据
        List<RowData> indexList = new ArrayList<RowData>();
        for (RowData row : rows) {
            for (CellData cell : row.getCellData()) {
                String columnName = cell.getColumnName();
                String value = cell.getColumnValue();
                ColumnFilter columnFilter = columnFilterMap.get(columnName);
                if (null == columnFilter) {
                    continue;
                }
                else {
                    if (checkContent(value, columnFilter.getOperator(),
                        columnFilter.getValue())) {
                        indexList.add(row);
                    }
                }
            }
        }
        
        //抛掉不合法数据
        for(RowData rd : indexList){
            rows.remove(rd);
        }
        
        //封装被过滤的数据
        BatchData fbatchDate = new BatchData();
        RowDataBatch frowDataBatch = new RowDataBatch();
        frowDataBatch.setBatch(indexList);
        fbatchDate.setRdb(rdb);
        //记录过滤规则
//        if(null == sysLogDetailId) {
//            try {
////                sysLogDetailId = detailLog.filterExtracter(columnFilters,0);
//            }
//            catch (SQLException e) {
//                logger.error(
//                    "record detailLog error! the error message is: {}",
//                    e.getMessage());
//            }
//        }
        //记录被过滤的数据
//        rowLog.filterDate(fbatchDate,sysLogDetailId);
        
        chain.doExtract(data);
    }


    private boolean checkContent(String colValue, String operate,
        List<String> value) {

        boolean check = false;
        Operator operator = Operator.valueOf(operate);

        try {
            switch (operator) {
                case EQUAL:
                    for (String val : value) {
                        if (colValue.equals(val)) {
                            check = true;
                        }
                    }
                break;
                case CONTAIN:
                    for (String val : value) {
                        if (colValue.contains(val)) {
                            check = true;
                        }
                    }
                break;
                case GREATER:
                    for (String val : value) {
                        if (Integer.parseInt(colValue) < Integer.parseInt(val)) {
                            check = true;
                        }
                    }
                break;
                case LESS:
                    for (String val : value) {
                        if (Integer.parseInt(colValue) > Integer.parseInt(val)) {
                            check = true;
                        }
                    }
                break;
                case REGEXP:
                    for (String val : value) {
                        if (colValue.matches(val)) {
                            check = true;
                        }
                    }
                break;
                default:
                    check = false;
                break;
            }
        }
        catch (NumberFormatException e) {
            
            if(logger.isDebugEnabled()){
                logger.debug("Intger转换数字时，出现异常：{}", e.getMessage());
            }
            e.printStackTrace();
        }catch(Exception e){
            if(logger.isDebugEnabled()){
                logger.debug("数据库内容过滤时，出现异常：{}", e.getMessage());
            }
            e.printStackTrace();
        }
        
        if(logger.isInfoEnabled() && check){
            logger.info("列值不合法{}，抛掉。", colValue);
        }

        return check;
    }

    /**
     * 处理字段名大小写问题
     */
    private void disposePump() {
        if (pump == null)
            return;
        if (pump.getSource() == null)
            return;
        Table table = (Table)pump.getSource();
        String dbType = table.getDb().getDbType();
        this.columnFilterMap = new HashMap<String, ColumnFilter>();
        if(logger.isInfoEnabled()){
            logger.info("数据库类型：{}", dbType);
        }
        for (ColumnFilter columnFilter : columnFilters) {
            if (!"m".equals(dbType) && !"e".equals(dbType) &&
                !"f".equals(dbType)&& !"s".equals(dbType)) {
                columnFilterMap.put(columnFilter.getName().toUpperCase(),
                    columnFilter);
                if(logger.isInfoEnabled()){
                    logger.info("进入Oracle或sqlserver");
                }
            }
            else {
                columnFilterMap.put(columnFilter.getName(), columnFilter);
                if(logger.isInfoEnabled()){
                    logger.info("进入Mysql或者达梦");
                }
            }
        }

    }

    public Pump getPump() {
        return pump;
    }

    public void setPump(Pump pump) {
        this.pump = pump;
        disposePump();
    }
}
