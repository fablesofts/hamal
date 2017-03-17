/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.select.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.common.dialect.sqldb.SqldbDialectFactory;
import com.fable.hamal.node.common.cache.config.PumpConfigCache;
import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.node.core.select.SqlTableTimestampSelector;
import com.fable.hamal.shuttle.common.data.media.DataCarrierType;
import com.fable.hamal.shuttle.common.data.media.source.DbCarrierSource;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.model.config.metadata.TimestampTable;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.metadata.sqldb.DbDriverClass;

/**
 * 数据库表的Selector产生工厂
 * @author xieruidong 2013年11月27日 下午3:35:30
 */
public class SqlTableTimestampSelectorFactory implements SelectorFactory {

	private static final Logger logger = LoggerFactory.getLogger(SqlTableTimestampSelectorFactory.class);
	private SqldbDialectFactory sqldbDialectFactory;
	
	public Selector createSelector(Long pumpId) {
		return createSelector(PumpConfigCache.get(pumpId));
	}

	public Selector createSelector(Pump pump) {
	    SqlTableTimestampSelector selector = new SqlTableTimestampSelector();
		final Db db = ((TimestampTable)pump.getSource()).getDb();
		DbCarrierSource dcs = new DbCarrierSource();
		dcs.setUrl(db.getConnectUrl());
		dcs.setDriver(DbDriverClass.getDriver(db.getDbType()));
		dcs.setUsername(db.getUsername());
		dcs.setPassword(db.getPassword());
		dcs.setType(DataCarrierType.getOf(db.getDbType()));
		
		selector.setPump(pump);
		if (logger.isInfoEnabled()) {
			logger.info("selector.setDbDialect(sqldbDialectFactory.getDbDialect(pump.getId(), dcs));");
		}
		selector.setDbDialect(sqldbDialectFactory.getDbDialect(pump.getId(), dcs));
		return selector;
	}

	//-----------------------------------------------------setter&&getter----------------------------------------------
	public SqldbDialectFactory getSqldbDialectFactroy() {
		return sqldbDialectFactory;
	}

	public void setSqldbDialectFactory(SqldbDialectFactory sqldbDialectFactory) {
		this.sqldbDialectFactory = sqldbDialectFactory;
	}
}
