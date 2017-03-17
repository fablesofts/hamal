/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.fable.hamal.proxy.handler.MiddleProxyIoHandler;
import com.fable.hamal.proxy.handler.ProxyIoHandler;
import com.fable.hamal.proxy.model.NetInnerConfig;
import com.fable.hamal.proxy.model.NetOuterConfig;
import com.fable.hamal.proxy.util.Configure;
import com.fable.hamal.proxy.util.ConfigureInDb;
import com.fable.hamal.proxy.util.MachineInfo;

/**
 * 
 * @author xieruidong 2014年4月4日 下午2:33:49
 */
public class ProxyServer {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
	private final static String INNER = "inner";
	private final static String OUTER = "outer";
	private final static String GAP = "gap";
	
	public static void main(String[] args) {
		
		if (null == args || args.length == 0) {
			usage();
		}
		Set<String> argsSet = new HashSet<String>();
		for (String arg : args) {
			if (!"".equals(arg)) {
				argsSet.add(arg);
			}
		}
		
		if (argsSet.contains("-i") && argsSet.contains("-o")) {
			usage();
		}
		
		if (!argsSet.contains("-i") && !argsSet.contains("-o")) {
			usage();
		}
		
		//关闭程序后续处理
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("ProxyServer is going down!");
			}
		});
		
		String hostname = MachineInfo.getHostname().toUpperCase();
		Map config = ConfigureInDb.getInstance().getConfig();
		try {
			//内交换上所有代理
			if (argsSet.contains("-i")) {
//				int tin = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_INNER_TRANSFER_IN_PORT)).intValue();
//				int tout = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_INNER_TRANSFER_OUT_PORT));
				NetInnerConfig nic = (NetInnerConfig)(((Map)config.get(INNER)).get(hostname));
				int tin = nic.getServiceInPort();
				int tout = nic.getServiceOutPort();
				
				SocketAcceptor transInAcceptor = new NioSocketAcceptor();
				transInAcceptor.setHandler(new ProxyIoHandler(true));
				transInAcceptor.bind(new InetSocketAddress(tin));
				if (logger.isInfoEnabled()) {
					logger.info("内交换对内网的数据交换代理使用的端口:{}", tin);
				}
				
				SocketAcceptor transOutAcceptor = new NioSocketAcceptor();
				transOutAcceptor.setHandler(new MiddleProxyIoHandler(true));
				transOutAcceptor.bind(new InetSocketAddress(tout));
				if (logger.isInfoEnabled()) {
					logger.info("内交换对外交换的数据交换代理使用的端口:{}", tout);
				}
				
				if (argsSet.contains("-ain")) {
//					int ain = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_INNER_APP_IN)).intValue();
					int ain = nic.getInnerProxyPort();
					SocketAcceptor appInAcceptor = new NioSocketAcceptor();
					appInAcceptor.setHandler(new ProxyIoHandler(true));
					appInAcceptor.bind(new InetSocketAddress(ain));
					if (logger.isInfoEnabled()) {
						logger.info("内交换上外网访问内网的应用代理使用的端口:{}", ain);
					}
				}
				
				if (argsSet.contains("-aout")) {
//					int aout = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_INNER_APP_OUT)).intValue();
					int aout = nic.getOuterProxyPort();
					SocketAcceptor appInAcceptor = new NioSocketAcceptor();
					appInAcceptor.setHandler(new MiddleProxyIoHandler(true));
					appInAcceptor.bind(new InetSocketAddress(aout));
					if (logger.isInfoEnabled()) {
						logger.info("内交换上内网访问外网的应用代理使用的端口:{}", aout);
					}
				}
				
			}
			
			//外交换上所有代理
			if (argsSet.contains("-o")) {
//				int tin = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_OUTER_TRANSFER_IN_PORT)).intValue();
				NetOuterConfig noc = (NetOuterConfig)(((Map)config.get(OUTER)).get(hostname));
				int tin = noc.getServicePort();
				SocketAcceptor transInAcceptor = new NioSocketAcceptor();
				transInAcceptor.setHandler(new ProxyIoHandler(true));
				transInAcceptor.bind(new InetSocketAddress(tin));
				if (logger.isInfoEnabled()) {
					logger.info("外交换对内交换的数据交换代理使用的端口:{}", tin);
				}
				
				if (argsSet.contains("-ain")) {
					int ain = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_OUTER_APP_IN)).intValue();
					SocketAcceptor appInAcceptor = new NioSocketAcceptor();
					appInAcceptor.setHandler(new ProxyIoHandler(true));
					appInAcceptor.bind(new InetSocketAddress(ain));
					if (logger.isInfoEnabled()) {
						logger.info("外交换上内网访问外网代理使用的端口:{}", ain);
					}
				}
				
				if (argsSet.contains("-aout")) {
					int aout = Integer.valueOf(Configure.get(Configure.DISTRIBUTE_OUTER_APP_OUT)).intValue();
					SocketAcceptor appInAcceptor = new NioSocketAcceptor();
					appInAcceptor.setHandler(new MiddleProxyIoHandler(true));
					appInAcceptor.bind(new InetSocketAddress(aout));
					if (logger.isInfoEnabled()) {
						logger.info("外交换上外网访问内网代理使用的端口:{}", aout);
					}
				}
			}
        } catch (IOException e) {
        	if (logger.isErrorEnabled()) {
        		logger.error("启动失败请检查配置是否正确");
        		logger.error(e.getMessage());
        	}
            System.exit(-1);
        }
	}
	
	public static void usage() {
		if (logger.isErrorEnabled()) {
    		logger.error("代理启动方法：ProxyServer -i -ain[ -aout]|| ProxyServer -o -ain[ -aout]");
    	} else {
    		System.err.println("代理启动方法：ProxyServer -i -ain[ -aout]|| ProxyServer -o -ain[ -aout]");
    	}
		System.exit(-1);
	}
}