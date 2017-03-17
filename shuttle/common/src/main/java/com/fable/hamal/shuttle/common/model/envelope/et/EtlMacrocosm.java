/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et;

import java.io.Serializable;
import java.util.Map;

import com.fable.hamal.shuttle.common.model.envelope.et.converter.Converter;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.Filter;
import com.fable.hamal.shuttle.common.model.envelope.et.mapping.Mapping;
import com.fable.hamal.shuttle.common.model.envelope.et.pretreatment.Pretreatment;

/**
 * ET 策略的全体配置信息
 * @author xieruidong 2013年11月5日 上午11:34:51
 */
public class EtlMacrocosm implements Serializable {
	
	private static final long serialVersionUID = -6643552721610797903L;
	
	private Pretreatment pretreatment = new Pretreatment();
	private Mapping mapping = new Mapping();
	private Filter filter = new Filter();
	private Converter converter = new Converter();
	private Map<?, ?> extension;

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Pretreatment getPretreatment() {
		return pretreatment;
	}

	public void setPretreatment(Pretreatment pretreatment) {
		this.pretreatment = pretreatment;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public Map<?, ?> getExtension() {
		return extension;
	}

	public void setExtension(Map<?, ?> extension) {
		this.extension = extension;
	}
	
}
