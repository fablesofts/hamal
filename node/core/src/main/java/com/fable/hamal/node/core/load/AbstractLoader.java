/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load;

import com.fable.hamal.log.JobTracker;

/**
 * 
 * @author xieruidong 2014年5月22日 下午2:26:52
 */
public abstract class AbstractLoader implements Loader {
	
	private JobTracker jobTracker;

	public JobTracker getJobTracker() {
		return jobTracker;
	}

	public void setJobTracker(JobTracker jobTracker) {
		this.jobTracker = jobTracker;
	}
}
