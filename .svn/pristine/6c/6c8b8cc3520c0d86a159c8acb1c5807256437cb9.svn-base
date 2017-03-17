/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load.factory;

import java.util.ArrayList;
import java.util.List;

import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.common.dialect.sqldb.SqldbDialectFactory;
import com.fable.hamal.node.core.load.Loader;
import com.fable.hamal.node.core.load.SqlTableLoader;
import com.fable.hamal.shuttle.common.data.media.DataCarrierType;
import com.fable.hamal.shuttle.common.data.media.source.DbCarrierSource;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.metadata.sqldb.DbDriverClass;

/**
 * 
 * @author xieruidong 2013年11月28日 下午2:19:23
 */
public class SqlTableLoaderFactory extends AbstractLoaderFactory implements LoaderFactory {

	private SqldbDialectFactory sqldbDialectFactory;
	
	public Loader createLoader(Long pumpId) {
		return null;
	}

	public Loader createLoader(Pump pump) {
		List<Loader> result = createLoaders(pump);
		return 0 == result.size() ? null : result.get(0);
	}

	public List<Loader> createLoaders(Long pumpId) {
		return null;
	}

	public List<Loader> createLoaders(Pump pump) {
		List<Loader> result = new ArrayList<Loader>();
		List<DbDialect> dbDialectList = new ArrayList<DbDialect>();
		SqlTableLoader loader = new SqlTableLoader();
		for (Pair pair : pump.getPairs()) {
			Db db = ((Table)pair.getTarget()).getDb();
			DbCarrierSource dcs = new DbCarrierSource();
			dcs.setUrl(db.getConnectUrl());
			dcs.setDriver(DbDriverClass.getDriver(db.getDbType()));
			dcs.setUsername(db.getUsername());
			dcs.setPassword(db.getPassword());
			dcs.setType(DataCarrierType.getOf(db.getDbType()));
			
			loader.setPair(pair);
			loader.setPump(pump);
			dbDialectList.add(sqldbDialectFactory.getDbDialect(pump.getId(), dcs));
		}
		loader.setDbDialect(dbDialectList);
		result.add(loader);
		return result;
	}

	//-----------------------------------------------------setter&&getter----------------------------------------------
	public SqldbDialectFactory getSqldbDialectFactory() {
		return sqldbDialectFactory;
	}

	public void setSqldbDialectFactory(SqldbDialectFactory sqldbDialectFactory) {
		this.sqldbDialectFactory = sqldbDialectFactory;
	}
}
