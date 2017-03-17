/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月5日 上午9:50:10
 */
public class Filter implements Serializable {

	private static final long serialVersionUID = -5670780695771074666L;

	private VirusFilter virusFilter = new VirusFilter();
	
	private List<FilecntFilter> filecntFilter = new ArrayList<FilecntFilter>();
	
	private List<ColumnFilter> columnFilter = new ArrayList<ColumnFilter>();

	public VirusFilter getVirusFilter() {
		return virusFilter;
	}

	public void setVirusFilter(VirusFilter virusFilter) {
		this.virusFilter = virusFilter;
	}

	public List<FilecntFilter> getFilecntFilter() {
		return filecntFilter;
	}

	public void setFilecntFilter(List<FilecntFilter> filecntFilter) {
		this.filecntFilter = filecntFilter;
	}

	public List<ColumnFilter> getColumnFilter() {
		return columnFilter;
	}

	public void setColumnFilter(List<ColumnFilter> columnFilter) {
		this.columnFilter = columnFilter;
	}
	
}
