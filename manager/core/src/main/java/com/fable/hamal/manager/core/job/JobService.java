/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.job;

import com.fable.hamal.shuttle.common.model.envelope.data.Job;

/**
 * 
 * @author xieruidong 2013年11月21日 下午4:42:55
 */
public interface JobService {

	/**启动初始化*/
	public void start();
	/**获取Job*/
	public Job getJob(Long jobId);
}
