/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.schedule.store;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author xieruidong 2013年12月3日 下午3:53:37
 */
public class HsqldbConfig {
	
	private final static Logger logger = LoggerFactory.getLogger(HsqldbConfig.class);
	private JdbcTemplate jdbcHsqldTemplate;
	private ResourceLoader resourceLoader;
	private final static int QUARTZ_TABLES_COUNT = 12;
	private final static String SQL_DDL_PALCE = "classpath:sql/tables_hsqldb.sql";
	private final static String SQL_DATA_PALCE = "classpath:sql/tables_hsqldb_initdata.sql";

	public void initialize() {
		if (isInitialize()) {
			return;
		}
		//由于hsqldb把数据持久化到磁盘有一个延迟时间(默认是0.5秒),初始化过程短暂，所以把延迟写先关闭后打开
		String sqlDdl = getInitSql(SQL_DDL_PALCE);
		String sqlData = getInitSql(SQL_DATA_PALCE);
		jdbcHsqldTemplate.execute("SET FILES WRITE DELAY FALSE");
		jdbcHsqldTemplate.execute(sqlDdl);
		jdbcHsqldTemplate.execute(sqlData);
		jdbcHsqldTemplate.execute("SET FILES WRITE DELAY TRUE");
	}
	
	private boolean isInitialize() {
		String sql = "SELECT count(1) FROM information_schema.system_tables WHERE table_name like 'QRTZ_%'";
		int count = jdbcHsqldTemplate.queryForInt(sql);
		return !(QUARTZ_TABLES_COUNT > count);
	}

	private String getInitSql(String place) {
		Resource resource = resourceLoader.getResource(place);
		InputStream in = null;
		BufferedInputStream bin = null;
		byte[] buf = new byte[64];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10240);
		try {
			in = resource.getInputStream();
			bin = new BufferedInputStream(in);
			int len;
			while (-1 != (len = bin.read(buf))) {
				baos.write(buf, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				bin.close();
			} catch (IOException e) {
				logger.error("hsqldb initialize failed~~");
				e.printStackTrace();
			}
		}
		return baos.toString();
	}
	
	public JdbcTemplate getJdbcHsqldTemplate() {
		return jdbcHsqldTemplate;
	}

	public void setJdbcHsqldTemplate(JdbcTemplate jdbcHsqldTemplate) {
		this.jdbcHsqldTemplate = jdbcHsqldTemplate;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	public static void main(String[] args) throws SQLException {
//		JobScheduleService jobService = HamalContextHelper.getBean("jobScheduleService");
//		Job job = new Job();
//		job.setCrontabExpression("0/5 * * ? * * *");
//		job.setId(new Random(100).nextLong());
//		job.setName("hsqld");
//		
//		try {
//			jobService.addJob(job);
//		} catch (SchedulerException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		HsqldbConfig hc = HamalContextHelper.getBean("hsqldbConfig");
//		hc.initialize();
	}
}
