/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.extracter.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.ColumnConverter;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.ColumnConverter.Verter;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.ColumnFilter;
import com.fable.hamal.shuttle.common.model.envelope.et.mapping.ColumnMapping;
//import com.fable.hamal.shuttle.common.syslog.impl.RowLevelLogImpl;
//import com.fable.hamal.shuttle.common.syslog.impl.SyslogDetailImpl;
import com.fable.hamal.shuttle.common.syslog.intf.RowLevelLogIntf;
import com.fable.hamal.shuttle.common.syslog.intf.SysLogDetailLogIntf;

/**
 * @author xieruidong 2013年12月18日 下午5:04:51
 */
public class ColumnConvertExtracter implements Extracter {

    private final static Logger logger = LoggerFactory.getLogger(ColumnConvertExtracter.class);
    
//    private static final RowLevelLogIntf rowLog = new RowLevelLogImpl();
//    
//    private static final SysLogDetailLogIntf detailLog = new SyslogDetailImpl();
    
    private Map<String, ColumnConverter> columnConverterMap;

    private List<ColumnConverter> columnConverters;

    private Pump pump;
    
    private Long sysLogDetailId = null;

    public ColumnConvertExtracter() {

    }

    public ColumnConvertExtracter(List<ColumnConverter> columnConverters) {
        this.columnConverters = columnConverters;
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
        if (columnConverterMap.isEmpty()) {
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
        
        for(String word : columnConverterMap.keySet()){
            if(logger.isInfoEnabled()){
                logger.info("替换的列名:{}", word); 
            }
        }
        
        for (RowData row : rows) {
            for (CellData cell : row.getCellData()) {
                String columnName = cell.getColumnName();
                String value = cell.getColumnValue();
                if (null == value || "".equals(value)) {
                	continue;
                }
                if(logger.isInfoEnabled()) {
                    logger.info("查看字段名称大小写：{}", columnName);
                }
                ColumnConverter columnConverter = columnConverterMap.get(columnName);
                if (null == columnConverter) {
                    if(logger.isInfoEnabled()) {
                       logger.info("配有匹配到字段{}", columnName); 
                    }
                    continue;
                } else {
                    cell.setColumnValue(checkContent(value, columnConverter.getName(), columnConverter.getPairs()));
                }
            }
        }


//        for (RowData row : rows) {
//            for (CellData cell : row.getCellData()) {
//                System.out.println(cell.getColumnValue());
//            }
//        }
        //记录转换策略
        if(null == sysLogDetailId) {
//            detailLog.convertExtracter(columnConverters,0);
        }
        chain.doExtract(data);

    }

    /**
     * 敏感字段替换.
     * @param value
     * @param keyword
     * @param filterVal
     * @return
     */
    private String checkContent(String value, String keyword, Verter pairs) {
        String result = value;
        
        if (null != pairs && pairs.getOriginals().contains(value)) {
            result = pairs.getReplacement();
        }
        
        if(logger.isDebugEnabled()){
            logger.debug("敏感字替换后的结果：{}", result);
        }
        return result;
    }

    /**
     * 处理字段名大小写问题
     */
    private void disposePump() {
    	if (pump == null) {
    		return;
    	}
        if (pump.getSource() == null) {
        	return;
        }
        Table table = (Table)pump.getSource();
        String dbType = table.getDb().getDbType();
        this.columnConverterMap = new HashMap<String, ColumnConverter>();
        if(logger.isInfoEnabled()){
            logger.info("数据库类型：{}", dbType);
        }
        for (ColumnConverter columnConverter : columnConverters) {
            if (!"m".equals(dbType) && !"e".equals(dbType) && !"f".equals(dbType) && !"s".equals(dbType)) {
            	columnConverterMap.put(columnConverter.getName().toUpperCase(),	columnConverter);
                if(logger.isInfoEnabled()){
                    logger.info("进入Oracle或sqlserver");
                }
            }
            else {
            	columnConverterMap.put(columnConverter.getName(), columnConverter);
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
