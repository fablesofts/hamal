package com.fable.hamal.node.core.extract.extracter.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.node.core.select.FTPSelector;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.FileData;
import com.fable.hamal.shuttle.common.data.envelope.FileDataBatch;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;


public class FileVirusFilterExtracter implements Extracter  {

    private final static Logger logger = LoggerFactory.getLogger(FileVirusFilterExtracter.class);
    
    private static int count = 0;
    
    @Override
    public void start() {
        
    }

    @Override
    public boolean isStart() {
        return false;
    }

    @Override
    public void doExtract(BatchData data, ExtracterChain chain) {
        if(logger.isInfoEnabled()){
            logger.info("扫描病毒的方法已经进入！！");
        }
        if(null == data){
            chain.doExtract(data);
            return;
        }

        final FileDataBatch fdb = data.getFdb();
        if(null == fdb){
            chain.doExtract(data);
            return;
        }

        final List<FileData> files = fdb.getList();
        if(null == files || 0 == files.size()){
            chain.doExtract(data);
            return;
        }
        
        for(final FileData file : files) {
            
            file.setInputdata(new VirusInputStream(file.getInputdata()));
        }
        if(logger.isInfoEnabled()) {
            logger.info("扫描病毒的方法已经离开！！");
        }
        chain.doExtract(data);
    }
    
    
    private class VirusInputStream extends InputStream {

        private final InputStream m_in;
        private final String m_ip;
        private final String m_port;
        private final ClamScan cs;
        
        public VirusInputStream(final InputStream in){
            
            this.m_in = in;
            this.m_ip = HamalPropertyConfigurer.getHamalProperty("node.virus.clamav.ip");
            this.m_port = HamalPropertyConfigurer.getHamalProperty("node.virus.clamav.port");
            cs = new ClamScan(this.m_ip, Integer.parseInt(this.m_port), 10000);
        }
        
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            
            final int iResult = this.m_in.read(b, off, len);
            if(iResult < 0){
                return iResult; 
            }
            final byte[] checkBytes = new byte[iResult];
            System.arraycopy(b, off, checkBytes, 0, iResult);
            String state = cs.scan(checkBytes);
            if(ScanResult.FIALED.equals(state)){
                
                throw new IOException("Found illegal file in SJJH");
            }
            System.out.println(count++);
            return iResult;
        }


        @Override
        public int read() throws IOException {
            
            return this.m_in.read();
        }
        
        @Override
        public int read(final byte[] b) throws IOException{
            
            return this.read(b, 0, b.length);
        }
        
        @Override
        public void close() throws IOException {
            this.m_in.close();
        }
    }
}
