/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et.pretreatment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ETL策略预处理
 * @author xieruidong 2013年12月25日 上午10:40:56
 */
public class Pretreatment implements Serializable {

	private static final long serialVersionUID = -1213749488663954790L;
	private List<FilePretreatment> files = new ArrayList<FilePretreatment>();
	private List<ColumnPretreatment> sourceColumns = new ArrayList<ColumnPretreatment>();
	private List<ColumnPretreatment> targetColumns = new ArrayList<ColumnPretreatment>();
	
	public List<FilePretreatment> getFiles() {
		return files;
	}
	public void setFiles(List<FilePretreatment> files) {
		this.files = files;
	}
	public List<ColumnPretreatment> getSourceColumns() {
		return sourceColumns;
	}
	public void setSourceColumns(List<ColumnPretreatment> sourceColumns) {
		this.sourceColumns = sourceColumns;
	}
	public List<ColumnPretreatment> getTargetColumns() {
		return targetColumns;
	}
	public void setTargetColumns(List<ColumnPretreatment> targetColumns) {
		this.targetColumns = targetColumns;
	}
	
}
