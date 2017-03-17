/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.chain.manager.impl;

import com.fable.hamal.node.core.extract.chain.manager.ChainManager;
import com.fable.hamal.node.core.pipe.PipeKey;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * 
 * @author xieruidong 2013年12月23日 下午3:24:08
 */
public abstract class AbstractChainManager implements ChainManager {
	
	public void processExtract(Long pumpId, PipeKey key, BatchData data) {
		
	}
}
