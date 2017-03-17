package com.fable.hamal.snmpserver.trap;

import java.io.IOException;
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
	private static Snmp SNMP = null;
	private static Address TARGET_ADDRESS;
	public static final String TRAP_ADDRESS = "udp:192.168.0.119/161";
	public static final String PDU_ADDRESS = "udp:127.0.0.1/161";
	private static final String COMMUNITY = "public";
	private static final String OID = "1.3.6.1.4.1.42882.2.1.0";
	private static final String TRAP_STRING = "aaaaaaaaaaaaaaa";

	public static void initComm(String string) throws IOException {
		// ���ý���trap��IP�Ͷ˿�
		TARGET_ADDRESS = GenericAddress.parse(string);
		TransportMapping<?> transport = new DefaultUdpTransportMapping();
		SNMP = new Snmp(transport);
		transport.listen();
	}

	public static ResponseEvent sendPDU(PDU pdu) throws IOException {
		// ���� target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(COMMUNITY));
		target.setAddress(TARGET_ADDRESS);
		// ͨ�Ų��ɹ�ʱ�����Դ���
		target.setRetries(2);
		// ��ʱʱ��
		target.setTimeout(500);
		target.setVersion(SnmpConstants.version2c);
		// ��Agent����PDU��������Response
		return SNMP.send(pdu, target);
	}

	public static void setTrap() throws IOException {
		// ����Trap PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(OID), new OctetString(TRAP_STRING)));
		pdu.setType(PDU.TRAP);
		sendPDU(pdu);
		System.out.println("Trap sent successfully.");
	}
	
	public static void setPDU(String IOD, String octetString) throws IOException {
		// set PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(IOD), new OctetString(octetString)));
		pdu.setType(PDU.SET);
		sendPDU(pdu);
	}

	public static void getPDU() throws IOException {
		// get PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(OID)));
		pdu.setType(PDU.GET);
		readResponse(sendPDU(pdu));
	}

	public static void readResponse(ResponseEvent respEvnt) {
		// ����Response
		if (respEvnt != null && respEvnt.getResponse() != null) {
			Vector<? extends VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println("��ȡPDU - " + recVB.getOid() + " - " + getChinese(recVB.getVariable().toString()));
			}
		}
	}
	
	/**
	 * ���snmp4j������������
	 */
	public static String getChinese(String octetString) {
		try {
			String[] temps = octetString.split(":");
			byte[] bs = new byte[temps.length];
			for (int i = 0; i < temps.length; i++)
				bs[i] = (byte) Integer.parseInt(temps[i], 16);
			return new String(bs, "GB2312");
		} catch (Exception e) {
			return octetString;
		}
	}
}
