package com.xxl.core.util;

import javax.servlet.http.HttpSession;

/**
 * HTTP.SESSION操作
 * @author Administrator
 *
 */
public class HttpSessionUtil {

	/**
	 * 存入
	 * @param session
	 * @param key
	 * @param value
	 */
	public static void set(HttpSession session, String key, Object value){
		session.setMaxInactiveInterval(30 * 60);	// 失效时间: 30分钟
		session.setAttribute(key, value);
	}
	
	/**
	 * 取出
	 * @param session
	 * @param key
	 * @return
	 */
	public static Object get(HttpSession session, String key){
		return session.getAttribute(key);
	}
	
	/**
	 * 移除
	 * @param session
	 * @param key
	 */
	public static void remove(HttpSession session, String key){
		session.removeAttribute(key);
	}
	
}
