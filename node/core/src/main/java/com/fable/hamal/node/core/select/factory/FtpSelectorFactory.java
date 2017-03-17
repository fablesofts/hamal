package com.fable.hamal.node.core.select.factory;

import com.fable.hamal.node.common.cache.config.PumpConfigCache;
import com.fable.hamal.node.core.select.FTPSelector;
import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;


public class FtpSelectorFactory implements SelectorFactory {

    public Selector createSelector(Long pumpId) {
        
        return createSelector(PumpConfigCache.get(pumpId));
    }

    public Selector createSelector(Pump pump) {
        FTPSelector selector = new FTPSelector();
        selector.setPump(pump);
        return selector;
    }

}
