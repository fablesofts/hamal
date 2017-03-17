package com.fable.hamal.node.core.extract.extracter.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

import com.fable.hamal.node.core.extract.Extracter;
import com.fable.hamal.node.core.extract.chain.ExtracterChain;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.data.envelope.FileData;
import com.fable.hamal.shuttle.common.data.envelope.FileDataBatch;
import com.fable.hamal.shuttle.common.model.envelope.et.converter.FileContentConverter;


public class FileWordsConvertExtracter implements Extracter {

    private List<String> m_filterList;
    private String m_keyword;
    
    public FileWordsConvertExtracter() {
        
    }
        
    public FileWordsConvertExtracter(FileContentConverter fileConverter) {
        this.m_filterList = fileConverter.getContent();
        this.m_keyword = fileConverter.getName();
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
            
            file.setInputdata(new SensitiveInputStream(file.getInputdata(), m_filterList, m_keyword));
        }

        chain.doExtract(data);
        
    }
    
    
    private class SensitiveInputStream extends InputStream {

        private final PushbackInputStream m_in;
        private final List<String> m_filterList;
        private final String m_keyword;
        private final int m_length;
        
        
        public SensitiveInputStream(InputStream in, List<String> filterList, String keyword){
            this.m_in = new PushbackInputStream(in, 1024 * 64);
            this.m_filterList = filterList;
            this.m_keyword = keyword;
            this.m_length = maxLength(m_filterList);
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
            System.out.println(temp);
            if(this.m_length != temp.length()){
                //替换敏感字段
                String filter = replaceWords(temp, false);
                //获得最后几个字
                String pushStr = temp.substring(temp.length() - m_length, temp.length());
                System.out.println(filter);
                System.out.println(1024*64);
                System.out.println(filter.getBytes("GBK").length);
                System.arraycopy(filter.getBytes("GBK"), 0, b, 0, filter.getBytes("GBK").length);
                m_in.unread(pushStr.getBytes("GBK"));
                iResult = filter.getBytes("GBK").length;
            }else{
                //替换敏感字段
                String filter = replaceWords(temp, true);
                iResult = filter.getBytes("GBK").length;
                System.arraycopy(filter.getBytes("GBK"), 0, b, 0, filter.getBytes("GBK").length);
            }

            return iResult;
        }
        
        @Override
        public void close() throws IOException {
            this.m_in.close();
        }
        
        private String replaceWords(String paragraph, boolean isEnd){
            String mess = null;
            if(isEnd){
                mess = paragraph;
            }else{
                mess = paragraph.substring(0, paragraph.length() - m_length);    
            }
            
            System.out.println(mess);
            for(String word : m_filterList){
                 System.out.println(word + "--->" + this.m_keyword);
                 mess =  mess.replaceAll(word, this.m_keyword);
                 System.out.println(mess);
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
        
    }

}
