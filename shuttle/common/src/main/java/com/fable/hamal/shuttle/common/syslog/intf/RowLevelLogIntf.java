package com.fable.hamal.shuttle.common.syslog.intf;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.RowData;


public interface RowLevelLogIntf {

    public void selectDate(BatchData batchData,Long sysLogDetailId);
    
    public void insertDate(RowData rowData,Long sysLogDetailId);
    
    public void updateDate(RowData rowData,Long sysLogDetailId);
    
    public void deleteDate(RowData rowData,Long sysLogDetailId);
    
    public void filterDate(BatchData batchData,Long sysLogDetailId);
}
