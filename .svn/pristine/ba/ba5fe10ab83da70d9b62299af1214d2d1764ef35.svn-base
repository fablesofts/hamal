/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.data.envelope;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月12日 下午4:32:06
 */
public class RowDataBatch implements Serializable {

	private static final long serialVersionUID = 7273028344427696839L;

	private List<RowData> batch;

	//这批数据中最后条数据的时间                
	private Date lastDataDate;

    public List<RowData> getBatch() {
		return batch;
	}

	public void setBatch(List<RowData> batch) {
		this.batch = batch;
	}
	
	public Date getLastDataDate() {
        return lastDataDate;
    }
    
    public void setLastDataDate(Date lastDataDate) {
        this.lastDataDate = lastDataDate;
    }
}
