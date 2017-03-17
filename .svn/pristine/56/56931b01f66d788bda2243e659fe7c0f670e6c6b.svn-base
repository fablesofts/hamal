/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.data;

/**
 * 
 * @author xieruidong 2013年11月15日 下午4:05:58
 */
public interface Source extends java.io.Serializable {
	
	/**这个源对应的Selector构造工厂id
	 * 例如源端若是RMDS中的表，则factoryPrefix="sqlTable",<bean id="sqlTableSelectorFactory" .../>
	 * 源端若是MongoDB中的Document，则factoryPrefix="mongoDoc", <bean id="mongoDocSelectorFactory" .../>
	 */
	public String getFactoryPrefix();
}
