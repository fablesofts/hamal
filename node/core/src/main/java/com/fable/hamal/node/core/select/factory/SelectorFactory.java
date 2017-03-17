/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.select.factory;

import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * selector 构造工厂接口
 * @author xieruidong 2013年11月14日 下午7:09:17
 */
public interface SelectorFactory {

	public Selector createSelector(Long pumpId);
	
	public Selector createSelector(Pump pump);
}
