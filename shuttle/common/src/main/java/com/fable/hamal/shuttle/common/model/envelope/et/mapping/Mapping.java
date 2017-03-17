/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月5日 上午9:47:15
 */
public class Mapping  implements Serializable {
	
	private static final long serialVersionUID = -2198140185384266358L;
	
	private List<ColumnMapping> columnsMapping = new ArrayList<ColumnMapping>();

	public List<ColumnMapping> getColumnsMapping() {
		return columnsMapping;
	}

	public void setColumnsMapping(List<ColumnMapping> columnsMapping) {
		this.columnsMapping = columnsMapping;
	}
	
}
