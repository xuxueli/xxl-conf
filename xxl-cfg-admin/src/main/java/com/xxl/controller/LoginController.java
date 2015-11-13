package com.xxl.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.controller.annotation.PermessionType;
import com.xxl.controller.core.LoginIdentity;
import com.xxl.core.result.ReturnT;
import com.xxl.service.IUserService;
import com.xxl.service.helper.LoginIdentitySessionHelper;

/**
 * 登陆/注销
 * @author xuxueli
 */
@Controller
public class LoginController {
	
	@Autowired
	private IUserService userService;
	
	@RequestMapping("")
	@PermessionType(loginState = false)
	public String index(HttpSession session){
		LoginIdentity identity =LoginIdentitySessionHelper.loginCheck(session);
		if (identity != null) {
			return "redirect:/zkcfg";
		}
		return "login";
	}
	
	@RequestMapping("/login")
	@ResponseBody
	@PermessionType(loginState = false)
	public ReturnT<String> login(HttpSession session, String userName, String password){
		return userService.login(session, userName, password);
	}
	
	@RequestMapping("/logout")
	@ResponseBody
	@PermessionType
	public ReturnT<String> logout(HttpSession session){
		LoginIdentitySessionHelper.logout(session);
		return new ReturnT<String>();
	}
	
}
