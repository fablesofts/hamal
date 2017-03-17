/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.proxy.util.HamalContextHelper;
import com.fable.hamal.proxy.util.Statistics;
import com.fable.hamal.shuttle.common.utils.Utility;

/**
 * 
 * @author wuhao, xieruidong 2014年4月11日 下午2:43:59
 */
public class ProxyIoHandler extends IoHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ProxyIoHandler.class);
	private boolean flux;
	private boolean handshake;
	
	public final static String MESSAGE_ERROR = "客户端返回报文错误！";
	public final static String COMMUNICATION_ERROR = "通讯版本错误:";
	private int http = 1;//0表示http代理，1表示sock5代理
	private final static Map<Long, IoSession> sessionMap = new HashMap<Long, IoSession>();
	private final static Map<Long, Integer> countMap = new HashMap<Long, Integer>();
	private final static Map<Long, String> serialMap = new HashMap<Long, String>();
	//流量统计容器
	public final static Map<String, Long> dataSizeMap = new ConcurrentHashMap<String, Long>();
	// 预留属性
	private String username;
	private Statistics statistics;
	
	public ProxyIoHandler() {
		statistics = new Statistics(dataSizeMap);
		HamalContextHelper.autowire(statistics);
		statistics.start();
	}
	
	public ProxyIoHandler(boolean flux) {
		this.flux = flux;
		statistics = new Statistics(dataSizeMap);
		HamalContextHelper.autowire(statistics);
		statistics.start();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (!countMap.containsKey(session.getId())) {
			countMap.put(session.getId(), 1);
		} else {
			int j = countMap.get(session.getId());
			countMap.put(session.getId(), ++j);
		}

		IoBuffer br = (IoBuffer) message;
		// 同一会话的前3次是握手信息
		if (countMap.get(session.getId()) <= (http == 0 ? 2 : 3)) {
			handShake(br, session);
		} else {
			IoSession se = sessionMap.get(session.getId());
			final byte[] buf = new byte[br.limit()];
			br.get(buf, 0, br.limit());
			// 统计Loader流量
			synchronized (dataSizeMap) {
				addTraffic(session.getId(), br.limit(), "loader");
			}
			se.write(IoBuffer.wrap(buf, 0, br.limit()));
		}
	}

	/**
	 * 创建客户端.
	 * @param isd
	 * @param session
	 */
	private void createClient(InetSocketAddress isd, IoSession session) {
		IoConnector connector = new NioSocketConnector();
		connector.setHandler(new MinaProxyClientIoHandler(session));
		ConnectFuture cf = connector.connect(isd);
		cf.awaitUninterruptibly();
		sessionMap.put(session.getId(), cf.getSession());
		Thread thread = new Thread(new CloseClient(connector, session.getId()));
		thread.start();
	}

	/**
	 * 与客户端握手.
	 * @throws IOException
	 */
	private void handShake(IoBuffer rb, IoSession session) throws IOException {

		final byte[] buf = new byte[512];
		IOException connException = null;
		int iTmpRead = 0;
		// 一次握手
		if (countMap.get(session.getId()) == 1) {

			iTmpRead = rb.get();
			if (iTmpRead != 5) {
				final StringBuffer strError = new StringBuffer(COMMUNICATION_ERROR + iTmpRead);
				throw new IOException(strError.toString());
			}
			boolean bFindUP = false;
			iTmpRead = rb.get();
			final IoBuffer iRead = rb.get(buf, 0, iTmpRead);
			for (int i = 0; i < iRead.limit(); i++) {
				if (buf[i] == (http == 0 ? 0 : 2)) {
					bFindUP = true;
					break;
				}
			}
			if (!bFindUP) {
				throw new IOException("客户端必须支持用户名，密码方式！");
			}
			if (http == 0) {
				session.write(IoBuffer.wrap(new byte[] { 5, 0 }));
			} else {
				session.write(IoBuffer.wrap(new byte[] { 5, 2 }));
			}

		}

		// 二次握手
		if (http != 0) {
			if (countMap.get(session.getId()) == 2) {
				iTmpRead = rb.get();
				if (iTmpRead != 1) {
					throw new IOException(MESSAGE_ERROR + iTmpRead);
				}
				iTmpRead = rb.get();
				rb.get(buf, 0, iTmpRead);
				username = new String(buf, 0, iTmpRead);
				// 判断是否是交换流程
				if ((username.contains("selector") || username.contains("loader")) && username.split("-").length > 4) {
				    String[] flows = username.split("-");
					// 保存统计流量, value的格式如：taskid-pipelineid-serial-selector
				    serialMap.put(session.getId(), new StringBuffer(flows[0]).append("-").append(flows[3]).append("-").append(flows[4]).toString());
				    dataSizeMap.put(serialMap.get(session.getId()), Long.parseLong("0"));
				}
				iTmpRead = rb.get();
				session.write(IoBuffer.wrap(new byte[] { 1, 0 }));
			}
		}

		// 三次握手
		if (countMap.get(session.getId()) == (http == 0 ? 2 : 3)) {
			int iType = 0;
			byte[] port = new byte[2];
			byte[] host = null;
			iTmpRead = rb.get();
			if (iTmpRead != 5) {
				throw new IOException(COMMUNICATION_ERROR + iTmpRead);
			}
			iTmpRead = rb.get();
			if (iTmpRead != 1) {
				throw new IOException(MESSAGE_ERROR + iTmpRead);
			}
			iTmpRead = rb.get();
			if (iTmpRead != 0) {
				throw new IOException(MESSAGE_ERROR + iTmpRead);
			}

			iType = rb.get();
			if (iType == 1) { // IPV4
				host = new byte[4];
				rb.get(host, 0, 4);
				rb.get(port, 0, 2);
				// 创建代理客户端
				this.createClient(new InetSocketAddress(InetAddress.getByAddress(host), Utility.byte2Int(port)), session);
			} else if (iType == 3) { // DOMAINNAME
				iTmpRead = rb.get();
				host = new byte[iTmpRead];
				rb.get(host, 0, iTmpRead);
				rb.get(port, 0, 2);

				this.createClient(new InetSocketAddress(InetAddress.getByAddress(host), Utility.byte2Int(port)), session);
			} else if (iType == 4) { //IPV6
				session.write(IoBuffer.wrap(new byte[] { 5, 8, 0, 1, 127, 0, 0, 1, port[2], port[3], }));
				throw new IOException("目前不支持ipv6方式！");
			} else {
				session.write(IoBuffer.wrap(new byte[] { 5, 8, 0, 1, 127, 0, 0, 1, port[2], port[3], }));
				throw new IOException("不支持的服务器类型！" + iTmpRead);
			}

			port = Utility.int2byte(1090);
			session.write(IoBuffer.wrap(new byte[] { 5, (byte) (connException == null ? 0 : 4), 0, 1, 127, 0, 0, 1,	port[2], port[3], }));

			if (logger.isInfoEnabled()) {
				logger.info("握手成功！");
			}
		}
	}

	/**MINA代理客户端.*/
	private class MinaProxyClientIoHandler extends IoHandlerAdapter {

		private IoSession m_session;
		
		public MinaProxyClientIoHandler(IoSession session) {
			m_session = session;
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			IoBuffer br = (IoBuffer) message;
			final byte[] buf = new byte[br.limit()];
			br.get(buf, 0, br.limit());
			// 统计Selector流量
			synchronized (dataSizeMap) {
				addTraffic(m_session.getId(), br.limit(), "selector");
			}
			m_session.write(IoBuffer.wrap(buf, 0, br.limit()));
		}
	}

	/**
	 * 统计流量.
	 * @param traffic
	 * @param flowSize
	 */
	private void addTraffic(final long sessionId, int flowSize, String operate) {
	    
	    String flow = serialMap.get(sessionId);
	    
	    if(null == flow || "".equals(flow)){
	        return;
	    }
	    
	    if(!operate.equals(flow.split("-")[flow.split("-").length - 1])){
	        return;
	    }
	    
	    long size = dataSizeMap.get(flow);
		
		size = size + flowSize;
		
		dataSizeMap.put(flow, size);

	}

	/**销毁Client端.*/
	private class CloseClient implements Runnable {

		private IoConnector connector;
		private long id;

		public CloseClient(IoConnector connector, long id) {
			this.connector = connector;
			this.id = id;
		}

		@Override
		public void run() {
			IoSession session = sessionMap.get(id);
			session.getCloseFuture().awaitUninterruptibly();
			sessionMap.remove(session.getId());
			countMap.remove(session.getId());
			session.close(false);
			connector.dispose();
		} 
	}
}
