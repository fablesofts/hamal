/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.start;

import java.net.ProxySelector;

import com.fable.hamal.node.core.controller.HamalContextHelper;
import com.fable.hamal.proxy.NodeProxySelector;
/**
 * 启动类
 * @author xieruidong 2013年11月22日 下午4:55:58
 */
public class NodeMain {
	public static void main(String[] args) {
		ProxySelector.setDefault(new NodeProxySelector());
		HamalContextHelper.getTaskController().start();
	}
}
