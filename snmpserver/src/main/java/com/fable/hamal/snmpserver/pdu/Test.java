package com.fable.hamal.snmpserver.pdu;

import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		try {
			SnmpUtil.initComm(SnmpUtil.PDUADDR);
			SnmpUtil.setPDU();
			SnmpUtil.getPDU();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
