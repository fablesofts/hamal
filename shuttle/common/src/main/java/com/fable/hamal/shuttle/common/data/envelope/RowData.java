/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.data.envelope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author xieruidong 2013年11月12日 下午4:28:42
 */
public class RowData implements Serializable,Comparable<RowData> {

	private static final long serialVersionUID = 299371568421414539L;
	
	private String tableName = "";
	
	private Long seq;
    
   
    private List<CellData> cellData = new ArrayList<CellData>();

	public List<CellData> getCellData() {
		return cellData;
	}

	public void setCellData(List<CellData> cellData) {
		this.cellData = cellData;
	}

	 public Long getSeq() {
        return seq;
    }

    
    public void setSeq(Long seq) {
        this.seq = seq;
    }


    public String getTableName() {
        return tableName;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public int compareTo(RowData tar) {
        if(((RowData)tar).getSeq()==null){
            return 1;
        } else if(this.seq==tar.getSeq()) {
            return 0;
        } 
        return ((Long)this.seq).compareTo((Long)tar.getSeq());
    }
}
