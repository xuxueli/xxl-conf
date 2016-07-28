package com.xxl.service.helper;

import com.xxl.controller.core.LoginIdentity;
import com.xxl.core.util.HttpSessionUtil;

import javax.servlet.http.HttpSession;

/**
 * 用户登陆信息，操作相关
 * @author Administrator
 *
 */
public class LoginIdentitySessionHelper {
	private static final String LOGIN_IDENTITY = "LOGIN_IDENTITY";


	/**
	 * “用户登陆信息”-初始化
	 */
	public static void login(HttpSession session, String userName, String password) {
		// 初始化用户登陆信息
		LoginIdentity identity = new LoginIdentity();
		identity.setUserName(userName);
		identity.setPassword(password);
		HttpSessionUtil.set(session, LOGIN_IDENTITY, identity);
	}

	/**
	 * “用户登陆信息”-注销
	 * @param session
	 */
	public static void logout(HttpSession session) {
		// SESSION移除 “用户登陆信息”
		HttpSessionUtil.remove(session, LOGIN_IDENTITY);
	}
	
	public static LoginIdentity loginCheck(HttpSession session){
		Object obj = HttpSessionUtil.get(session, LOGIN_IDENTITY);
		if (obj != null) {
			return (LoginIdentity) obj;
		}
		return null;
	}
}
