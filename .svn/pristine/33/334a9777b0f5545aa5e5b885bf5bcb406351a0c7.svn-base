package com.fable.hamal.snmpserver.trap;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * SNMP������
 */
public class SnmpManager {
	private static Snmp SNMP = null;
	public TransportMapping<?> TRANSPORT = null;
	private String UDPADDRESS = "localhost/162";

	public void initComm() throws IOException {
		// ���ý���trap��IP�Ͷ˿�
		TRANSPORT = new DefaultUdpTransportMapping(new UdpAddress(UDPADDRESS));
		SNMP = new Snmp(TRANSPORT);
		CommandResponder trapRec = new CommandResponder() {
			public synchronized void processPdu(CommandResponderEvent e) {
				// ����trap
				PDU command = e.getPDU();
				if (command != null) {
					// System.out.println(command.toString());
					Vector<? extends VariableBinding> recVBs = command.getVariableBindings();
					for (int i = 0; i < recVBs.size(); i++) {
						VariableBinding recVB = recVBs.elementAt(i);
					/*	System.out.println("There comes a trap - " + recVB.getOid() + " - "
								+ SnmpUtil.getChinese(recVB.getVariable().toString()));*/
						// ��trap���ĵ���ݿ�
						try {
							// ��trap���ĵ�pdu
							SnmpUtil.initComm(SnmpUtil.PDU_ADDRESS);
							SnmpUtil.setPDU(recVB.getOid().toString(),
									SnmpUtil.getChinese(recVB.getVariable().toString()));
							// ��DB?????
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		};
		SNMP.addCommandResponder(trapRec);
		TRANSPORT.listen();
	}

	public synchronized void listen() {
		System.out.println("Snmp server started waiting for traps...");
		try {
			this.wait();// Wait for traps to come in
		} catch (InterruptedException ex) {
			System.out.println("Interrupted while waiting for traps: " + ex);
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		try {
			SnmpManager snmpManager = new SnmpManager();
			snmpManager.initComm();
			snmpManager.listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}