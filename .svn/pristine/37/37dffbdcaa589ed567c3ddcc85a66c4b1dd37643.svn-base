/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shutte.allocation.event.service.impl;

import com.fable.hamal.shuttle.allocation.event.AllocateEvent;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.fable.hamal.shuttle.allocation.event.ExtractAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.LoadAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.PrepareAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.SelectAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.TransformAllocateEvent;
import com.fable.hamal.shuttle.allocation.manager.factory.StageManagerFactory;

/**
 * 默认SEDA事件处理服务
 * @author xieruidong 2013年11月4日 下午2:53:36
 */
public class DefaultAllocateEventServiceImpl extends AbstractAllocateEventServiceImpl {

	/**准备事件*/
	private PrepareAllocateEvent prepareAllocateEvent;
	/**查询事件*/
	private SelectAllocateEvent selectAllocateEvent;
	/**抽取事件*/
	private ExtractAllocateEvent extractAllocateEvent;
	/**转化事件*/
	private TransformAllocateEvent transformAllocateEvent;
	/**加载事件*/
	private LoadAllocateEvent loadAllocateEvent;
	
	public AllocateEventData await(Long pumpId, AllocateEvent event) throws InterruptedException {
		return event.await(pumpId);
	}

	public void awise(AllocateEventData allocateEventData, AllocateEvent event) {
		event.awise(allocateEventData);
	}

	/**S->E->T->L,通知这个@param event的下一个event,没有后续数据需要处理*/
	public void endup(Long pumpId, AllocateEvent event) {
		event.endup(pumpId);
	}
	
	public void destoryStageManager(Long pumpId) {
		StageManagerFactory.destroy(pumpId);
	}
	//----------------------------------------------------getter-------------------------------------------------------
	public PrepareAllocateEvent prepareAllocateEvent() {
		return this.prepareAllocateEvent;
	}
	
	public SelectAllocateEvent selectAllocateEvent() {
		return selectAllocateEvent;
	}

	public ExtractAllocateEvent extractAllocateEvent() {
		return this.extractAllocateEvent;
	}

	public TransformAllocateEvent transformAllocateEvent() {
		return this.transformAllocateEvent;
	}

	public LoadAllocateEvent loadAllocateEvent() {
		return this.loadAllocateEvent;
	}

	//----------------------------------------------------setter-------------------------------------------------------
	public void setExtractAllocateEvent(ExtractAllocateEvent extractAllocateEvent) {
		this.extractAllocateEvent = extractAllocateEvent;
	}

	public TransformAllocateEvent getTransformAllocateEvent() {
		return transformAllocateEvent;
	}

	public void setTransformAllocateEvent(
			TransformAllocateEvent transformAllocateEvent) {
		this.transformAllocateEvent = transformAllocateEvent;
	}

	public LoadAllocateEvent getLoadAllocateEvent() {
		return loadAllocateEvent;
	}

	public void setLoadAllocateEvent(LoadAllocateEvent loadAllocateEvent) {
		this.loadAllocateEvent = loadAllocateEvent;
	}

	public void setSelectAllocateEvent(SelectAllocateEvent selectAllocateEvent) {
		this.selectAllocateEvent = selectAllocateEvent;
	}
}
