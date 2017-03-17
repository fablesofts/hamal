package com.fable.hamal.proxy;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.proxy.model.NetInnerConfig;
import com.fable.hamal.proxy.util.Configure;
import com.fable.hamal.proxy.util.ConfigureInDb;
import com.fable.hamal.proxy.util.MachineInfo;

/**
 * 内外网代理选择器.
 * @author wuhao.
 *
 */
public class NodeProxySelector extends ProxySelector {

	private static final Logger logger = LoggerFactory.getLogger(NodeProxySelector.class);
	private final static String INNER = "inner";

	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Proxy> select(URI uri) {
	    List<Proxy> result = new LinkedList<Proxy>();
		Proxy proxy = null;
		String host = uri.getHost();
		if(logger.isInfoEnabled()) {
            logger.info("正在进行代理选择");
        }
		String hostname = MachineInfo.getHostname().toUpperCase();
		//判断是否是内交换节点信息
		String target = "socket://" + Configure.getConfigDatabaseInfo();
		System.out.println("配置库查询不走代理：" + uri.toString() + "ip:" + host);
		if (uri.toString().equals(target)) {
			result.add(Proxy.NO_PROXY);
			return result;
		}
		Map config = ConfigureInDb.getInstance().getConfig();
		NetInnerConfig nic = (NetInnerConfig)(((Map)config.get(INNER)).get(hostname));
		
//		System.out.println("网闸信息:s" + nic.getToGapIp() + ":" + nic.getServiceOutPort());
//		proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(nic.getToGapIp(), nic.getServiceOutPort()));
//		result.add(proxy);
//		if(logger.isDebugEnabled()) {
//            logger.debug("通过代理访问外网...");
//        }
		
		if (ConfigureInDb.isInnerIp(host)) {
			proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(nic.getToInnerNetIp(), nic.getServiceInPort()));
			result.add(proxy); 
			if(logger.isDebugEnabled()) {
                logger.debug("通过代理访问内网...");
            }
		} else if (ConfigureInDb.isOuterIp(host)) {
			proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(nic.getToGapIp(), nic.getServiceOutPort()));
			result.add(proxy);
			if(logger.isDebugEnabled()) {
                logger.debug("通过代理访问外网...");
            }
		} else {
			proxy = Proxy.NO_PROXY;
			result.add(proxy);
			if(logger.isDebugEnabled()) {
                logger.debug("直接访问！");
            }
		}
		
		Authenticator.setDefault(new Authenticator() {
		    
		    @Override
            protected PasswordAuthentication
                getPasswordAuthentication() {
		        
		        String threadName = Thread.currentThread().getName();
		        String threadGroupName = Thread.currentThread().getThreadGroup().getName();
		        String dataName = null;
		        if(threadName.endsWith("-selector") || threadName.endsWith("-loader")){
		            dataName = threadName;
		        }else if(threadGroupName.endsWith("-selector") || threadGroupName.endsWith("-loader")){
		            dataName = threadGroupName;
		        }
		        
		        if(dataName == null){
		            return null;
		        }
		            
		        char[] passChar = dataName.substring(dataName.lastIndexOf("-") + 1, dataName.length()).toCharArray();
		        return new PasswordAuthentication(dataName,  passChar);
		        
		    }
		    
		});
		return result;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		if (logger.isDebugEnabled()) {
			logger.error("无法连接到内外网转换器！访问对象：{}，转换器：{}，异常：{}", new Object[]{uri, sa, ioe.getMessage()});
		}
	}
}
