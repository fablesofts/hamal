package com.fable.hamal.snmpserver.pdu;

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
	private static Address TARGETADDRESS;
	public static String TRAPUDPADDR = "udp:192.168.0.76/161";
	public static String PDUADDR = "udp:localhost/161";
	private static final String OID = "1.3.6.1.4.1.42882.2.4.0";
	private static final String OCTETSTRING = "ccc";

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
		target.setCommunity(new OctetString("public"));
		target.setAddress(TARGETADDRESS);
		//set retry times 
		target.setRetries(2);
		// set timeout
		target.setTimeout(500);
		target.setVersion(SnmpConstants.version2c);
		// set SNMP PDU for Response
		return SNMP.send(pdu, target);
	}

	public static void setTrap() throws IOException {
		// send Trap PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(OID), new OctetString("test")));
		pdu.setType(PDU.TRAP);
		sendPDU(pdu);
		System.out.println("Trap sent successfully.");
	}

	public static void setPDU() throws IOException {
		// set PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(OID), new OctetString(OCTETSTRING)));
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
		// get Response
		if (respEvnt != null && respEvnt.getResponse() != null) {
			Vector<? extends VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println(recVB.getOid() + " : " + recVB.getVariable());
			}
		}
	}
}
