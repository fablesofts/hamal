/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fable.hamal.shuttle.common.model.config.DataSource;
import com.fable.hamal.shuttle.common.model.config.EtlStrategy;
import com.fable.hamal.shuttle.common.model.config.Pipeline;
import com.fable.hamal.shuttle.common.model.config.ScheduleConfig;
import com.fable.hamal.shuttle.common.model.config.TransEntity;
import com.fable.hamal.shuttle.common.model.envelope.data.Job;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 配置信息缓存
 * @author xieruidong 2013年11月26日 上午10:58:32
 */
public class JobConfigCache {

	/**{1001(jobId):job(Job's instance)}*/
	private static Map<Long, Job> cache = new MapMaker().makeComputingMap(new Function<Long, Job>(){

		@Override
		public Job apply(Long input) {
			return new Job(input);
		}
		
	});
	/**{1001(jobId):[pipeline1(Pipeline's instance),pipeline2(Pipeline's instance),...]}*/
	private static Map<Long, List<Pipeline>> pipelineMap = new MapMaker().makeComputingMap(new Function<Long, List<Pipeline>>() {
		public List<Pipeline> apply(Long input) {
			return new ArrayList<Pipeline>();
		}
	});
	/**{1001(transEntityId):transEntity(TransEntity's instance)}*/
	private static Map<Long, TransEntity> transEntityMap = new ConcurrentHashMap<Long, TransEntity>();
	/**{1001(dataSourceId):dataSource(DataSource's instance)}*/
	private static Map<Long, DataSource> dataSourceMap = new ConcurrentHashMap<Long, DataSource>();
	/**{1001(pipelineId):etlStrategy(EtlStrategy's instance)}*/
	private static Map<Long, List<EtlStrategy>> etlStrategyMap = new MapMaker().makeComputingMap(new Function<Long, List<EtlStrategy>>() {
		public List<EtlStrategy> apply(Long input) {
			return new ArrayList<EtlStrategy>();
		}
	});
	private static Map<Long, Map<String, EtlStrategy>> etlStrategyTable = new MapMaker().makeComputingMap(new Function<Long, Map<String, EtlStrategy>>() {
		public Map<String, EtlStrategy> apply(Long input) {
			return new HashMap<String, EtlStrategy>();
		}
	});

	/**{taskId:scheduleConfig{ScheduleConfig's instance}}*/
	private static Map<Long, ScheduleConfig> scheduleConfig = new ConcurrentHashMap<Long, ScheduleConfig>();
	
	public static void put(Long jobId, Job job) {
		cache.put(jobId, job);
	}
	
	public static Job get(Long jobId) {
		return cache.get(jobId);
	}
	
	public static void remove(Long jobId) {
		cache.remove(jobId);
	}
	
	public static Job getOne() {
		Job ret = null;
		Iterator<Long> iter = cache.keySet().iterator();
		while (iter.hasNext()) {
			ret = cache.get(8450L);
			break;
		}
		return ret;
	}

	public static Map<Long, List<Pipeline>> getPipelineMap() {
		return pipelineMap;
	}

	public static Map<Long, TransEntity> getTransEntityMap() {
		return transEntityMap;
	}

	public static Map<Long, DataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	public static Map<Long, List<EtlStrategy>> getEtlStrategyMap() {
		return etlStrategyMap;
	}

	public static void setEtlStrategyMap(Map<Long, List<EtlStrategy>> etlStrategyMap) {
		JobConfigCache.etlStrategyMap = etlStrategyMap;
	}

	public static Map<Long, ScheduleConfig> getScheduleConfig() {
		return scheduleConfig;
	}

	public static void setScheduleConfig(Map<Long, ScheduleConfig> scheduleConfig) {
		JobConfigCache.scheduleConfig = scheduleConfig;
	}
	
	public static Map<Long, Map<String, EtlStrategy>> getEtlStrategyTable() {
		return etlStrategyTable;
	}

	public static void setEtlStrategyTable(
			Map<Long, Map<String, EtlStrategy>> etlStrategyTable) {
		JobConfigCache.etlStrategyTable = etlStrategyTable;
	}
}
