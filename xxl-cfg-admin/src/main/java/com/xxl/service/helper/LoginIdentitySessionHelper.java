package com.xxl.service.helper;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.xxl.controller.core.LoginIdentity;
import com.xxl.core.model.UserMain;
import com.xxl.core.util.HttpSessionUtil;

/**
 * 用户登陆信息，操作相关
 * @author Administrator
 *
 */
public class LoginIdentitySessionHelper {
	private static final String LOGIN_IDENTITY = "LOGIN_IDENTITY";
	
	/**
	 * 填充“用户登录信息”
	 * @param identity
	 * @param user
	 */
	private static void fillin(LoginIdentity identity, UserMain user){
		try {
			BeanUtils.copyProperties(identity, user);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * “用户登陆信息”-初始化
	 * @param user
	 */
	public static void login(HttpSession session, UserMain user) {
		// 初始化用户登陆信息
		LoginIdentity identity = new LoginIdentity();
		fillin(identity, user);
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
