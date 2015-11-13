package com.xxl.core.constant;


public class Configuration {
	
	private String key01;
	private String key02;
	private String key03;
	
	public Configuration() {
		super();
	}

	public Configuration(String key01, String key02, String key03) {
		super();
		this.key01 = key01;
		this.key02 = key02;
		this.key03 = key03;
	}
	
	public String getKey01() {
		return key01;
	}
	public void setKey01(String key01) {
		this.key01 = key01;
	}
	public String getKey02() {
		return key02;
	}
	public void setKey02(String key02) {
		this.key02 = key02;
	}
	public String getKey03() {
		return key03;
	}
	public void setKey03(String key03) {
		this.key03 = key03;
	}
	
}
