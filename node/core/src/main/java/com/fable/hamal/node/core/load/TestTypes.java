/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author xieruidong 2014年5月12日 下午2:43:30
 */
public class TestTypes {
	
	public static void main(String[] args) throws SQLException {
	        Connection conn = null;
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.161:1521:orcl","fable", "fable");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        print(conn, "TEST_TYPE");
	        
	        Connection connSqlserver = null;
	        try {
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	            connSqlserver = DriverManager.getConnection("jdbc:sqlserver://192.168.1.89:1433;databaseName=fable","fable", "123456");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        print(connSqlserver, "sqlserverall");
	        print(connSqlserver, "student");
	}

	public static void print(Connection conn, String table) throws SQLException {
		DatabaseMetaData dmd = conn.getMetaData();
        ResultSet rs = dmd.getColumns(null, null, table, null);
        while (rs.next()) {
        	String columnName = rs.getString("COLUMN_NAME");
        	String typeName = rs.getString("TYPE_NAME");
			int columnType = rs.getInt("DATA_TYPE");
			
			System.out.print(columnName+"=================");
			System.out.print(typeName+"=================");
			System.out.println(columnType+"=================");
        }
	}
}
