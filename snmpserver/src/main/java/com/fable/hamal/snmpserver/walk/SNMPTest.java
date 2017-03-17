package com.fable.hamal.snmpserver.walk;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class SNMPTest {

	public static final String JDBC_DRIVER;
	public static final String JDBC_URL;
	public static final String JDBC_USERNAME;
	public static final String JDBC_PASSWORD;
	public static final String TABLE_NAME;

	static {
		JDBC_DRIVER = readValue("src/main/resources/jdbc.properties", "jdbc.driverClassName");
		JDBC_URL = readValue("src/main/resources/jdbc.properties", "jdbc.url");
		JDBC_USERNAME = readValue("src/main/resources/jdbc.properties", "jdbc.username");
		JDBC_PASSWORD = readValue("src/main/resources/jdbc.properties", "jdbc.password");
		TABLE_NAME = readValue("src/main/resources/jdbc.properties", "jdbc.table");
	}

	// 当前任务数量
	private static final String CURRENT_TASKS = "select count(id) from " + TABLE_NAME + " where current_status = '1'";
	// 历史所有任务数量
	private static final String TOTAL_TASKS = "select count(id) from " + TABLE_NAME;
	// 当前流量
	private static final String CURRENT_RATE = "select sum(load_rate) + sum(select_rate) from " + TABLE_NAME
			+ " where current_status = '1'";
	// 历史流量
	private static final String TOTAL_RATE = "select sum(load_rate) + sum(select_rate) from " + TABLE_NAME;
	// 上一次成功时间
	private static final String LAST_TASK_TIME = "select max(finish_time) from " + TABLE_NAME
			+ " where current_status = '0'";

	public static void main(String[] args) throws IOException {
		// 设置当前任务数量
		String OCTETSTRING_CURRENT_TASKS = getTaskInfo(CURRENT_TASKS);
		setPDU(SnmpUtil.OID_TASKS, OCTETSTRING_CURRENT_TASKS);
		
		// 设置历史所有任务数量
		String OCTETSTRING_TOTAL_TASKS = getTaskInfo(TOTAL_TASKS);
		setPDU(SnmpUtil.OID_TOTAL_TASKS, OCTETSTRING_TOTAL_TASKS);
		
		// 设置当前流量
		String OCTETSTRING_CURRENT_RATE = getTaskInfo(CURRENT_RATE);
		setPDU(SnmpUtil.OID_RATE, OCTETSTRING_CURRENT_RATE);
		
		// 设置历史流量
		String OCTETSTRING_TOTAL_RATE = getTaskInfo(TOTAL_RATE);
		setPDU(SnmpUtil.OID_TOTAL_RATE, OCTETSTRING_TOTAL_RATE);
		
		// 设置上一次成功时间
		String OCTETSTRING_LAST_TASK_TIME = getTaskInfo(LAST_TASK_TIME);
		setPDU(SnmpUtil.OID_LAST_TASK_TIME, OCTETSTRING_LAST_TASK_TIME);
		
		// walk oid
		// snmpWalk(SnmpUtil.TARGET_OID);

		// get pdu in target oid
		System.out.println("正在运行的任务数量: " + SnmpUtil.getPDU(SnmpUtil.OID_TASKS));
		System.out.println("历史总任务数量: " + SnmpUtil.getPDU(SnmpUtil.OID_TOTAL_TASKS));
		System.out.println("当前系统流量: " + SnmpUtil.getPDU(SnmpUtil.OID_RATE));
		System.out.println("历史总流量: " + SnmpUtil.getPDU(SnmpUtil.OID_TOTAL_RATE));
		System.out.println("最后一次成功任务时间: " + SnmpUtil.getPDU(SnmpUtil.OID_LAST_TASK_TIME));
	}

	/**
	 * 解析properties
	 * 
	 * @param filePath
	 * @param key
	 * @return
	 */
	public static String readValue(String filePath, String key) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			props.load(in);
			String value = props.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取任务信息
	 * 
	 * @param sql
	 * @return
	 */
	public static String getTaskInfo(String sql) {
		String string = null;
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				string = rs.getString(1);
			}
			rs.close();
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return string;
	}

	public static void setPDU(String oid, String octetString) throws IOException {
		SnmpUtil.initComm(SnmpUtil.SNMP_SERVER_ADDRESS);
		SnmpUtil.setPDU(oid, octetString);
		// SnmpUtil.getPDU(oid);
	}

	/*
	 * public static void snmpWalk(String oid) { // TODO Auto-generated method
	 * stub String address = SnmpUtil.SNMP_SERVER_ADDRESS; OID targetOID = new
	 * OID(oid); PDU requestPDU = new PDU(); requestPDU.setType(PDU.GETNEXT);
	 * requestPDU.add(new VariableBinding(targetOID));
	 * 
	 * CommunityTarget target = SnmpUtil.createCommunityTarget(address,
	 * SnmpUtil.AGENT_COMMUNITY, SnmpUtil.DEFAULT_VERSION,
	 * SnmpUtil.DEFAULT_TIMEOUT, SnmpUtil.DEFAULT_RETRY); TransportMapping<?>
	 * transport = null; Snmp snmp = null; try { transport = new
	 * DefaultUdpTransportMapping(); snmp = new Snmp(transport);
	 * transport.listen();
	 * 
	 * boolean finished = false; while (!finished) { VariableBinding vb = null;
	 * ResponseEvent response = snmp.send(requestPDU, target); PDU responsePDU =
	 * response.getResponse();
	 * 
	 * if (null == responsePDU) { System.out.println("responsePDU == null");
	 * finished = true; break; } else { vb = responsePDU.get(0); }
	 * 
	 * // check finish finished = checkWalkFinished(targetOID, responsePDU, vb);
	 * if (!finished) { System.out.println("OID: " + vb.getOid().toString() +
	 * " - " + vb.getVariable()); // Set up the variable binding for the next
	 * entry requestPDU.setRequestID(new Integer32(0)); requestPDU.set(0, vb); }
	 * } System.out.println("success finish snmp walk!"); } catch (Exception e)
	 * { // TODO: handle exception System.out.println("SNMP walk Exception: " +
	 * e); } finally { if (snmp != null) { try { snmp.close(); } catch
	 * (IOException ex1) { snmp = null; } } if (transport != null) { try {
	 * transport.close(); } catch (IOException e2) { // TODO: handle exception }
	 * } } }
	 * 
	 * private static boolean checkWalkFinished(OID targetOID, PDU responsePDU,
	 * VariableBinding vb) { boolean finished = false; if
	 * (responsePDU.getErrorStatus() != 0) {
	 * System.out.println("responsePDU.getErrorStatus() != 0");
	 * System.out.println(responsePDU.getErrorStatusText()); finished = true; }
	 * else if (vb.getOid() == null) {
	 * System.out.println("vb.getOid() == null"); finished = true; } else if
	 * (vb.getOid().size() < targetOID.size()) {
	 * System.out.println("vb.getOid.size() < targetOID.size()"); finished =
	 * true; } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid())
	 * != 0) { System.out.println("targetOID.leftMostCompare() != 0"); finished
	 * = true; } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax()))
	 * {
	 * System.out.println("Null.isExceptionSyntax(vb.getVariable().getSyntax())"
	 * ); finished = true; } else if (vb.getOid().compareTo(targetOID) < 0) {
	 * System.out.println(
	 * "Variable received is not lexicographic successor of requested one:");
	 * System.out.println(vb.toString() + " <= " + targetOID); finished = true;
	 * } return finished; }
	 */

}