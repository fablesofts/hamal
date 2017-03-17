package com.fable.hamal.node.core.load.factory;

import java.util.ArrayList;
import java.util.List;

import com.fable.hamal.node.core.load.FTPLoader;
import com.fable.hamal.node.core.load.Loader;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;


public class FtpLoaderFactory extends AbstractLoaderFactory implements LoaderFactory  {

    public Loader createLoader(Long pumpId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Loader createLoader(Pump pump) {
        List<Loader> result = createLoaders(pump);
        return 0 == result.size() ? null : result.get(0);
    }

    public List<Loader> createLoaders(Long pumpId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Loader> createLoaders(Pump pump) {
        List<Loader> result = new ArrayList<Loader>();
        for (Pair pair : pump.getPairs()) {
            FTPLoader loader = new FTPLoader();
            loader.setPair(pair);
            loader.setPump(pump);
            result.add(loader);
        }
        return result;
    }

}
