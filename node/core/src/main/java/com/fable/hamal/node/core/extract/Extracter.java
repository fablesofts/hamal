/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract;

import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * 抽取器接口
 * @author xieruidong 2013年11月5日 下午4:48:40
 */
public interface Extracter {

	/**启动抽取器*/
	public void start();
	/**判断抽取器是否已经工作*/
	public boolean isStart();
	/**处理数据*/
	public void doExtract(BatchData data, ExtracterChain chain);
}
