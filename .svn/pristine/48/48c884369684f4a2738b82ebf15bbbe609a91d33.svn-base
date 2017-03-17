/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.atomikos;

import javax.sql.DataSource;

import org.apache.commons.dbcp.managed.BasicManagedDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.arjuna.ats.jta.TransactionManager;
import com.arjuna.ats.jta.UserTransaction;

/**
 * SUCCESS
 * XA事务在连接执行时候绑定到线程，所以不再进行跨线程尝试。
 * @author xieruidong 2013年12月16日 下午2:15:38
 */
public class JbossasXaJta {

	public static void main(String[] args) {
		TransactionTemplate transactionTemplate = new TransactionTemplate();
		JtaTransactionManager jtaManager = new JtaTransactionManager();
		
		javax.transaction.TransactionManager tm = TransactionManager.transactionManager();
		jtaManager.setTransactionManager(tm);
		javax.transaction.UserTransaction ut = UserTransaction.userTransaction();
		jtaManager.setUserTransaction(ut);
		transactionTemplate.setTransactionManager(jtaManager);

		JdbcTemplate jdbcTemplate1 = new JdbcTemplate();
		JdbcTemplate jdbcTemplate2 = new JdbcTemplate();
		jdbcTemplate1.setDataSource(getDataSource1(tm));
		jdbcTemplate2.setDataSource(getDataSource2(tm));
		
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("SomeTxName");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = jtaManager.getTransaction(def);

		try {
			jdbcTemplate2.execute("insert into employee(id,name)values(888,'Red888')");
			jdbcTemplate1.execute("insert into employee(id,name)values(4,'Red4')");
			jtaManager.commit(status);
		} catch(DataAccessException dae) {
			dae.printStackTrace();
			jtaManager.rollback(status);
		}
		
	}
	
	public static DataSource getDataSource1(javax.transaction.TransactionManager tm) {
		BasicManagedDataSource bmds = new BasicManagedDataSource();
		bmds.setUrl("jdbc:mysql://192.168.0.156:3306/test");
		bmds.setUsername("fable");
		bmds.setPassword("fable");
		bmds.setDriverClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		bmds.setInitialSize(10);
		bmds.setMaxActive(60);
		bmds.setTransactionManager(tm);
		return bmds;
	}
	
	public static DataSource getDataSource2(javax.transaction.TransactionManager tm) {
		BasicManagedDataSource bmds = new BasicManagedDataSource();
		bmds.setUrl("jdbc:mysql://192.168.0.156:3306/xatest");
		bmds.setUsername("root");
		bmds.setPassword("kdm001");
		bmds.setDriverClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		bmds.setInitialSize(10);
		bmds.setMaxActive(60);
		bmds.setTransactionManager(tm);
		return bmds;
	}
}
