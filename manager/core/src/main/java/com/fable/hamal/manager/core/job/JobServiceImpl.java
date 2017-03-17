/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.fable.hamal.manager.common.cache.JobConfigCache;
import com.fable.hamal.manager.common.utils.constants.ConfigConstants;
import com.fable.hamal.manager.core.config.service.DataSourceService;
import com.fable.hamal.manager.core.config.service.EtlStrategyService;
import com.fable.hamal.manager.core.config.service.PipelineService;
import com.fable.hamal.manager.core.config.service.ScheduleConfigService;
import com.fable.hamal.manager.core.config.service.TaskService;
import com.fable.hamal.manager.core.config.service.TimestampService;
import com.fable.hamal.manager.core.config.service.TransEntityService;
import com.fable.hamal.shuttle.common.model.config.DataSource;
import com.fable.hamal.shuttle.common.model.config.EtlStrategy;
import com.fable.hamal.shuttle.common.model.config.Pipeline;
import com.fable.hamal.shuttle.common.model.config.ScheduleConfig;
import com.fable.hamal.shuttle.common.model.config.Task;
import com.fable.hamal.shuttle.common.model.config.Timestamp;
import com.fable.hamal.shuttle.common.model.config.TransEntity;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.model.config.metadata.File;
import com.fable.hamal.shuttle.common.model.config.metadata.Ftp;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.config.metadata.TimestampTable;
import com.fable.hamal.shuttle.common.model.envelope.data.Job;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.et.EtlMacrocosm;
import com.fable.hamal.shuttle.common.utils.EncryptDES;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;
import com.fable.hamal.shuttle.communication.client.Communication;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.task.TaskRunEvent;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 
 * @author xieruidong 2013年11月21日 下午4:43:10
 */
public class JobServiceImpl implements JobService {

	private final static Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
	private volatile boolean start = false;
	private Communication communication;
	private DataSourceService dataSourceService;
	private EtlStrategyService etlStrategyService;
	private PipelineService pipelineService;
	private ScheduleConfigService scheduleConfigService;
	private TaskService taskService;
	private TimestampService timestampService;
	private TransEntityService transEntityService;
	
	private static final String IS_TABLE = "0";
	
	/**同步类型，(0:全量 ；3:增量触发器,4:增量时间戳，2：日志)*/
	private static final String IS_FULL  = "0";
    private static final String IS_INCREMENT = "3";
	private static final String IS_TIMESTAMP = "4";
	private static final String IS_LOG= "2";
	private static final String IS_SLAVETABLE = "1";
	private static final String COMMA = ",";
	/**增量表*/
	private static final String FACTORY_ADD_TABLE = "addTable";
	/**主从表*/
	private static final String FACTORY_FP_TABLE  = "fpTable";
	/**全量表*/
	private static final String FACTORY_FULL_TABLE = "sqlTable";
	/**时间戳*/
	private static final String FACTORY_TIMESTAMP_TABLE = "sqlTableTimestamp";
	
	public static void main(String[] args) throws IOException, Exception {
		System.out.println(EncryptDES.decrypt("J2iiqcVaQ6A="));
		System.out.println(EncryptDES.encrypt("xVlFBZNo9cU="));
	}
	
	public void start() {
		if (!start) {
			List<Task> tasks = taskService.getAllTasks();
			List<Pipeline> pipelines = pipelineService.getAllPipelines();
			List<TransEntity> transEntities = transEntityService.getAllTransEntities();
			List<DataSource> dataSources = dataSourceService.getAllDataSources();
			List<EtlStrategy> etlStrategies = etlStrategyService.getAllEtlStrategies();
			List<ScheduleConfig> scheduleConfigs = scheduleConfigService.getAllScheduleConfigs();
			
			preCacheTasks(pipelines, transEntities, dataSources, etlStrategies, scheduleConfigs);
			configureTasks(tasks);
			start = true;
		}
	}
	
    public boolean isStart() {
		return start;
	}
	
	/**通知node执行作业*/
	public void notice(String addr, Job job) {
		Event event = new TaskRunEvent();
		event.setData(job);
		communication.call(addr, event);
	}
	
