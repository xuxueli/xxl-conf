package com.xxl.conf.admin.core.model;

import java.util.Date;

/**
 * @author xuxueli 2018-03-01
 */
public class XxlConfNodeLog {

	private String env;
	private String key;			// 配置Key
	private String title;		// 配置描述
	private String value;		// 配置Value
	private Date addtime;		// 操作时间
	private String optuser;		// 操作人


	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getOptuser() {
		return optuser;
	}

	public void setOptuser(String optuser) {
		this.optuser = optuser;
	}
}
