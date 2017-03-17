/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.data.envelope;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月13日 下午2:34:30
 */
public class BatchData {

	private FileDataBatch fdb;
	
	private RowDataBatch rdb;
	
	private Object extension;
	
	public void merge(BatchData bd) {
		if (null != bd.getFdb()) {
			List<FileData> list = this.fdb.getList();
			list = null == list ? new ArrayList<FileData>() : list;
			List<FileData> values = bd.getFdb().getList();
			if (null != values) {
				list.addAll(values);
			}
			if (null == this.fdb.getSourcePath()) {
				this.fdb.setSourcePath(bd.getFdb().getSourcePath());
			}
		}
		
		if (null != bd.getRdb()) {
			if (null == this.rdb) {
				this.rdb = new RowDataBatch();
			}
			List<RowData> list = this.rdb.getBatch();
			list =  null == list ? new ArrayList<RowData>() : list;
			list.addAll(bd.getRdb().getBatch());
			if (null == this.rdb.getLastDataDate()) {
				this.rdb.setLastDataDate(bd.getRdb().getLastDataDate());
			}
		}
	}
	
	public FileDataBatch getFdb() {
		return fdb;
	}
	
	public void setFdb(FileDataBatch fdb) {
		this.fdb = fdb;
	}
	
	public RowDataBatch getRdb() {
		return rdb;
	}
	
	public void setRdb(RowDataBatch rdb) {
		this.rdb = rdb;
	}

	public Object getExtension() {
		return extension;
	}

	public void setExtension(Object extension) {
		this.extension = extension;
	}
	
}
