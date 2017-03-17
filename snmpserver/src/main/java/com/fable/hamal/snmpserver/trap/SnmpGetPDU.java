package com.fable.hamal.snmpserver.trap;

import java.io.IOException;

public class SnmpGetPDU {
	public static void main(String[] args) {
		try {
			SnmpUtil.initComm(SnmpUtil.PDU_ADDRESS);
			SnmpUtil.getPDU();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
