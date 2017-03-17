/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.schedule.job.factory;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * <property name="applicationContextSchedulerContextKey" value="context" />
 * 使用方式替代
 * <bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
 *      <property name="jobFactory">
 *          <bean class="me.arganzheng.study.quartz.task.SpringBeanJobFactory" />
 *      </property>
 *      ...
 * </bean>
 * @author xieruidong 2014年5月14日 下午3:19:04
 */
public class HamalScheduleJobFactory extends SpringBeanJobFactory  implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(jobInstance);
        return jobInstance;
    }

}
