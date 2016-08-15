package com.xxl.controller.core;

import java.io.Serializable;

/**
 * 登陆信息缓存
 * @author xuxueli
 */
@SuppressWarnings("serial")
public class LoginIdentity implements Serializable {
	
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
