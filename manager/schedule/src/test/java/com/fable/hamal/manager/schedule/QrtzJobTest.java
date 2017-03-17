//package com.fable.hamal.manager.schedule;
//
//import java.text.ParseException;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.quartz.SchedulerException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.fable.hamal.manager.schedule.service.JobService;
//import com.fable.hamal.shuttle.common.model.config.metadata.Column;
//import com.fable.hamal.shuttle.common.model.config.metadata.Db;
//import com.fable.hamal.shuttle.common.model.config.metadata.Table;
//import com.fable.hamal.shuttle.common.model.envelope.Job;
//import com.fable.hamal.shuttle.common.model.envelope.Pump;
//import com.fable.hamal.shuttle.common.model.envelope.SQLTable2SQLTablePump;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath*:applicationContext*.xml" })
//public class QrtzJobTest {
//	@Autowired
//	JobService jobService;
//
//	Logger logger = LoggerFactory.getLogger(QrtzJobTest.class);
//
//	@Test
//	public void testAdd() throws SchedulerException, ParseException {
//		logger.debug("添加调度测试开始......");
//		Job job = getTestJob();
//		jobService.addJob(job);
//		
//		job.setCrontabExpression("0/5 * * ? * * *");
//		jobService.updateJob(job);
//		
//		logger.debug("添加调度测试完成?......");
//	}
//
//	@Test
//	public void testUpdate() throws SchedulerException, ParseException {
//		logger.debug("修改调度测试开始......");
//		Job job = getTestJob();
//		job.setCrontabExpression("0/5 * * ? * * *");
//		jobService.updateJob(job);
//		logger.debug("修改调度测试完成?......");
//
//	}
//
//	@Test
//	public void testDelete() throws SchedulerException {
//		logger.debug("删除调度测试开始......");
//		jobService.deleteJob(getTestJob());
//		logger.debug("删除调度测试完成?......");
//
//	}
//
//	@Test
//	public void testPause() throws SchedulerException {
//		logger.debug("暂停调度测试开始......");
//		jobService.pauseJob(getTestJob());
//		logger.debug("暂停调度测试完成?......");
//
//	}
//
//	@Test
//	public void testResume() throws SchedulerException {
//		logger.debug("重启调度测试开始......");
//		jobService.resumeJob(getTestJob());
//		logger.debug("重启调度测试完成?......");
//
//	}
//
//	private Job getTestJob() {
//		// 直接构建一个测试Job
//		Job job = new Job();
//		job.setId(1);
//		job.setName("JOBTEST");
//		job.setCrontabExpression("0/10 * * ? * * *");
//		SQLTable2SQLTablePump dbPump = new SQLTable2SQLTablePump();
//
//		Db db = new Db();
//		db.setDbType("oracle");
//
//		Table origin = new Table();
//		Table target = new Table();
//
//		Column c1 = new Column("ID");
//		Column c2 = new Column("CREATE_TIME");
//		Column c3 = new Column("CREATE_USER");
//		Column c4 = new Column("UPDATE_TIME");
//		Column c5 = new Column("UPDATE_USER");
//		Column c6 = new Column("CONNECT_URL");
//		Column c7 = new Column("DB_NAME");
//		Column c8 = new Column("DB_TYPE");
//		Column c9 = new Column("DEL_FLAG");
//		Column c10 = new Column("DESCRIPTION");
//		Column c11 = new Column("NAME");
//		Column c12 = new Column("PASSWORD");
//		Column c13 = new Column("PORT");
//		Column c14 = new Column("ROOT_PATH");
//		Column c15 = new Column("SERVER_IP");
//		Column c16 = new Column("SOURCE_TYPE");
//		Column c17 = new Column("USERNAME");
//		List<Column> oric = origin.getColumns();
//		oric.add(c1);
//		oric.add(c2);
//		oric.add(c3);
//		oric.add(c4);
//		oric.add(c5);
//		oric.add(c6);
//		oric.add(c7);
//		oric.add(c8);
//		oric.add(c9);
//		oric.add(c10);
//		oric.add(c11);
//		oric.add(c12);
//		oric.add(c13);
//		oric.add(c14);
//		oric.add(c15);
//		oric.add(c16);
//		oric.add(c17);
//
//		origin.setTableName("DSP_DATA_SOURCE");
//		origin.setDb(db);
//		origin.setColumns(oric);
//
//		target.setTableName("DSP_DATA_SOURCE_TEMP");
//		target.setDb(db);
//		target.setColumns(oric);
//
//		dbPump.setId(1L);
//		dbPump.setOrigin(origin);
//		dbPump.setTarget(target);
//		dbPump.setEtl(null);
//		List<Pump> pumps = new LinkedList<Pump>();
//		pumps.add(dbPump);
//
//		job.setPumps(pumps);
//
//		return job;
//	}
//}
