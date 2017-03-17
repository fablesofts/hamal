/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.extract.chain.manager.impl;

import java.util.ArrayList;
import java.util.List;

import com.fable.hamal.node.common.cache.config.PumpConfigCache;
import com.fable.hamal.node.core.controller.HamalContextHelper;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.node.core.extract.chain.LinkExtracterChain;
import com.fable.hamal.node.core.extract.chain.SimpleExtracterChain;
import com.fable.hamal.node.core.extract.chain.manager.ChainManager;
import com.fable.hamal.node.core.extract.chain.manager.ExtensionChainManager;
import com.fable.hamal.node.core.extract.extracter.db.ColumnConvertExtracter;
import com.fable.hamal.node.core.extract.extracter.db.ColumnFilterExtracter;
import com.fable.hamal.node.core.extract.extracter.db.ColumnMappingExtracter;
import com.fable.hamal.node.core.extract.extracter.file.FileVirusFilterExtracter;
import com.fable.hamal.node.core.extract.extracter.file.FileWordsConvertExtracter;
import com.fable.hamal.node.core.extract.extracter.file.FileWordsFilterExtracter;
import com.fable.hamal.node.core.pipe.DataPipe;
import com.fable.hamal.node.core.pipe.FullDataPipe;
import com.fable.hamal.node.core.pipe.PipeKey;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.config.metadata.File;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.config.metadata.TimestampTable;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.model.envelope.data.Source;
import com.fable.hamal.shuttle.common.model.envelope.data.Target;
import com.fable.hamal.shuttle.common.model.envelope.et.EtlMacrocosm;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.ColumnConverter;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.ColumnConverter.Verter;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.FileContentConverter;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.ColumnFilter;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.FilecntFilter;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.VirusFilter;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;
import com.fable.hamal.shuttle.common.model.envelope.et.mapping.ColumnMapping;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.Filter;


/**
 * 简单过滤处理器--和{@link SimpleExtracterChain}相对应</br>
 * 修改两者中任何一个都要关联修改另一个
 * @author xieruidong 2013年12月18日 下午4:37:40
 */
public class SimpleChainManager implements ChainManager {

	/**{@link DataPipe}在Spring配置文件中的id*/
	private static final String FULLDATAPIPE_IN_SPRING = "fullDataPipe";
	private static final String DATAPIPE_IN_SPRING = "dataPipe";
	/**扩展链管理器*/
	private ExtensionChainManager extendChainManager;
	
	/**生成抽取链，数据库到数据库，文件到文件，扩展抽取链始终在标准抽取链结尾*/
	@Override
	public void process(Long pumpId, PipeKey key, BatchData data) {
		Pump pump = PumpConfigCache.get(pumpId);
		pump.getId();
		List<LinkExtracterChain> chains = new ArrayList<LinkExtracterChain>();
//		FileWordsConvertExtracter wordsFilterExtracter = new FileWordsConvertExtracter(null);
//	    chain.appendExtracter(wordsFilterExtracter);

	    Source source = pump.getSource();
	    List<Pair> pairs = pump.getPairs();
	    
	    if (source instanceof Table || source instanceof TimestampTable) {
	    	for (Pair pair : pairs) {
	    		LinkExtracterChain chain = new SimpleExtracterChain();
	    		EtlMacrocosm etl =  pair.getEtl();
	    		if (null == etl) {
	    			continue;
	    		} else {
	    			chain = new SimpleExtracterChain();
	    		}
	    		//1.过滤
	    		List<ColumnFilter> columnFilters = etl.getFilter().getColumnFilter();
	    		if (0 != columnFilters.size()) {
	    			ColumnFilterExtracter cfe = new ColumnFilterExtracter(columnFilters);
		    	    cfe.setPump(pump);
		    		chain.appendExtracter(cfe);
	    		}
	    		
	    		
	    		Target target = pair.getTarget();
	    		if (target instanceof Table ) {
	    			//2.映射
	    			List<ColumnMapping> columnMappings= etl.getMapping().getColumnsMapping();
		    		if (0 != columnMappings.size()) {
		    			ColumnMappingExtracter cme = new ColumnMappingExtracter(columnMappings);
		    			cme.setPump(pump);
		    			chain.appendExtracter(cme);
		    		}
		    		//3.转换
		    		List<ColumnConverter> columnConverters = etl.getConverter().getColumnConverter();
		    		if (0 != columnConverters.size()) {
		    			ColumnConvertExtracter cce = new ColumnConvertExtracter(columnConverters);
		    			cce.setPump(pump);
		    			chain.appendExtracter(cce);
		    		}
	    		}
	    		
	    		if (null != chain) {
	    			chains.add(chain);
	    		}
		    }
	    }
	    
	    if (source instanceof File) {
	    	for (Pair pair : pairs) {
	    		LinkExtracterChain chain = new SimpleExtracterChain();
	    		EtlMacrocosm etl =  pair.getEtl();
	    		if (null == etl) {
	    			continue;
	    		} else {
	    			chain = new SimpleExtracterChain();
	    		}
	    		//1.过滤
	    		//1.1病毒过滤
	    		List<String> virusFilters = etl.getFilter().getVirusFilter().getFiles();
	    		if (virusFilters.contains("*") || virusFilters.contains("*.*")) {
	    			FileVirusFilterExtracter ffe = new FileVirusFilterExtracter();
	    			chain.appendExtracter(ffe);
	    		}
	    		//1.2文件内容过滤
	    		List<FilecntFilter> filecntFilters = etl.getFilter().getFilecntFilter();
	    		if (0 != filecntFilters.size()) {
	    			FileWordsFilterExtracter wordsFilterExtracter = new FileWordsFilterExtracter(filecntFilters);
	    		    chain.appendExtracter(wordsFilterExtracter);
	    		}
	    		
	    		List<FileContentConverter> fileContentConverters = etl.getConverter().getFilecntConverter();
	    		if (0 != fileContentConverters.size()) {
	    			
	    		}
	    	}
	    }
		
//		ColumnMappingExtracter columnFilterExtracter = new ColumnMappingExtracter();
//		FileVirusFilterExtracter virusFileterExtracter = new FileVirusFilterExtracter();
//		FileWordsFilterExtracter wordsFilterExtracter = new FileWordsFilterExtracter(tempParam());
//		if (null != extendChainManager) {
//			chain.appendExtracters(extendChainManager.getExtensionExtracters(pumpId, key, data));
//		}
//		new TerminalExtracter().doExtract(data, chain);
	    BatchData mergeData = new BatchData();
	    if (chains.isEmpty()) {
	    	mergeData = data;
	    } else {
	    	for (LinkExtracterChain chain : chains) {
		    	chain.doExtract(data);
		    	mergeData.merge(data);
		    }
	    }
		FullDataPipe fullPipe = HamalContextHelper.getBean(FULLDATAPIPE_IN_SPRING);
		DataPipe pipe = HamalContextHelper.getBean(DATAPIPE_IN_SPRING);
		fullPipe.put(key, mergeData);
		pipe.put(key.getProcessId(), mergeData);
		
	}
	
	public ExtensionChainManager getExtendChainManager() {
		return extendChainManager;
	}

	public void setExtendChainManager(ExtensionChainManager extendChainManager) {
		this.extendChainManager = extendChainManager;
	}
}
