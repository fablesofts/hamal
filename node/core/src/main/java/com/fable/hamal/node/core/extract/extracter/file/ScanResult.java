package com.fable.hamal.node.core.extract.extracter.file;

public class ScanResult {

    private String result = "";
    private Status status = Status.FAILED;
    private String signature = "";
    private Exception exception = null;

    public enum Status { PASSED, FAILED, ERROR }

    public static final String ERROR = "ERROR";
    public static final String FIALED = "filed";
    public static final String PASSED = "passed";
    
    public static final String STREAM_PREFIX = "stream: ";
    public static final String RESPONSE_OK = "stream: OK";
    public static final String FOUND_SUFFIX = "FOUND";

    public static final String RESPONSE_SIZE_EXCEEDED = "INSTREAM size limit exceeded. ERROR";
    public static final String RESPONSE_ERROR_WRITING_FILE = "Error writing to temporary file. ERROR";

    public ScanResult(String result){
        setResult(result);
    }

    public ScanResult(Exception ex){
        setException(ex);
        setStatus(Status.ERROR);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getResult() {
        return result;
    }

    public static String setResult(String result) {
        if (result == null){
            
            return ERROR;
            
        } else if (RESPONSE_OK.equals(result)) {
            
            return PASSED;
            
        } else if (result.endsWith(FOUND_SUFFIX)){
            
            return FIALED;
            
        } else if (RESPONSE_SIZE_EXCEEDED.equals(result)) {
            
            return ERROR;
            
        } else if (RESPONSE_ERROR_WRITING_FILE.equals(result)) {
            
            return ERROR;
            
        } else {
            return ERROR;
        }
  
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
