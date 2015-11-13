package com.xxl.core.exception;

/**
 * 自定义异常
 * @author xuxueli
 */

@SuppressWarnings("serial")
public class WebException extends RuntimeException {
	
	public String exceptionKey;
	public String exceptionMsg;

	public WebException() {
	}

	public WebException(String exceptionKey, String exceptionMsg) {
		super(exceptionMsg);
		this.exceptionKey = exceptionKey;
		this.exceptionMsg = exceptionMsg;
	}

	public WebException(String exceptionMsg) {
		super(exceptionMsg);
		this.exceptionMsg = exceptionMsg;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public String getExceptionKey() {
		return exceptionKey;
	}

	public void setExceptionKey(String exceptionKey) {
		this.exceptionKey = exceptionKey;
	}
}
