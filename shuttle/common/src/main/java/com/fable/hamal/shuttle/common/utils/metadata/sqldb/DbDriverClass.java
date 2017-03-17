/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.utils.metadata.sqldb;

import org.springframework.util.Assert;

import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 
 * @author xieruidong 2013年11月27日 下午4:38:19
 */
public class DbDriverClass {

	public static final String MYSQL_DEFAULT 			= "com.mysql.jdbc.Driver";
	public static final String MSSQL_DEFAULT 			= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String ORACLE_DEFAULT			= "oracle.jdbc.driver.OracleDriver";
	public static final String DAMENG6_DEFAULT 			= "dm6.jdbc.driver.DmDriver";
	public static final String DAMENG7_DEFAULT 			= "dm.jdbc.driver.DmDriver";
	public static final String POSTGRESQL_DEFAULT 		= "org.postgresql.Driver";
	public static final String DB2_DEFAULT				= "com.ibm.db2.jdbc.net.DB2Driver";
	
	public static String getDriver(String type) {
		Assert.notNull(type);
		String result = null;
		if (DbType.MYSQL.equals(type) || DbType.MYSQL_FULL.equals(type)) {
			result = MYSQL_DEFAULT;
		} else if (DbType.MSSQL.equals(type) || DbType.MSSQL_FULL.equals(type)) {
			result = MSSQL_DEFAULT;
		} else if (DbType.ORACLE.equals(type) || DbType.ORACLE_FULL.equals(type)) {
			result = ORACLE_DEFAULT;
		} else if (DbType.DAMENG6.equals(type) || DbType.DAMENG6_FULL.equals(type)) {
			result = DAMENG6_DEFAULT;
		} else if (DbType.DAMENG7.equals(type) || DbType.DAMENG7_FULL.equals(type)) {
			result = DAMENG7_DEFAULT;
		}else if (DbType.POSTGRESQL.equals(type) || DbType.POSTGRESQL_FULL.equals(type)) {
			result = POSTGRESQL_DEFAULT;
		} else if (DbType.DB2.equals(type) || DbType.DB2_FULL.equals(type)) {
			result = DB2_DEFAULT;
		}
		return result;
	}
}
