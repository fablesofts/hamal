/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.select.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.common.cache.config.PumpConfigCache;
import com.fable.hamal.node.core.controller.HamalContextHelper;
import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * Selector构造代理工厂，根据pump实现
 * @author xieruidong 2013年11月19日 下午3:05:24
 */
public class SelectorFactoryDelegate implements SelectorFactory {

	private static final Logger logger = LoggerFactory.getLogger(SelectorFactoryDelegate.class);
	private static final String SELECTORFACTORY_SUFFIX = "SelectorFactory";
	
	/**创建Selector*/
	public Selector createSelector(Long pumpId) {
		return createSelector(PumpConfigCache.get(pumpId));
	} 

	/**创建Selector*/
	public Selector createSelector(Pump pump) {
		String factory = pump.getSource().getFactoryPrefix() + SELECTORFACTORY_SUFFIX;
		SelectorFactory selectorFactory = HamalContextHelper.getBean(factory);
		if (null == selectorFactory) {
			logger.error("Can not find selector factory:{0}", factory);
			throw new UnsupportedOperationException("Can not find selector factory:" + factory);
		}
		return selectorFactory.createSelector(pump);
	}
}
