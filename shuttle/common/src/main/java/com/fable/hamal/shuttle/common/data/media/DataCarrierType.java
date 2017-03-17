/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.data.media;

import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 
 * @author xieruidong 2013年11月12日 下午2:24:13
 */
public enum DataCarrierType {

	MYSQL(DataCarrierBelongType.SQLDB, DbType.MYSQL, DbType.MYSQL_FULL),
	ORACLE(DataCarrierBelongType.SQLDB, DbType.ORACLE, DbType.ORACLE_FULL),
	MSSQL(DataCarrierBelongType.SQLDB, DbType.MSSQL, DbType.MSSQL_FULL),
	DAMENGSQL6(DataCarrierBelongType.SQLDB, DbType.DAMENG6, DbType.DAMENG6_FULL),
	DAMENGSQL7(DataCarrierBelongType.SQLDB, DbType.DAMENG7, DbType.DAMENG7_FULL),
	DB2(DataCarrierBelongType.SQLDB, DbType.DB2, DbType.DB2_FULL),
	POSTGRESQL(DataCarrierBelongType.SQLDB, DbType.POSTGRESQL, DbType.POSTGRESQL_FULL),
	HBASE(DataCarrierBelongType.NOSQLDB),
	MONGODB(DataCarrierBelongType.NOSQLDB),
	FILE(DataCarrierBelongType.FILE);
	
	private DataCarrierBelongType belongType;
	private String shortName;
	private String fullName;
	
	private DataCarrierType(DataCarrierBelongType type) {
		this.belongType = type;
	}
	
	private DataCarrierType(DataCarrierBelongType type, String shortName, String fullName) {
		this.belongType = type;
		this.shortName = shortName;
		this.fullName = fullName;
	}
	
	public static DataCarrierType getOf(String type) {
		DataCarrierType[] values = DataCarrierType.values();
		DataCarrierType result = null;
		for (DataCarrierType value : values) {
			if (value.getShortName().equals(type)) {
				result = value;
				break;
			}
		}
		return result;
	}
	
	public DataCarrierBelongType getBelongType() {
		return belongType;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
