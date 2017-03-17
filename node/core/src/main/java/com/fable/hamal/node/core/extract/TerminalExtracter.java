/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract;

import com.fable.hamal.node.core.controller.HamalContextHelper;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.node.core.pipe.DataPipe;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * 终点抽取，不做任何抽取动作，仅仅把数据写入到datapipe中</br>
 * 使用 {@link com.fable.hamal.node.core.extract.chain.manager.ChainManager}处理
 * @author xieruidong 2013年12月18日 下午2:50:32
 */
@Deprecated
public class TerminalExtracter implements Extracter {

	private DataPipe pipe = HamalContextHelper.getBean("dataPipe");
	
	@Override
	public void start() {
		
	}

	@Override
	public boolean isStart() {
		return false;
	}

	@Override
	public void doExtract(BatchData data, ExtracterChain chain) {
		pipe.put(1L, data);
	}
}
