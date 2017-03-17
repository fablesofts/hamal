/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.datasource;

import com.fable.hamal.shuttle.common.data.media.DataCarrierSource;
import com.fable.hamal.shuttle.common.model.envelope.Pump;

/**
 * 
 * @author xieruidong 2013年11月11日 下午2:35:05
 */
public interface DataSourceManage {
	
	<T> T getDataSource(Long pumpId, DataCarrierSource carrierSource);
	
	<T> T getDataSource(Pump pump, DataCarrierSource carrierSource);
	
	void destroy(Long pumpId);
	
	void destroy(Long pumpId, DataCarrierSource carrierSource);
	
	void destroy(Pump pump, DataCarrierSource carrierSource);
}
