package com.fable.hamal.shuttle.common.syslog;

import java.util.HashMap;
import java.util.Map;


public class SysLogId {
    
    // sysLogId : XXX ; taskId :YYY
    private static Map<String,Long> sysLogId = new HashMap<String, Long>();
    
    public static synchronized Map<String,Long> getSysLogId(){
        return sysLogId;
    }
    
    public static synchronized void setSysLogId(Long taskId,Long LogId){
        sysLogId.put("taskId", taskId);
        sysLogId.put("sysLogId", LogId);
    }
}
