/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et.converter;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月5日 上午10:50:55
 */
public class ColumnConverter implements Serializable {

	private static final long serialVersionUID = 4891846127208854148L;

	private String name;
	private Verter pairs;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Verter getPairs() {
        return pairs;
    }

    public void setPairs(Verter pairs) {
        this.pairs = pairs;
    }


    public class Verter {
		
		private String replacement;
		private List<String> originals;
		
		public String getReplacement() {
			return replacement;
		}
		public void setReplacement(String replacement) {
			this.replacement = replacement;
		}
		public List<String> getOriginals() {
			return originals;
		}
		public void setOriginals(List<String> originals) {
			this.originals = originals;
		}
	}
	
	public Object convert(Object obj) {
		if (null ==  obj) {
			return null;
		}
		return obj;
	}
}
