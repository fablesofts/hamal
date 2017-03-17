/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.jobtracker;

import com.fable.hamal.log.RmiUrlService;
import com.fable.hamal.shuttle.common.utils.rmi.RmiUtil;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;

/**
 * 
 * @author xieruidong 2014年5月21日 下午5:07:15
 */
public class RmiUrlServiceImpl implements RmiUrlService {

	public final static String MANAGERS_COMMUNICATION_ADDRESS = "managers.communication.address";
	@Override
	public String getManagerRmiUrl() {
		return RmiUtil.getRmiUrl(HamalPropertyConfigurer.getHamalProperty(MANAGERS_COMMUNICATION_ADDRESS));
	}

	@Override
	public String getNodeRmiUrl() {
		return null;
	}

}
