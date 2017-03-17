package com.fable.hamal.snmpserver.trap;

import java.io.IOException;

public class SnmpSendTrap {
	public static void main(String[] args) {
		try {
			SnmpUtil.initComm(SnmpUtil.TRAP_ADDRESS);
			SnmpUtil.setTrap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
