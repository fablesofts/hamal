/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.chain;

import java.util.LinkedList;
import java.util.List;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.manager.impl.SimpleChainManager;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * 简单抽取器链实现,和{@link SimpleChainManager}相对应</br>
 * 修改两者中任何一个，都要修改另一个
 * @author xieruidong 2013年12月18日 下午4:03:15
 */
public class SimpleExtracterChain implements LinkExtracterChain {

	/**过滤链*/
	private LinkedList<Extracter> chain = new LinkedList<Extracter>();
	
	/**依次执行Extracter处理器*/
	@Override
	public void doExtract(BatchData data) {
		Extracter next = chain.poll();
		if (null != next) {
			next.doExtract(data, this);
		}
	}

	@Override
	public void appendExtracter(Extracter extracter) {
		chain.addLast(extracter);
	}
}
