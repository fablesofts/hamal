/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * " virusFilter":{files:["*.docx","*temp*"],columns:["column_name1","column_name2"]}
 * @author xieruidong 2013年12月23日 下午4:31:18
 */
public class VirusFilter implements Serializable {

	private static final long serialVersionUID = 7668846264756369286L;

	/**需要进行病毒过滤的文件，支持通配符*/
	private List<String> files = new ArrayList<String>();
	/**需要进行病毒过滤的大字段--必须是大字段类型否则忽略*/
	private List<String> columns = new ArrayList<String>();
	
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
}
