/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.event;

/**
 * 
 * @author xieruidong 2013年11月1日 下午2:04:18
 */
public interface AllocateEvent {
	
	/**等待一个事件预处理完成*/
	public AllocateEventData await(Long pumpId) throws InterruptedException;
	/**发送事件通知下一阶段任务进行后续处理*/
	public void awise(AllocateEventData allocateEventData);
	/**发送事件通知下一阶段任务*/
	public void endup(Long pumpId);
}
