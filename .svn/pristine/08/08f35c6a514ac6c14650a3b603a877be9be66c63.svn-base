/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.chain;

import java.util.List;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * 
 * @author xieruidong 2013年12月23日 下午4:09:25
 */
public class StandardExtracterChain implements ExtracterChain {

	private Segment root = new Segment();
	
	@Override
	public void doExtract(BatchData data) {
		//遍历这个放射树
		
	}

	class Segment {
		List<Extracter> extracters;
		List<Segment> nexts;
		Segment prevs;
	}
}
