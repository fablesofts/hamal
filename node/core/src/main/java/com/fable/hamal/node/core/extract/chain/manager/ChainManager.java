/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.chain.manager;

import com.fable.hamal.node.core.pipe.PipeKey;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * 过滤链管理器接口
 * @author xieruidong 2013年12月18日 上午10:36:05
 */
public interface ChainManager {

	public void process(Long pumpId, PipeKey key, BatchData data);
}
