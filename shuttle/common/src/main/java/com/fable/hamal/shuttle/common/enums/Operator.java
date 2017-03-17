/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.enums;

/**
 * 
 * @author xieruidong 2013年11月5日 上午10:02:01
 */
public enum Operator {

	EQUAL("="), GREATER(">"), NOTGREATER("<="), LESS("<"), NOTLESS(">="), CONTAIN("contain"), REGEXP("regexp");
	
	
	String operator;
	
	private Operator(String operator) {
		this.operator = operator;
	}
	
	public String toJson() {
		
		return this.operator;
	}
}
