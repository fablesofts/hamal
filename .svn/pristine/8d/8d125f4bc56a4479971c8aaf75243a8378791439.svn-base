package com.fable.hamal.snmpserver.walk;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpUtil {
	public static final String AGENT_COMMUNITY = "public";
	public static final String TARGET_OID = "1.3.6.1.4.1.42882.2";

	public static final int DEFAULT_VERSION = SnmpConstants.version2c;
	public static final String DEFAULT_PROTOCLO = "udp";
	public static final String DEFAULT_PORT = "161";
	public static final long DEFAULT_TIMEOUT = 3 * 1000L;
	public static final int DEFAULT_RETRY = 3;

	// count current tasks, OID: 1.3.6.1.4.1.42882.2.1.0
	public static final String OID_TASKS = "1.3.6.1.4.1.42882.2.1.0";
	// count total tasks, OID: 1.3.6.1.4.1.42882.2.2.0
	public static final String OID_TOTAL_TASKS = "1.3.6.1.4.1.42882.2.2.0";
	// current working rate, OID: 1.3.6.1.4.1.42882.2.3.0
	public static final String OID_RATE = "1.3.6.1.4.1.42882.2.3.0";
	// total working rate, OID: 1.3.6.1.4.1.42882.2.4.0
	public static final String OID_TOTAL_RATE = "1.3.6.1.4.1.42882.2.4.0";
	// last succeed task work time, OID: 1.3.6.1.4.1.42882.2.5.0
	public static final String OID_LAST_TASK_TIME = "1.3.6.1.4.1.42882.2.5.0";

	private static Snmp SNMP = null;
	private static Address TARGETADDRESS;
	private static String SNMP_SERVER_IP;
	public static String SNMP_SERVER_ADDRESS;

	static {
		SNMP_SERVER_IP = readValue("src/main/resources/snmp.properties", "snmp.server.ip");
		SNMP_SERVER_ADDRESS = SnmpUtil.DEFAULT_PROTOCLO + ":" + SNMP_SERVER_IP + "/" + SnmpUtil.DEFAULT_PORT;
	}

	public static void initComm(String string) throws IOException {
		// set trap target address
		TARGETADDRESS = GenericAddress.parse(string);
		TransportMapping<?> transport = new DefaultUdpTransportMapping();
		SNMP = new Snmp(transport);
		transport.listen();
	}

	public static ResponseEvent sendPDU(PDU pdu) throws IOException {
		// set target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(AGENT_COMMUNITY));
		target.setAddress(TARGETADDRESS);
		// set retry times
		target.setRetries(DEFAULT_RETRY);
		// set timeout
		target.setTimeout(DEFAULT_TIMEOUT);
		target.setVersion(DEFAULT_VERSION);
		// set SNMP PDU for Response
		return SNMP.send(pdu, target);
	}

	public static void setPDU(String oid, String octetString) throws IOException {
		// set PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid), new OctetString(octetString)));
		pdu.setType(PDU.SET);
		sendPDU(pdu);
	}

	public static String getPDU(String oid) throws IOException {
		// get PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(PDU.GET);
		return readResponse(sendPDU(pdu));
	}

	public static String readResponse(ResponseEvent respEvnt) {
		return respEvnt.getResponse().getVariableBindings().elementAt(0).getVariable().toString();
	}

	public static CommunityTarget createCommunityTarget(Address targetAddress, String community, int version,
			long timeout, int retry) {
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(targetAddress);
		target.setVersion(version);
		target.setTimeout(timeout);
		target.setRetries(retry);
		return target;
	}

	public static CommunityTarget createCommunityTarget(String address, String community, int version, long timeout,
			int retry) {
		Address targetAddress = GenericAddress.parse(address);
		return createCommunityTarget(targetAddress, community, version, timeout, retry);
	}

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
}
