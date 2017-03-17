/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fable.hamal.proxy.model.NetGapConfig;
import com.fable.hamal.proxy.model.NetInnerConfig;
import com.fable.hamal.proxy.model.NetOuterConfig;

/**
 * 
 * @author xieruidong 2014年4月10日 下午2:15:37
 */
public class ConfigureInDb {

	@SuppressWarnings("rawtypes")
	private Map config = null;
	private Set<String> innerSet = new HashSet<String>();
	private Set<String> outerSet = new HashSet<String>();
	private static ConfigureInDb configInDb = null;
	
	private ConfigureInDb() {
		
	}
	
	public synchronized static ConfigureInDb getInstance() {
		if (null == configInDb) {
			configInDb = new ConfigureInDb();
		}
		return configInDb;
	}
	
    public Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(Configure.get(Configure.JDBC_DRIVER));
            conn = DriverManager.getConnection(Configure.get(Configure.JDBC_URL), 
            		Configure.get(Configure.JDBC_USER), Configure.get(Configure.JDBC_PASSWORD));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return conn;
    }
    
    public static boolean isInnerIp(String ip) {
    	ConfigureInDb cfg = ConfigureInDb.getInstance();
    	if (!cfg.innerSet.contains(ip)) {
    		cfg.initIps();
    	}
    	return cfg.innerSet.contains(ip);
    }
    
    public static boolean isOuterIp(String ip) {
    	ConfigureInDb cfg = ConfigureInDb.getInstance();
    	if (!cfg.outerSet.contains(ip)) {
    		cfg.initIps();
    	}
    	return cfg.outerSet.contains(ip);
    }
    
	public synchronized void initIps() {
		Connection con = getConn();
    	ResultSet rs = null;
    	PreparedStatement ps = null;
    	String outer = "1";
    	String inner = "0";
    	try {
			ps = con.prepareStatement("SELECT SERVER_IP, DEVICE_TYPE FROM DSP_DATA_SOURCE");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String serverIp =  rs.getString("SERVER_IP");
				String deveiceType = rs.getString("DEVICE_TYPE");
				if (outer.equals(deveiceType)) {
					outerSet.add(serverIp);
				} else if (inner.equals(deveiceType)) {
					innerSet.add(serverIp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
    }
    
    @SuppressWarnings("rawtypes")
	public synchronized Map getConfig() {
    	return null == config? getConfigFromDb() : config;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private synchronized Map getConfigFromDb() {
    	Connection con = getConn();
    	ResultSet rs = null;
    	PreparedStatement ps = null;
    	
    	Map configs = new HashMap();
    	//内交换
    	try {
    		ps = con.prepareStatement("SELECT ID, HOSTNAME, TO_GAP_IP, TO_INNERNET_IP, INNER_PROXY_PORT, OUTER_PROXY_PORT, SERVICE_IN_PORT, SERVICE_OUT_PORT FROM NET_INNER_CONFIG");
    		rs = ps.executeQuery();
    		Map inner = new HashMap();
    		configs.put("inner", inner);
    		while (rs.next()) {
    			NetInnerConfig nic = new NetInnerConfig();
    			nic.setId(rs.getLong("ID"));
    			nic.setInnerProxyPort(rs.getInt("INNER_PROXY_PORT"));
    			nic.setHostname(rs.getString("HOSTNAME"));
    			nic.setOuterProxyPort(rs.getInt("OUTER_PROXY_PORT"));
    			nic.setServiceInPort(rs.getInt("SERVICE_IN_PORT"));
    			nic.setServiceOutPort(rs.getInt("SERVICE_OUT_PORT"));
    			nic.setToGapIp(rs.getString("TO_GAP_IP"));
    			nic.setToInnerNetIp(rs.getString("TO_INNERNET_IP"));
    			inner.put(nic.getHostname(), nic);
    		}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != ps) {
					ps.close();
				}
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	//外交换
    	try {
			ps = con.prepareStatement("SELECT ID, HOSTNAME, TO_GAP_IP, TO_OUTERNET_IP, INNER_PROXY_PORT, OUTER_PROXY_PORT, SERVICE_PORT FROM NET_OUTER_CONFIG");
			rs = ps.executeQuery();
			Map outer = new HashMap();
			configs.put("outer", outer);
			while (rs.next()) {
				NetOuterConfig noc = new NetOuterConfig();
				noc.setId(rs.getLong("ID"));
				noc.setHostname(rs.getString("HOSTNAME"));
				noc.setInnerProxyPort(rs.getInt("INNER_PROXY_PORT"));
				noc.setOuterProxyPort(rs.getInt("OUTER_PROXY_PORT"));
				noc.setServicePort(rs.getInt("SERVICE_PORT"));
				noc.setToGapIp(rs.getString("TO_GAP_IP"));
				noc.setToOuterNetIp(rs.getString("TO_OUTERNET_IP"));
				outer.put(noc.getHostname(), noc);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != ps) {
					ps.close();
				}
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
    	//网闸
    	try {
			ps = con.prepareStatement("SELECT ID, GAP_NAME, INNER_IP, OUTER_IP, INNER_PROXY_PORT, OUTER_PROXY_PORT, SERVICE_PORT FROM NET_GAP_CONFIG");
			rs = ps.executeQuery();
			List<NetGapConfig> gap = new ArrayList<NetGapConfig>();
			configs.put("gap", gap);
			while (rs.next()) {
				NetGapConfig ngc = new NetGapConfig();
				ngc.setId(rs.getLong("ID"));
				ngc.setGapName(rs.getString("GAP_NAME"));
				ngc.setInnerIp(rs.getString("INNER_IP"));
				ngc.setOuterIp(rs.getString("OUTER_IP"));
				ngc.setInnerProxyPort(rs.getInt("INNER_PROXY_PORT"));
				ngc.setOutProxyPort(rs.getInt("OUTER_PROXY_PORT"));
				ngc.setServicePort(rs.getInt("SERVICE_PORT"));
				gap.add(ngc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != con) {
					con.close();
				}
				if (null != ps) {
					ps.close();
				}
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return configs;
    }
}
