/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fable.hamal.common.dialect.sqldb.DbDialect;
import com.fable.hamal.common.dialect.sqldb.SqldbDialectFactory;
import com.fable.hamal.node.core.load.Loader;
import com.fable.hamal.node.core.load.factory.AbstractLoaderFactory;
import com.fable.hamal.node.core.load.factory.LoaderFactory;
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
public class AddTableLoaderFactory extends AbstractLoaderFactory implements LoaderFactory {

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
		AddTableLoader loader = new AddTableLoader();
		List<DbDialect> dbDialectList = new ArrayList<DbDialect>();
		for (Pair pair : pump.getPairs()) {
			Db db = ((Table)pair.getTarget()).getDb();
			DbCarrierSource dcs = new DbCarrierSource();
			dcs.setUrl(db.getConnectUrl());
			dcs.setDriver(DbDriverClass.getDriver(db.getDbType()));
			dcs.setUsername(db.getUsername());
			dcs.setPassword(db.getPassword());
			dcs.setType(DataCarrierType.getOf(db.getDbType()));
			//获取源端方言
            Db sourceDb = ((Table)pump.getSource()).getDb();
            DbCarrierSource sourcedcs = new DbCarrierSource();
            sourcedcs.setUrl(sourceDb.getConnectUrl());
            sourcedcs.setDriver(DbDriverClass.getDriver(sourceDb.getDbType()));
            sourcedcs.setUsername(sourceDb.getUsername());
            sourcedcs.setPassword(sourceDb.getPassword());
            sourcedcs.setType(DataCarrierType.getOf(sourceDb.getDbType()));
			
			loader.setPair(pair);
			loader.setSourceDbDialect(sqldbDialectFactory.getDbDialect(pump.getId(), sourcedcs));
			dbDialectList.add(sqldbDialectFactory.getDbDialect(pump.getId(), dcs));
		}
		loader.setPump(pump);
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
