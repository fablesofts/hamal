package com.fable.hamal.node.core.extract.extracter.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.node.core.extract.extracter.file.ClamScan;
import com.fable.hamal.node.core.extract.extracter.file.ScanResult;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.CellData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.VirusFilter;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;

/**
 * 数据库病毒过滤.
 * @author wuh
 *
 */
public class ColumnVirusFilterExtracter implements Extracter {


    private List<String> columns;
    private String ip;
    private String port;
    private ClamScan cs;
    
    public ColumnVirusFilterExtracter(VirusFilter virusFilter){
        
        columns = virusFilter.getColumns();
        ip = HamalPropertyConfigurer.getHamalProperty("node.virus.clamav.ip");
        port = HamalPropertyConfigurer.getHamalProperty("node.virus.clamav.port");
        cs = new ClamScan(ip, Integer.parseInt(port), 10000);
    }
    
    
    @Override
    public void doExtract(BatchData data, ExtracterChain chain) {

        if(null == data){
            chain.doExtract(data);
            return;
        }
        
        
       final List<RowData> rowsList = data.getRdb().getBatch();
       List<RowData> indexList = new ArrayList<RowData>();
       for (RowData row : rowsList) {
           for (CellData cell : row.getCellData()) {
               //如匹配到，则说明是需要扫描病毒的字段
               if(columns.contains(cell.getColumnName())){
                   try {
                    String state = cs.scan(cell.getColumnValue().getBytes());
                       if(ScanResult.FIALED.equals(state)){
                           //扫描到病毒后，加入List待删除
                           indexList.add(row);
                       }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
               }
           }
       }
       
       //抛掉不合法数据
       for(RowData rd : indexList){
           rowsList.remove(rd);
       }
       
     //封装被过滤的数据
       BatchData fbatchDate = new BatchData();
       RowDataBatch frowDataBatch = new RowDataBatch();
       frowDataBatch.setBatch(indexList);
       fbatchDate.setRdb(frowDataBatch);
       
       
       chain.doExtract(data);
       
    }
    
    
    
    @Override
    public void start() {

    }

    @Override
    public boolean isStart() {
        return false;
    }

   



}
