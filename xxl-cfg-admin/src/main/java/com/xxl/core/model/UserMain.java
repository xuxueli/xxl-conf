package com.xxl.core.model;

import java.io.Serializable;

/**
 * 用户信息
 * @author xuxueli
 */
@SuppressWarnings("serial")
public class UserMain implements Serializable {
	
	private String userName;
	private String password;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
