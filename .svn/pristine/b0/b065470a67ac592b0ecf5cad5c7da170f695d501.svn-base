/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.handler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.proxy.model.NetGapConfig;
import com.fable.hamal.proxy.model.NetInnerConfig;
import com.fable.hamal.proxy.util.ConfigureInDb;
import com.fable.hamal.proxy.util.HamalContextHelper;
import com.fable.hamal.proxy.util.MachineInfo;
import com.fable.hamal.proxy.util.Statistics;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;

/**
 * 
 * @author wuhao,xieruidong 2014年4月9日 下午2:11:11
 */
public class MiddleProxyIoHandler extends IoHandlerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(MiddleProxyIoHandler.class);
	private boolean flux = false;
	private Map config = ConfigureInDb.getInstance().getConfig();
	public final static Map<Long, IoSession> sessionMap = new HashMap<Long, IoSession>();
	private final static Map<Long, Integer> countMap = new HashMap<Long, Integer>();
	private final static Map<Long, String> serialMap = new HashMap<Long, String>();
	//流量统计容器
    public final static Map<String, Long> dataSizeMap = new HashMap<String, Long>();
	private final static String GAP = "gap";
	private Statistics statistics;
	
	public MiddleProxyIoHandler() {
		statistics = new Statistics(dataSizeMap);
		HamalContextHelper.autowire(statistics);
		statistics.start();
	}
	
	public MiddleProxyIoHandler(boolean flux) {
		this.flux = flux;
		statistics = new Statistics(dataSizeMap);
		HamalContextHelper.autowire(statistics);
		statistics.start();
	}
	
	@SuppressWarnings({ "unchecked"})
	@Override
    public void messageReceived(IoSession session, Object message) throws Exception {
	    if(logger.isInfoEnabled()){
	        logger.info("############################enter into middle proxy##################################");   
	    }
	    if (!countMap.containsKey(session.getId())) {
            countMap.put(session.getId(), 1);
        } else {
            int j = countMap.get(session.getId());
            countMap.put(session.getId(), ++j);
        }
	    
        if(!sessionMap.containsKey(session.getId())) {
        	List<NetGapConfig> gapList = (List<NetGapConfig>)config.get(GAP);
        	for (int i = 0; i < gapList.size(); i++) {
        		NetGapConfig ngc = gapList.get(i);
	        	try {
	        	    createClient(new InetSocketAddress(ngc.getInnerIp(), ngc.getServicePort()), session);
	        	    break;
	        	} catch(Throwable tx) {
	        		logger.error("网闸:{},连接不上,请检查网络配置", ngc.getGapName());
	        		if (i == gapList.size() - 1) {
	        			logger.error("所有网闸连接失败，请检查网络配置");
	        			return;
	        		}
	        		continue;
	        	}
        	}
        }
        
        IoSession se = sessionMap.get(session.getId());
        IoBuffer br = (IoBuffer) message;
        final byte[] buf = new byte[br.limit()];
        br.get(buf, 0, br.limit());
        //获取username流量信息
        if (countMap.get(session.getId()) == 2) {
            byte[] bufTemp = new byte[br.limit()];
            System.arraycopy(buf,0,bufTemp,0,buf.length);
            handShake(bufTemp, session);
        }
        //前3次是握手信息，不计流量
        if(countMap.get(session.getId()) > 3){
         // 统计Loader流量
            synchronized (dataSizeMap) {
                addTraffic(session.getId(), br.limit(), "loader");
            }
        }
        se.write(IoBuffer.wrap(buf, 0, br.limit()));
    }
    
    private boolean handShake(final byte[] bufTemp, final IoSession session) {
        
        int num = bufTemp[0];
        if(num != 1){
            return false;
        }
        num = bufTemp[1];
        String username = new String(bufTemp, 2, num);

        if ((username.contains("selector") || username.contains("loader")) && username.split("-").length > 4) {
            String[] flows = username.split("-");
            // 保存统计流量, value的格式如：taskid-serial-selector
            serialMap.put(session.getId(), new StringBuffer(flows[0]).append("-").append(flows[3]).append("-").append(flows[4]).toString());
            dataSizeMap.put(serialMap.get(session.getId()), Long.parseLong("0"));
        }
        
        return true;
    }

    /**
     * 创建客户端,连接到其他代理，可以是中间节点也可以是终端节点
     */
    private void createClient(InetSocketAddress isd, final IoSession previousSession) {
        
        IoConnector connector = new NioSocketConnector();
        connector.setHandler(new IoHandlerAdapter() {
        	@Override
            public void messageReceived(IoSession nextSession, Object message) throws Exception {
                IoBuffer br = (IoBuffer) message;
                final byte[] buf = new byte[br.limit()];
                br.get(buf, 0, br.limit());
                
                //前3次是握手信息，不计流量
                if(countMap.get(previousSession.getId()) > 3){
                 // 统计Selector流量
                    synchronized (dataSizeMap) {
                        addTraffic(previousSession.getId(), br.limit(), "selector");
                    }
                }
                
                previousSession.write(IoBuffer.wrap(buf, 0, br.limit()));
                if(logger.isInfoEnabled()){
                    logger.info("############################Middle Proxy Client receive message !##################################");   
                }
            }
        });
        ConnectFuture cf = connector.connect(isd);
        cf.awaitUninterruptibly();
        sessionMap.put(previousSession.getId(), cf.getSession());
        Thread thread = new Thread(new CloseClient(connector, previousSession.getId()));
        thread.start();
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
    
    
    /**
     * 销毁Client端.
     */
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
            session.close(false);
            connector.dispose();
        }
    }
}
