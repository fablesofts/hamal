package com.fable.hamal.log.model;

import java.io.Serializable;
import java.util.Date;


public class SysLogDetail implements Serializable {

    private static final long serialVersionUID = -4957272577164813761L;

    private Long id;
    
    private Long taskId;
    
    private String taskSerial;
    
    private String operationType;
    
    private String operationDetail;
    
    private String operationResults;
    
    private Date startTime;
    
    private Date endTime;

    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOperationType() {
        return operationType;
    }

    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    
    public String getOperationDetail() {
        return operationDetail;
    }

    
    public void setOperationDetail(String operationDetail) {
        this.operationDetail = operationDetail;
    }

    
    public String getOperationResults() {
        return operationResults;
    }

    
    public void setOperationResults(String operationResults) {
        this.operationResults = operationResults;
    }

    
    public Date getStartTime() {
        return startTime;
    }

    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    
    public Date getEndTime() {
        return endTime;
    }

    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskSerial() {
		return taskSerial;
	}

	public void setTaskSerial(String taskSerial) {
		this.taskSerial = taskSerial;
	}
}