	/**根据作业ID即：taskId,获取作业信息*/
	public Job getJob(Long jobId) {
		final Long taskId = jobId;
		Job job = JobConfigCache.get(jobId);
		if (null != job.getName()) {
			return job;
		}
		configSomeoneTask(taskId);
		return JobConfigCache.get(jobId);
	}
	
	/**初始化一个任务配置--更新一个任务也可以调用这个方法*/
	public void configSomeoneTask(Long taskId) {
		Task task = taskService.getTask(taskId);
		List<Pipeline> pipelines = pipelineService.getPipelinesByTaskId(taskId);
		List<TransEntity> transEntities = transEntityService.getTransEntitiesByTaskId(taskId);
		List<DataSource> dataSources = dataSourceService.getDataSourcesByTaskId(taskId);
		List<EtlStrategy> etlStrategy = etlStrategyService.getEtlStratgiesBytaskId(taskId);
		List<ScheduleConfig> schedule = scheduleConfigService.getScheduleConfigByTaskId(taskId);
		
		preCacheTasks(pipelines, transEntities, dataSources, etlStrategy, schedule);
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);
		configureTasks(tasks);
	}
	
	/**JobCache预处理*/
	private void preCacheTasks(List<Pipeline> pipelines, List<TransEntity> transEntities, 
			List<DataSource> dataSources, List<EtlStrategy> etlStrategies, List<ScheduleConfig> scheduleConfigs ) {
		
		final Map<Long, List<Pipeline>> pipelineMap = JobConfigCache.getPipelineMap();
		final Map<Long, TransEntity> transEntityMap = JobConfigCache.getTransEntityMap();
		final Map<Long, DataSource> dataSourceMap = JobConfigCache.getDataSourceMap();
		final Map<Long, List<EtlStrategy>> etlStrategyMap = JobConfigCache.getEtlStrategyMap();
		final Map<Long, Map<String, EtlStrategy>> etlStrategyTableMap = JobConfigCache.getEtlStrategyTable();
		final Map<Long, ScheduleConfig> scheduleConfigMap = JobConfigCache.getScheduleConfig();
		
		for (Pipeline pipeline : pipelines) {
			pipelineMap.get(pipeline.getTaskId()).add(pipeline);
		}
		
		for (TransEntity transEntity : transEntities) {
			transEntityMap.put(transEntity.getId(), transEntity);
		}
		
		for (DataSource dataSource : dataSources) {
			dataSourceMap.put(dataSource.getId(), dataSource);
		}
		
		for (EtlStrategy etlStrategy : etlStrategies) {
			etlStrategyMap.get(etlStrategy.getPipelieId()).add(etlStrategy);
		}
		
		for (EtlStrategy etlStrategy : etlStrategies) {
			etlStrategyTableMap.get(etlStrategy.getPipelieId()).put(etlStrategy.getFromTable(), etlStrategy);
		}
		
		for (ScheduleConfig scheduleConfig : scheduleConfigs) {
			scheduleConfigMap.put(scheduleConfig.getTaskId(), scheduleConfig);
		}
	}
	
	public void configureTasks(List<Task> tasks) {
		
		final Map<Long, List<Pipeline>> pipelineMap = JobConfigCache.getPipelineMap();
		final Map<Long, TransEntity> transEntityMap = JobConfigCache.getTransEntityMap();
		final Map<Long, DataSource> dataSourceMap = JobConfigCache.getDataSourceMap();
		final Map<Long, List<EtlStrategy>> etlStrategyMap = JobConfigCache.getEtlStrategyMap();
		final Map<Long, Map<String, EtlStrategy>> etlStrategyTableMap = JobConfigCache.getEtlStrategyTable();
		
		Map<Fragment, List<Fragment>> fragms = new MapMaker().makeComputingMap(new Function<Fragment, List<Fragment>>() {
			public List<Fragment> apply(Fragment input) {
				return new ArrayList<Fragment>();
			}
		});
		
		String[] sourceTables;
		String[] targetTables;
		for (Task task : tasks) {
			boolean stop = false;
			Long taskId = task.getId();
			//文件任务
			Job job = new Job();
			job.setId(taskId);
			job.setName(task.getName());
			List<Pump> pumps = new ArrayList<Pump>();
			job.setPumps(pumps);
			List<Pipeline> pipelines = pipelineMap.get(taskId);
			//一个任务中是同源的(交换实体相同)
			for (Pipeline pipeline : pipelines) {
				if (stop) {
					break;
				}
				Long pipelineId = pipeline.getId();
				Long sourceId = pipeline.getSourceId();
				Long targetId = pipeline.getTargetId();
				TransEntity source = transEntityMap.get(sourceId);
				TransEntity target = transEntityMap.get(targetId);
				DataSource datasourceSource = dataSourceMap.get(source.getDataSourceId());
				DataSource datasourceTarget = dataSourceMap.get(target.getDataSourceId());
				
				if (IS_TABLE.equals(source.getType()) && IS_TABLE.equals(target.getType()) ) {
				    //增量触发器同步主从表关系
                    if(IS_SLAVETABLE.equals(task.getAssociation()) &&
                                    IS_INCREMENT.equals(task.getSynchroType())) {
//                        //将表映射关系一一对应
//                        String sourceTableNames ="";
//                        String targetTableNames ="";
//                        
//                        sourceTables = source.getTableName().split(COMMA);
//                        targetTables = target.getTableName().split(COMMA);
//                        int index = 0;
//                        for (String tableName : sourceTables) {
//                            EtlStrategy etlStrategy = etlStrategyTableMap.get(pipelineId).get(tableName);
//                            String targetName = getTargetTable(tableName, etlStrategy, targetTables, index);
//                            if (null == targetName) {
//                                logger.error("Cant find the target table of the source(task:{},pipeline:{},table:{})", 
//                                        new String[] {taskId.toString(), pipelineId.toString(), tableName});
//                                if (ConfigConstants.CONFIG_ERROR_SKIPPED_TASK
//                                        .equals(HamalPropertyConfigurer.getHamalProperty("config.error.skip"))) {
//                                    JobConfigCache.remove(taskId);
//                                    stop = true;
//                                    break;
//                                } else {
//                                    continue;
//                                }
//                            }
//                            sourceTableNames += sourceTables+COMMA;
//                            targetTableNames += sourceTables+"->"+targetName+COMMA;
//                        }
//                        sourceTableNames = sourceTableNames.substring(0,sourceTableNames.length()-1);
//                        targetTableNames = targetTableNames.substring(0,targetTableNames.length()-1);
//                        Fragment sourceFrag = new Fragment(sourceTableNames, taskId, pipelineId);
//                        Fragment targetFrag = new Fragment(targetTableNames, taskId, pipelineId);
//                        sourceFrag.setDataSource(dataSourceMap.get(source.getDataSourceId()));
//                        sourceFrag.setEtlStrategy(etlStrategy);
//                        sourceFrag.setPipeline(pipeline);
//                        sourceFrag.setTask(task);
//                        sourceFrag.setTransEntity(source);
//                        
//                        targetFrag.setDataSource(dataSourceMap.get(target.getDataSourceId()));
//                        targetFrag.setEtlStrategy(etlStrategy);
//                        targetFrag.setPipeline(pipeline);
//                        targetFrag.setTask(task);
//                        targetFrag.setTransEntity(target);
//                        fragms.get(sourceFrag).add(targetFrag);
                        
                    } else {
                        sourceTables = source.getTableName().split(COMMA);
                        targetTables = target.getTableName().split(COMMA);
                        int index = 0;
                        for (String tableName : sourceTables) {
                            EtlStrategy etlStrategy = etlStrategyTableMap.get(pipelineId).get(tableName);
                            String targetName = getTargetTable(tableName, etlStrategy, targetTables, index);
                            if (null == targetName) {
                                logger.error("Cant find the target table of the source(task:{},pipeline:{},table:{})", 
                                        new String[] {taskId.toString(), pipelineId.toString(), tableName});
                                if (ConfigConstants.CONFIG_ERROR_SKIPPED_TASK
                                        .equals(HamalPropertyConfigurer.getHamalProperty("config.error.skip"))) {
                                    JobConfigCache.remove(taskId);
                                    stop = true;
                                    break;
                                } else {
                                    continue;
                                }
                            }
                            Fragment sourceFrag = new Fragment(tableName, taskId, pipelineId);
                            Fragment targetFrag = new Fragment(targetName, taskId, pipelineId);
                            sourceFrag.setDataSource(dataSourceMap.get(source.getDataSourceId()));
                            sourceFrag.setEtlStrategy(etlStrategy);
                            sourceFrag.setPipeline(pipeline);
                            sourceFrag.setTask(task);
                            sourceFrag.setTransEntity(source);
                            
                            targetFrag.setDataSource(dataSourceMap.get(target.getDataSourceId()));
                            targetFrag.setEtlStrategy(etlStrategy);
                            targetFrag.setPipeline(pipeline);
                            targetFrag.setTask(task);
                            targetFrag.setTransEntity(target);
                            fragms.get(sourceFrag).add(targetFrag);
                        }
                        
                    }
				} else {
					Ftp sourceFtp = new Ftp();
				    Ftp targetFtp = new Ftp();
				    setFtpInfo(sourceFtp, datasourceSource);
				    setFtpInfo(targetFtp, datasourceTarget);
				    File sourceFile = new File();
				    File targetFile = new File();
				    sourceFile.setPath(source.getLocation());
				    sourceFile.setFtp(sourceFtp);
				    targetFile.setPath(target.getLocation());
				    targetFile.setFtp(targetFtp);
				    Pump pump = new Pump();
                    Pair pair = new Pair();
                    pair.setTarget(targetFile);
                    pump.setId(createPumpId());
                    pump.setSource(sourceFile);
                    pump.getPairs().add(pair);
                    pump.setJobId(taskId);
                    pump.setPipelineId(pipelineId);
                    pumps.add(pump);
				}
			}
			JobConfigCache.put(job.getId(), job);
		}
		
		Iterator<Fragment> itor= fragms.keySet().iterator();
		while (itor.hasNext()) {
			Fragment s = itor.next();
			Task task = s.getTask();
			Job job = JobConfigCache.get(s.getTaskId());
			Pump pump = new Pump();
			pump.setId(createPumpId());
			pump.setJobId(job.getId());
			pump.setPipelineId(s.getPipelineId());
			if (IS_TIMESTAMP.equals(task.getSynchroType())) {
				Timestamp timestamp = new Timestamp();
		        timestamp.setTaskId(task.getId());
		        timestamp.setDataSourceId(s.getDataSourceId());
		        timestamp.setTableName(s.getName());
		        Timestamp ts = timestampService.getTimestamp(timestamp);
		        
		        TimestampTable sourceTimestamp = new TimestampTable();
		        Db sourceDb = new Db();
		        setDbInfo(sourceDb, s.getDataSource());
		        sourceTimestamp.setDb(sourceDb);
		        sourceTimestamp.setSourceTableName(s.getName());
		        List<Fragment> targetFragments = fragms.get(s);
				for (Fragment t : targetFragments) {
					Pair pair = new Pair();
					TimestampTable targetTimestamp = new TimestampTable();
					Db targetDb = new Db();
					setDbInfo(targetDb, t.getDataSource());
					targetTimestamp.setDb(targetDb);
					targetTimestamp.setTargetTableName(t.getName());
//					targetTimestamp.setFactoryPrefix(getFactory(null, null));
					pair.setTarget(targetTimestamp);
					/*
			         * 为目标端 设置源表名 taskid 和 datesourceid 
			         * 做到每load一批数据 修改时间戳表中 相应记录的 时间戳字段
			         */
			        targetTimestamp.setSourceTableName(s.getName());
			        targetTimestamp.setTaskId(task.getId());
			        targetTimestamp.setDataSourceId(s.getDataSourceId());
			        if ( ts != null) {
			            sourceTimestamp.setTimestampColumn(ts.getDataColumn());
			            sourceTimestamp.setTimes(ts.getSwitchTime());
			            targetTimestamp.setTimestampColumn(ts.getDataColumn());
			            targetTimestamp.setTimes(ts.getSwitchTime());
			           
			        }
					//etl机制
					String etl = t.getEtlStrategy().getContentFilter();
					EtlMacrocosm em = null;
					if (null != etl) {
						em = JSON.parseObject(etl, EtlMacrocosm.class);
					}
					pair.setEtl(em);
					pump.getPairs().add(pair);
				}
			} else {
				Table sourceTable = new Table();
				Db sourceDb = new Db();
				setDbInfo(sourceDb, s.getDataSource());
				sourceTable.setDb(sourceDb);
				sourceTable.setTableName(s.getName());
				sourceTable.setFactoryPrefix(getFactory(s.getTask().getSynchroType(), s.getTask().getAssociation()));
				
				pump.setSource(sourceTable);
				List<Fragment> ts = fragms.get(s);
				for (Fragment t : ts) {
					Pair pair = new Pair();
					Table targetTable = new Table();
					Db targetDb = new Db();
					setDbInfo(targetDb, t.getDataSource());
					targetTable.setDb(targetDb);
					targetTable.setTableName(t.getName());
					targetTable.setFactoryPrefix(getFactory(s.getTask().getSynchroType(), s.getTask().getAssociation()));
					pair.setTarget(targetTable);
					
					//etl机制
					EtlStrategy etlStrategy = t.getEtlStrategy();
					if (null != etlStrategy) {
						String etl = etlStrategy.getContentFilter();
						EtlMacrocosm em = null;
						if (null != etl) {
							em = JSON.parseObject(etl, EtlMacrocosm.class);
						}
						pair.setEtl(em);
					}
					pump.getPairs().add(pair);
				}
			}
			job.getPumps().add(pump);
		}
	}
	
	/**把Task转换成Job并且缓存起来*/
    public void cacheTasks(List<Task> tasks) {
		final Map<Long, List<Pipeline>> pipelineMap = JobConfigCache.getPipelineMap();
		final Map<Long, TransEntity> transEntityMap = JobConfigCache.getTransEntityMap();
		final Map<Long, DataSource> dataSourceMap = JobConfigCache.getDataSourceMap();
		String[] sourceTables;
		String[] targetTables;
		String sourceLocation;
		String targetLocation;
		for (Task task : tasks) {
			Job job = new Job();
			job.setId(task.getId());
			job.setName(task.getName());
			UUID uuid = UUID.randomUUID();
			Long pumpId = new Long(uuid.toString().hashCode());
			List<Pump> pumps = new ArrayList<Pump>();
			job.setPumps(pumps);
			//暂时未作合并数据源处理,由于ETL策略未定所有默认source和target是相同类型比如都是table到table，并且个数相同，顺序对应
			for (Pipeline pipeline : pipelineMap.get(task.getId())) {
				TransEntity source = transEntityMap.get(pipeline.getSourceId());
				TransEntity target = transEntityMap.get(pipeline.getTargetId());
				DataSource datasourceSource = dataSourceMap.get(source.getDataSourceId());
				DataSource datasourceTarget = dataSourceMap.get(target.getDataSourceId());
				
				if (IS_TABLE.equals(source.getType()) && IS_TABLE.equals(target.getType()) ) {
					Db sourceDb = new Db();
					Db targetDb = new Db();
					setDbInfo(sourceDb, datasourceSource);
					setDbInfo(targetDb, datasourceTarget);
					sourceTables = source.getTableName().split(COMMA);
					targetTables = target.getTableName().split(COMMA);
					//如果主从表个数不一样按少的来算
					if(sourceTables.length > targetTables.length) {
					    String [] arg = new String [targetTables.length];
					    System.arraycopy(sourceTables,0,arg,0,targetTables.length);
					    sourceTables = arg;
					} else if(sourceTables.length < targetTables.length) {
					    String [] arg = new String [sourceTables.length];
                        System.arraycopy(targetTables,0,arg,0,sourceTables.length);
                        targetTables = arg;
					}
					if(IS_INCREMENT.equals(task.getSynchroType())) {
					    //主从表关系
					    if(IS_SLAVETABLE.equals(task.getAssociation())) {
					        Table sourceTable = new Table();
					        Table targetTable = new Table();
					        sourceTable.setFactoryPrefix("fpTable");
					        targetTable.setFactoryPrefix("fpTable");
					        sourceTable.setDb(sourceDb);
	                        sourceTable.setTableName(source.getTableName());
	                        targetTable.setDb(targetDb);
	                        targetTable.setTableName(target.getTableName());
	                        Pump pump = new Pump();
	                        Pair pair = new Pair();
	                        pair.setTarget(targetTable);
	                        pump.setId(pumpId);
	                        pump.setSource(sourceTable);
	                        pump.getPairs().add(pair);
	                        pumps.add(pump);
					    } else {//普通表
					        for (int i = 0; i < sourceTables.length; i++) {
					            Table sourceTable = new Table();
					            Table targetTable = new Table();
					            sourceTable.setFactoryPrefix("addTable");
					            targetTable.setFactoryPrefix("addTable");
	                            sourceTable.setDb(sourceDb);
	                            sourceTable.setTableName(sourceTables[i]);
	                            targetTable.setDb(targetDb);
	                            targetTable.setTableName(targetTables[i]);
	                            Pump pump = new Pump();
	                            Pair pair = new Pair();
	                            pair.setTarget(targetTable);
	                            pump.setId(pumpId);
	                            pump.setSource(sourceTable);
	                            pump.getPairs().add(pair);
	                            pumps.add(pump);
//	                            pumpId++;
	                        }
						}
					//时间戳
					}
					else if(IS_TIMESTAMP.equals(task.getSynchroType())) {
					    for (int i = 0; i < sourceTables.length; i++) {
					        Timestamp timestamp = new Timestamp();
					        timestamp.setTaskId(task.getId());
					        timestamp.setDataSourceId(datasourceSource.getId());
					        timestamp.setTableName(sourceTables[i]);
					        Timestamp ts = timestampService.getTimestamp(timestamp);
					        TimestampTable sourceTimestamp = new TimestampTable();
					        TimestampTable targetTimestamp = new TimestampTable();
					        sourceTimestamp.setDb(sourceDb);
					        sourceTimestamp.setSourceTableName(source.getTableName());
					        targetTimestamp.setDb(targetDb);
					        targetTimestamp.setTargetTableName(targetTables[i]);
					        /*
					         * 为目标端 设置源表名 taskid 和 datesourceid 
					         * 做到每load一批数据 修改时间戳表中 相应记录的 时间戳字段
					         */
					        targetTimestamp.setSourceTableName(source.getTableName());
					        targetTimestamp.setTaskId(task.getId());
					        targetTimestamp.setDataSourceId(datasourceSource.getId());
					        if(ts!=null) {
					            sourceTimestamp.setTimestampColumn(ts.getDataColumn());
					            sourceTimestamp.setTimes(ts.getSwitchTime());
					            targetTimestamp.setTimestampColumn(ts.getDataColumn());
					            targetTimestamp.setTimes(ts.getSwitchTime());
					           
					        }
					        Pump pump = new Pump();
					        Pair pair = new Pair();
					        pair.setTarget(targetTimestamp);
					        pump.setId(pumpId);
					        pump.setSource(sourceTimestamp);
					        pump.getPairs().add(pair);
					        pumps.add(pump);
					        pumpId++;
					    }    
					//全量
					} else {   
    				    for (int i = 0; i < sourceTables.length; i++) {
    				        Table sourceTable = new Table();
    				        Table targetTable = new Table();
    				        sourceTable.setDb(sourceDb);
    				        sourceTable.setTableName(sourceTables[i]);
    				        targetTable.setDb(targetDb);
    				        targetTable.setTableName(targetTables[i]);
    				        Pump pump = new Pump();
    				        Pair pair = new Pair();
    				        pair.setTarget(targetTable);
    				        pump.setId(pumpId);
    				        pump.setSource(sourceTable);
    				        pump.getPairs().add(pair);
    				        pumps.add(pump);
    				        pumpId++;
				        }
					}
				//文件	
				} else {
				    Ftp sourceFtp = new Ftp();
				    Ftp targetFtp = new Ftp();
				    setFtpInfo(sourceFtp, datasourceSource);
				    setFtpInfo(targetFtp, datasourceTarget);
				    sourceLocation = source.getLocation();
				    targetLocation = target.getLocation();
				    
				    File sourceFile = new File();
				    File targetFile = new File();
				    sourceFile.setPath(sourceLocation);
				    sourceFile.setFtp(sourceFtp);
				    targetFile.setPath(targetLocation);
				    targetFile.setFtp(targetFtp);
				    Pump pump = new Pump();
                    Pair pair = new Pair();
                    pair.setTarget(targetFile);
                    pump.setId(pumpId);
                    pump.setSource(sourceFile);
                    pump.getPairs().add(pair);
                    pumps.add(pump);
                    pumpId++;
				}
			}
			JobConfigCache.put(job.getId(), job);
		}
	}
	
	public void updateJob(Long jobId) {
		
	}
	
	public void deleteJob(Long jobId) {
		
	}
	
	public void addJob() {
		
	}
	
	
	
	private void setDbInfo(Db db, DataSource ds) {
		db.setId(ds.getId());
		db.setDbName(ds.getDbName());
		db.setServerIp(ds.getServerIp());
		db.setUsername(ds.getUsername());
		try {
			db.setPassword(EncryptDES.decrypt(ds.getPassword()));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("database:{},{},{} decrypt failed", 
						new String[] {ds.getDbName(), ds.getConnectUrl(), ds.getUsername()});
			}
			e.printStackTrace();
		}
		db.setDbType(ds.getDbType());
		db.setConnectUrl(ds.getConnectUrl());
	}
	
	private void setFtpInfo(Ftp ftp,DataSource ds){
	    ftp.setId(ds.getId());
	    ftp.setUsername(ds.getUsername());
	    try {
	        ftp.setPassword(EncryptDES.decrypt(ds.getPassword()));
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("database:{},{},{} decrypt failed", 
                        new String[] {ds.getDbName(), ds.getConnectUrl(), ds.getUsername()});
            }
            e.printStackTrace();
        }
	    ftp.setServerIp(ds.getServerIp());
	    ftp.setPort(ds.getPort());
	    ftp.setRootPath(ds.getRootPath());
	}

	//---------------------------------------------setter&&getter------------------------------------------------------
	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public DataSourceService getDataSourceService() {
		return dataSourceService;
	}

	public void setDataSourceService(DataSourceService dataSourceService) {
		this.dataSourceService = dataSourceService;
	}

	public EtlStrategyService getEtlStrategyService() {
		return etlStrategyService;
	}

	public void setEtlStrategyService(EtlStrategyService etlStrategyService) {
		this.etlStrategyService = etlStrategyService;
	}

	public PipelineService getPipelineService() {
		return pipelineService;
	}

	public void setPipelineService(PipelineService pipelineService) {
		this.pipelineService = pipelineService;
	}

	public ScheduleConfigService getScheduleConfigService() {
		return scheduleConfigService;
	}

	public void setScheduleConfigService(ScheduleConfigService scheduleConfigService) {
		this.scheduleConfigService = scheduleConfigService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public TransEntityService getTransEntityService() {
		return transEntityService;
	}

	public void setTransEntityService(TransEntityService transEntityService) {
		this.transEntityService = transEntityService;
	}
	

    public TimestampService getTimestampService() {
        return timestampService;
    }
    
    public void setTimestampService(TimestampService timestampService) {
        this.timestampService = timestampService;
    }
	
    public void setStart(boolean start) {
		this.start = start;
	}
    
    /**节点配合Map使用构造出一张源表交换到多张源表*/
    class Fragment {
    	
    	private String name;
    	
    	private Long taskId;
    	
    	private Long pipelineId;
    	
    	private Long transEntityId;
    	
    	private Long dataSourceId;
    	
    	private Task task;
    	
    	private Pipeline pipeline;
    	
    	private TransEntity transEntity;
    	
    	private DataSource dataSource;
    	
    	private EtlStrategy etlStrategy;
    	
    	public Fragment() {
    		
    	}
    	
    	public Fragment(String name, Long taskId, Long pipelineId) {
    		this.name = name;
    		this.taskId = taskId;
    		this.pipelineId = pipelineId;
    	}
    	
    	public Fragment(String name, Long taskId, Long pipelineId, Long transEntityId, Long dataSourceId) {
    		this.name = name;
    		this.taskId = taskId;
    		this.pipelineId =  pipelineId;
    		this.transEntityId = transEntityId;
    		this.dataSourceId = dataSourceId;
    	}
    	
		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof Fragment)) {
				return false;
			}
			Fragment frag = (Fragment)obj;
			boolean result = this.name.equals(frag.name) && this.taskId.equals(frag.taskId)
					&& this.dataSourceId.equals(frag.dataSourceId);
			return result;
		}
		
		@Override
		public int hashCode() {
			return this.name.hashCode() + taskId.intValue() + dataSourceId.intValue();
		}
    	
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Long getTaskId() {
			return taskId;
		}
		
		public void setTaskId(Long taskId) {
			this.taskId = taskId;
		}
		
		public Long getPipelineId() {
			return pipelineId;
		}
		
		public void setPipelineId(Long pipelineId) {
			this.pipelineId = pipelineId;
		}
		
		public Long getTransEntityId() {
			return transEntityId;
		}
		
		public void setTransEntityId(Long transEntityId) {
			this.transEntityId = transEntityId;
		}
		
		public Long getDataSourceId() {
			return dataSourceId;
		}
		
		public void setDataSourceId(Long dataSourceId) {
			this.dataSourceId = dataSourceId;
		}
		
		public Task getTask() {
			return task;
		}
		
		public void setTask(Task task) {
			this.task = task;
		}
		
		public Pipeline getPipeline() {
			return pipeline;
		}
		
		public void setPipeline(Pipeline pipeline) {
			this.pipeline = pipeline;
		}
		
		public TransEntity getTransEntity() {
			return transEntity;
		}
		
		public void setTransEntity(TransEntity transEntity) {
			this.transEntity = transEntity;
		}
		
		public DataSource getDataSource() {
			return dataSource;
		}
		
		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
			this.dataSourceId = dataSource.getId();
		}
		
		public EtlStrategy getEtlStrategy() {
			return etlStrategy;
		}
		
		public void setEtlStrategy(EtlStrategy etlStrategy) {
			this.etlStrategy = etlStrategy;
		}
    }
    /**获取目标表*/
	private String getTargetTable(String sourceTable, EtlStrategy etlStrategy, String[] targetTables, int index) {
		String result = null;
		/*如果有etl策略则使用etl配置表*/
		if (null != etlStrategy) {
			return etlStrategy.getToTable();
		}
		/*去目标表中寻找同名表*/
		for (String tab : targetTables) {
			if (sourceTable.equalsIgnoreCase(tab)) {
				return tab;
			}
		}
		/*位置匹配*/
		int len = targetTables.length;
		if (index < len) {
			result = targetTables[index];
		}
		
		return result;
	}
	
	/**生成pumpid*/
	private Long createPumpId() {
		UUID uuid = UUID.randomUUID();
		Long pumpId = new Long(uuid.toString().hashCode());
		return pumpId;
	}
	
	private String getFactory(String type, String subType) {
		if (IS_INCREMENT.equals(type)) {
			if (IS_SLAVETABLE.equals(subType)) {
				return FACTORY_FP_TABLE;
			}
			return FACTORY_ADD_TABLE;
		} else if (IS_TIMESTAMP.equals(type)) {
			return FACTORY_TIMESTAMP_TABLE;
		}
		return FACTORY_FULL_TABLE;
	}
	
	private void configureFile2FileTasks(List<Task> tasks) {
		
	}
	
	private void configureDb2DbTasks(List<Task> tasks) {
		
	}
	
	private void configureDb2File(List<Task> tasks) {
		
	}
	
	private void configureFile2Db(List<Task> tasks) {
		
	}
}
