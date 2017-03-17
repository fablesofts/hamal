package com.fable.hamal.node.core.extract.extracter.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.FileData;
import com.fable.hamal.shuttle.common.data.envelope.FileDataBatch;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.FileContentConverter;
import com.fable.hamal.shuttle.common.model.envelope.et.filter.FilecntFilter;


public class FileWordsFilterExtracter implements Extracter {

    private List<String> filterList = new ArrayList<String>();
    private List<FilecntFilter> fileFilters;
    
    public FileWordsFilterExtracter(){
        
    }
    
    public FileWordsFilterExtracter(FilecntFilter fileFilter){
        filterList = fileFilter.getContent();
    }
    
    public FileWordsFilterExtracter(List<FilecntFilter> fileFilters) {
    	this.fileFilters = fileFilters;
    	for (FilecntFilter fileFilter : fileFilters) {
    		filterList.addAll(fileFilter.getContent());
    	}
    }
    
    @Override
    public void start() {
        
    }

    @Override
    public boolean isStart() {
        return false;
    }

    @Override
    public void doExtract(BatchData data, ExtracterChain chain) {

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
        
        for(final FileData file : files){
            
            file.setInputdata(new SensitiveInputStream(file.getInputdata(), filterList));
        }

        chain.doExtract(data);
        
    }
    
    private class SensitiveInputStream extends InputStream {

        private final PushbackInputStream m_in;
        private final List<String> m_filterList;
        private final int m_length;
        
        
        public SensitiveInputStream(InputStream in, List<String> filterList){
            m_in = new PushbackInputStream(in, 1024);
            this.m_filterList = filterList;
            m_length = maxLength(m_filterList);
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
        public int read(byte[] b, final int off, final int len) throws IOException {
            
            int iResult = this.m_in.read(b, off, len);
            System.out.println(iResult);
            if(iResult == -1)
                return iResult;
            String temp = new String(b, 0, iResult, "GBK");
            
            if(this.m_length != temp.length()){
                //替换敏感字段
                String filter = replaceWords(temp, false);
                
                if(Boolean.parseBoolean(filter)){
                    
                    throw new IOException("Found illegal file in SJJH");
                }
                //获得最后几个字
                String pushStr = temp.substring(temp.length() - m_length, temp.length());
                System.out.println(filter);
                System.arraycopy(filter.getBytes("GBK"), 0, b, 0, filter.getBytes("GBK").length);
                m_in.unread(pushStr.getBytes("GBK"));
                iResult = filter.getBytes("GBK").length;
            }else{
                String filter = replaceWords(temp, true);
                iResult = filter.getBytes("GBK").length;
                System.arraycopy(filter.getBytes("GBK"), 0, b, 0, filter.getBytes("GBK").length);
            }

            return iResult;
            
        }
        
        private String replaceWords(String paragraph,  boolean isEnd){
            String mess = null;
            if(isEnd){
                mess = paragraph;
            }else{
                mess = paragraph.substring(0, paragraph.length() - m_length);
            }
            
            for(String word : m_filterList){
                 if(mess.contains(word)){
                     return "true";
                 }
            }
            
            return mess;
        }
        
        
        private int maxLength(List<String> filterList){
            
            int length = 0;
            
            for (int i = 0; i < filterList.size(); i++) {
                
                if(filterList.get(i).length() > length){
                    length = filterList.get(i).length();
                }
            }
            return length;
        }
        
        
        @Override
        public void close() throws IOException {
            this.m_in.close();
        }
        
        
    }

}
