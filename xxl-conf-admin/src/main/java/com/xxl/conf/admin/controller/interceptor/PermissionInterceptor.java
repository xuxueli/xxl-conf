package com.xxl.conf.admin.controller.interceptor;

import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.util.CookieUtil;
import com.xxl.conf.core.core.XxlConfPropConf;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * 权限拦截, 简易版
 * @author xuxueli 2015-12-12 18:09:04
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	public static final String LOGIN_IDENTITY_KEY = "XXL_CONF_LOGIN_IDENTITY";
	public static String LOGIN_IDENTITY_TOKEN = null;

	@Override
	public void afterPropertiesSet() throws Exception {

		String username = XxlConfPropConf.get("xxl.conf.login.username");
		String password = XxlConfPropConf.get("xxl.conf.login.password");

		// login token
		String tokenTmp = DigestUtils.md5DigestAsHex(String.valueOf(username + "_" + password).getBytes());	// md5
		tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);	// md5-hex

		LOGIN_IDENTITY_TOKEN = tokenTmp;
	}

	public static boolean login(HttpServletResponse response, String usernameParam, String passwordParam, boolean ifRemember){
		// login token
		String tokenTmp = DigestUtils.md5DigestAsHex(String.valueOf(usernameParam + "_" + passwordParam).getBytes());
		tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);

		if (!LOGIN_IDENTITY_TOKEN.equals(tokenTmp)){
			return false;
		}

		// do login
		CookieUtil.set(response, LOGIN_IDENTITY_KEY, LOGIN_IDENTITY_TOKEN, ifRemember);
		return true;
	}
	public static void logout(HttpServletRequest request, HttpServletResponse response){
		CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
	}
	public static boolean ifLogin(HttpServletRequest request){
		String indentityInfo = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
		if (indentityInfo==null || !LOGIN_IDENTITY_TOKEN.equals(indentityInfo.trim())) {
			return false;
		}
		return true;
	}



	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		if (!ifLogin(request)) {
			HandlerMethod method = (HandlerMethod)handler;
			PermessionLimit permission = method.getMethodAnnotation(PermessionLimit.class);
			if (permission == null || permission.limit()) {
				response.sendRedirect(request.getContextPath() + "/toLogin");
				//request.getRequestDispatcher("/toLogin").forward(request, response);
				return false;
			}
		}

		return super.preHandle(request, response, handler);
	}

}
