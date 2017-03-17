package com.fable.hamal.node.core.extract.extracter.db;

import java.util.ArrayList;
import java.util.List;

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
import com.fable.hamal.shuttle.common.model.envelope.et.mapping.ColumnMapping;
import com.fable.hamal.shuttle.common.model.envelope.et.mapping.Mapping;

public class ColumnMappingExtracter implements Extracter  {
    
    private final static Logger logger = LoggerFactory.getLogger(ColumnMappingExtracter.class);
    
    private Pump pump;
    
    private List<ColumnMapping> columnsMapping;
    
    public ColumnMappingExtracter(){
        
    }
    
    public ColumnMappingExtracter(List<ColumnMapping> columnMapping){
        this.columnsMapping = columnMapping;
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
        if(logger.isDebugEnabled()){
            logger.debug("*****进入字段映射功能******");
        }
        System.out.println(columnsMapping);
        System.out.println(columnsMapping.size());
        if (columnsMapping.isEmpty()) {
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
        
        List<RowData> rowsList = data.getRdb().getBatch();
        for (RowData row : rowsList) {
            for (CellData cell : row.getCellData()) {
                String targetColumn = disposeColumn(cell.getColumnName());
                cell.setColumnName(targetColumn);
            }
        }
        
        

        //创建一个新的BatchData
//        BatchData bData = new BatchData();
//        RowDataBatch rdBatch = new RowDataBatch();
//        List<RowData> rowsList = new ArrayList<RowData>();
//        for (RowData row : rows) {
//            RowData rd = new RowData();
//            List<CellData> cdList = new ArrayList<CellData>();
//            for (CellData cell : row.getCellData()) {
//                CellData cd = disposeCellData(cell);
//                String targetColumn = disposeColumn(cd.getColumnName());
//                cd.setColumnName(targetColumn);
//                System.out.println(cd.getColumnValue());
//                cdList.add(cd);
//            }
//            rd.setCellData(cdList);
//            rd.setTableName(rd.getTableName());
//            rowsList.add(rd);
//        }
//        rdBatch.setBatch(rowsList);
//        bData.setRdb(rdBatch);
        
        
        
        
//        List<RowData> list = data.getRdb().getBatch();
//        for(RowData row : list){
//            for (CellData cell : row.getCellData()) {
//                System.out.println(cell.getColumnName() + "-->" + cell.getColumnValue());
//            }
//        }
        
        chain.doExtract(data);
        
    }
    
    /**
     * 处理字段名大小写问题
     */
    private void disposePump(){
        if(pump == null)
            return;
        if(pump.getSource() == null)
            return;
        Table table = (Table)pump.getSource();
        String dbType = table.getDb().getDbType();
        if(!"m".equals(dbType) && !"e".equals(dbType) && !"f".equals(dbType) && !"s".equals(dbType)){
            for(ColumnMapping cm : columnsMapping){
                cm.setSource(cm.getSource().toUpperCase());
            }
        }
    }
    
    /**
     * 匹配字段映射的列名称.
     * @param value
     * @return
     */
    private String disposeColumn(String value){

        String column = value;
        for(ColumnMapping cm : columnsMapping){
            if(logger.isDebugEnabled()){
                logger.debug("原字段与目标字段匹配：{} --> {}",value, cm.getSource());
            }
            if(value.equals(cm.getSource())){
                return column = cm.getTarget();
            }
        }
        
        return column;
        
    }

    
    private CellData disposeCellData(CellData cd){
        
        CellData cData = new CellData();
        cData.setColumnIndex(cd.getColumnIndex());
        cData.setColumnName(cd.getColumnName());
        cData.setColumnType(cd.getColumnType());
        cData.setColumnValue(cd.getColumnValue());
        cData.setKey(cd.isKey());
        cData.setNull(cd.isNull());
        cData.setRowIndex(cd.getRowIndex());
        cData.setType(cd.getType());
        cData.setValue(cd.getValue());
        
        return cData;
        
    }

    
    public Pump getPump() {
        return pump;
    }

    
    public void setPump(Pump pump) {
        this.pump = pump;
        disposePump();
    }
    
    
    
}
