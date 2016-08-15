package com.xxl.core.model;

import java.io.Serializable;

/**
 * 配置节点
 * @author xuxueli 2015-9-4 15:26:01
 */
@SuppressWarnings("serial")
public class ZNodeEntry implements Serializable {
	
	private String znodeKey; 	// key of prop
	private String znodeValue; 	// value of prop [in sqlite]
	private String znodeDesc;	// description of prop
	
	private String znodeValueReal; // [in zk]
	
	public ZNodeEntry() {
		super();
	}

	public String getZnodeKey() {
		return znodeKey;
	}

	public void setZnodeKey(String znodeKey) {
		this.znodeKey = znodeKey;
	}

	public String getZnodeValue() {
		return znodeValue;
	}

	public void setZnodeValue(String znodeValue) {
		this.znodeValue = znodeValue;
	}

	public String getZnodeDesc() {
		return znodeDesc;
	}

	public void setZnodeDesc(String znodeDesc) {
		this.znodeDesc = znodeDesc;
	}

	public String getZnodeValueReal() {
		return znodeValueReal;
	}

	public void setZnodeValueReal(String znodeValueReal) {
		this.znodeValueReal = znodeValueReal;
	}
	
}
