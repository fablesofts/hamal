package com.fable.hamal.node.core.trigger;

import com.fable.hamal.common.dialect.sqldb.SqldbDialectFactory;
import com.fable.hamal.node.common.cache.config.PumpConfigCache;
import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.node.core.select.factory.SelectorFactory;
import com.fable.hamal.shuttle.common.data.media.DataCarrierType;
import com.fable.hamal.shuttle.common.data.media.source.DbCarrierSource;
import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.model.config.metadata.Table;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.metadata.sqldb.DbDriverClass;


public class AddTableSelecterFactory implements SelectorFactory {
    
    private SqldbDialectFactory sqldbDialectFactory;
    
    public Selector createSelector(Long pumpId) {
        return createSelector(PumpConfigCache.get(pumpId));
    }

    public Selector createSelector(Pump pump) {
        final Db db = ((Table)pump.getSource()).getDb();
        AddTableSelector selector = new AddTableSelector();
        DbCarrierSource dcs = new DbCarrierSource();
        dcs.setUrl(db.getConnectUrl());
        dcs.setDriver(DbDriverClass.getDriver(db.getDbType()));
        dcs.setUsername(db.getUsername());
        dcs.setPassword(db.getPassword());
        dcs.setType(DataCarrierType.getOf(db.getDbType()));
        
        selector.setPump(pump);
        selector.setDbDialect(sqldbDialectFactory.getDbDialect(pump.getId(), dcs));
        return selector;
    }
    
  //-----------------------------------------------------setter&&getter----------------------------------------------
    public SqldbDialectFactory getSqldbDialectFactory() {
        return sqldbDialectFactory;
    }

    public void setSqldbDialectFactory(SqldbDialectFactory sqldbDialectFactory) {
        this.sqldbDialectFactory = sqldbDialectFactory;
    }

}
