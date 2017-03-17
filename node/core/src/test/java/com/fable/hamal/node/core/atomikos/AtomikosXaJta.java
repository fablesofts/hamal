/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.atomikos;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/**
 * SUCCESS
 * Atomikos分布式事务XA测试类--开源版本：TransactionsEssentials
 * 商业版本为：ExtremeTransactions(需要购买后才能测试)。
 * 2013-12-17：XA事务在连接执行时候绑定到线程，所以不再进行跨线程尝试。
 * @author xieruidong 2013年12月16日 下午2:15:12
 */
public class AtomikosXaJta {

	@SuppressWarnings({ "unchecked", "rawtypes"})
	public static void main(String[] args) throws SQLException {
		//spring TransactionTemplate can not deal transaction cross-threads
		TransactionTemplate transactionTemplate = new TransactionTemplate();
		JtaTransactionManager jtaManager = new JtaTransactionManager();
		
		UserTransactionManager utm = new UserTransactionManager();
		utm.setForceShutdown(false);
		try {
			utm.init();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		UserTransactionImp uti = new UserTransactionImp();
		
		jtaManager.setTransactionManager(utm);
		jtaManager.setUserTransaction(uti);
		transactionTemplate.setTransactionManager(jtaManager);
		
		final JdbcTemplate jdbcTemplate1 = new JdbcTemplate();
		final JdbcTemplate jdbcTemplate2 = new JdbcTemplate();
		
		jdbcTemplate1.setDataSource(getDataSourceTest());
		jdbcTemplate2.setDataSource(getDataSourceXATest());
		
//		单线程XA事务处理完成--done
		transactionTemplate.execute(new TransactionCallback() {

			@Override
			public Object doInTransaction(TransactionStatus status) {
				jdbcTemplate2.execute("insert into employee(id,name)values(1,'Red1')");
				jdbcTemplate1.execute("insert into employee(id,name)values(2,'Red2')");
				return null;
			}
		});
		
//		ExecutorService exec = Executors.newCachedThreadPool();
//		ArrayList<Future<String>> results = new ArrayList<Future<String>>();
//		
//		Runner runner1 = new Runner(jdbcTemplate2, "insert into employee(id,name)values(4,'Red4')");
//		Runner runner2 = new Runner(jdbcTemplate1, "insert into employee(id,name)values(3,'Red3')");
//		
//		Caller caller1 = new Caller(jdbcTemplate2, "insert into employee(id,name)values(6,'Red6')");
//		Caller caller2 = new Caller(jdbcTemplate1, "insert into employee(id,name)values(3,'Red3')");
		
		//尝试跨线程事务，由于Spring TransactionTemplate不支持跨线程事务，所以进行 PlatformTransactionManager--failed
		//1.try first
//		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//		def.setName("SomeTxName");
//		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//		TransactionStatus status = jtaManager.getTransaction(def);
//		try {
//			runner1.start();
//			runner2.start();
//		} catch (Exception ex) {
//			jtaManager.rollback(status);
//		}
//		jtaManager.commit(status);
		
		//2.try second
//		DefaultTransactionDefinition def2 = new DefaultTransactionDefinition();
//		def2.setName("qqTxName");
//		def2.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//		TransactionStatus status2 = jtaManager.getTransaction(def2);
//		try {
//			results.add(exec.submit(caller1));
//			results.add(exec.submit(caller2));
//			int count = 0;
//			while (true) {
//				 for (Future<String> fs : results) {
//			            if (fs.isDone()) {
//			                System.out.println(fs.get());
//			                count++;
//			            } else {
//			                System.out.println("Future result is not yet complete");
//			                Thread.currentThread().sleep(100);
//			            }  
//			    }
//				 if (2 == count) {
//					 break;
//				 }
//			}
//		} catch (Exception ex) {
//			jtaManager.rollback(status2);
//			System.out.println("======================================================!");
//		}
//		jtaManager.commit(status2);
		
		//尝试跨线程事务，由于Spring TransactionTemplate不支持跨线程事务，所以进行原始XA编程
	}
	
	/**构造一个数据源*/
	public static DataSource getDataSourceTest() {
		Properties pro = new Properties();
		pro.setProperty("url", "jdbc:mysql://192.168.0.156:3306/test");
		pro.setProperty("user", "fable");
		pro.setProperty("password", "fable");
		AtomikosDataSourceBean adsb = new AtomikosDataSourceBean();
		adsb.setUniqueResourceName("ds_1");
		//1.Oracle：oracle.jdbc.xa.client.OracleXADataSource
		//2.MSSQL
		//3.DM6
		//4.DM7
		//5.Mysql
		adsb.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		adsb.setXaProperties(pro);
		adsb.setMinPoolSize(10);
		adsb.setMaxPoolSize(100);
		adsb.setBorrowConnectionTimeout(30);
		adsb.setTestQuery("select 1");
		adsb.setMaintenanceInterval(60);
		return adsb;
	}
	
	/**构造第二个数据源*/
	public static DataSource getDataSourceXATest() {
		Properties pro = new Properties();
		pro.setProperty("url", "jdbc:mysql://192.168.0.156:3306/xatest");
		pro.setProperty("user", "root");
		pro.setProperty("password", "kdm001");
		AtomikosDataSourceBean adsb = new AtomikosDataSourceBean();
		adsb.setUniqueResourceName("ds_2");
		adsb.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		adsb.setXaProperties(pro);
		adsb.setMinPoolSize(10);
		adsb.setMaxPoolSize(100);
		adsb.setBorrowConnectionTimeout(30);
		adsb.setTestQuery("select 1");
		adsb.setMaintenanceInterval(60);
		return adsb;
	}
	
	static class Runner extends Thread {
		
		private JdbcTemplate jdbcTemplate;
		private String sql;

		public Runner() {
			
		}

		public Runner(JdbcTemplate jdbcTemplate, String sql) {
			this.jdbcTemplate = jdbcTemplate;
			this.sql = sql;
		}
		
		public void run() {
			jdbcTemplate.execute(sql);
		}
		
		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}
	}
	
	@SuppressWarnings("rawtypes")
	static class Caller implements Callable {
		
		private JdbcTemplate jdbcTemplate;
		private String sql;

		public Caller() {
			
		}

		public Caller(JdbcTemplate jdbcTemplate, String sql) {
			this.jdbcTemplate = jdbcTemplate;
			this.sql = sql;
		}
		
		public void run() {
			jdbcTemplate.execute(sql);
		}
		
		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		@Override
		public String call() throws Exception {
			jdbcTemplate.execute(sql);
//			throw new Exception("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%$$$$$$$$$$$$$");
			return null;
		}
	}
}
