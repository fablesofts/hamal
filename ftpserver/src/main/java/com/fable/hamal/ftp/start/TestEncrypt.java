/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.start;

import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;

/**
 * 
 * @author xieruidong 2014年2月18日 下午6:38:21
 */
public class TestEncrypt {

	public static void main(String[] args) {
		SaltedPasswordEncryptor spe = new SaltedPasswordEncryptor();
		Md5PasswordEncryptor mpe = new Md5PasswordEncryptor();
//		System.out.println(spe.encrypt("admin"));
		
		System.out.println(spe.encrypt("123"));
	}
}
