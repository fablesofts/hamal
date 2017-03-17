/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load.factory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.controller.HamalContextHelper;
import com.fable.hamal.node.core.load.Loader;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * Loader代理工厂，根据pump实现
 * @author xieruidong 2013年11月28日 上午11:21:55
 */
public class LoaderFactoryDelegate implements LoaderFactory {

	private static final Logger logger = LoggerFactory.getLogger(LoaderFactoryDelegate.class);
	private static final String LOADERFACTORY_SUFFIX = "LoaderFactory";
	
	public Loader createLoader(Long pumpId) {
		
		return null;
	}

	public Loader createLoader(Pump pump) {
		List<Loader> result = createLoaders(pump);
		return 0 == result.size() ? null : result.get(0);
	}

	public List<Loader> createLoaders(Long pumpId) {
		return null;
	}

	public List<Loader> createLoaders(Pump pump) {
		List<Loader> loaders = new ArrayList<Loader>();
		for (Pair pari : pump.getPairs()) {
			String factory = pari.getTarget().getFactoryPrefix() + LOADERFACTORY_SUFFIX;
			LoaderFactory loaderFactory = HamalContextHelper.getBean(factory);
			if (null == loaderFactory) {
				logger.error("Can not find loader factory:{0}", factory);
				throw new UnsupportedOperationException("Can not find loader factory:" + factory);
			}
			loaders.add(loaderFactory.createLoader(pump));
		}
		return loaders;
	}
}
