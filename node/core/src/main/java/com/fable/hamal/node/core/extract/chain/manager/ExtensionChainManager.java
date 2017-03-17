/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.chain.manager;

import java.util.List;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.node.core.pipe.PipeKey;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * 
 * @author xieruidong 2013年12月18日 下午5:19:17
 */
public interface ExtensionChainManager {

	public List<Extracter> getExtensionExtracters(Long pairId, PipeKey key, BatchData data);
	
	public ExtracterChain getExtracterChain(Long pumpId, PipeKey key, BatchData data);
}
