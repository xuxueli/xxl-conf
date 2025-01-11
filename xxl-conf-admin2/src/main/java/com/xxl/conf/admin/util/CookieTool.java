package com.xxl.conf.admin.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Cookie Tool
 *
 * @author xuxueli 2015-12-12 18:01:06
 */
public class CookieTool {

	/**
	 * 默认缓存时间,单位/秒, 2H
 	 */
	private static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
	/**
	 * 保存路径,根路径
 	 */
	private static final String COOKIE_PATH = "/";

	/**
	 * add cookie
	 *
	 * @param response
	 * @param key
	 * @param value
	 * @param domain
	 * @param path
	 * @param maxAge	: >0 存活秒数，=0 删除， <0 浏览器推出则销毁；
	 * @param isHttpOnly
	 */
	private static void set(HttpServletResponse response, String key, String value,
							String domain, String path, int maxAge, boolean isHttpOnly) {

		// encode value
        try {
			value = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }

		// add cookie
        Cookie cookie = new Cookie(key, value);
		if (domain != null) {
			cookie.setDomain(domain);
		}
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(isHttpOnly);
		response.addCookie(cookie);
	}

	/**
	 * 新增 cookie
	 *
	 * @param response
	 * @param key
	 * @param value
	 * @param ifRemember 	： true - 永不过期，false - 浏览器推出则销毁；
	 */
	public static void set(HttpServletResponse response, String key, String value, boolean ifRemember) {
		int age = ifRemember?COOKIE_MAX_AGE:-1;
		set(response, key, value, null, COOKIE_PATH, age, true);
	}

	/**
	 * 新增 cookie
	 *
	 * @param response
	 * @param key
	 * @param value
	 * @param maxAge
	 */
	public static void set(HttpServletResponse response, String key, String value, int maxAge) {
		set(response, key, value, null, COOKIE_PATH, maxAge, true);
	}

	/**
	 * 删除 cookie
	 *
	 * @param request
	 * @param response
	 * @param key
	 */
	public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
		Cookie cookie = get(request, key);
		if (cookie != null) {
			set(response, key, "", null, COOKIE_PATH, 0, true);
		}
	}

	/**
	 * get cookie
	 *
	 * @param request
	 * @param key
	 */
	private static Cookie get(HttpServletRequest request, String key) {
		Cookie[] arr_cookie = request.getCookies();
		if (arr_cookie != null && arr_cookie.length > 0) {
			for (Cookie cookie : arr_cookie) {
				if (cookie.getName().equals(key)) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * 查询 cookie value
	 *
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getValue(HttpServletRequest request, String key) {
		Cookie cookie = get(request, key);
		if (cookie == null) {
			return null;
		}

		// decode value
		String value = cookie.getValue();
		try {
			value = URLDecoder.decode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return value;
	}

}