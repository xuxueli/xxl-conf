package com.xxl.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.xxl.core.constant.CommonDic.CommonReturn;
import com.xxl.core.constant.CommonDic.ReturnCodeEnum;
import com.xxl.core.result.ReturnT;
import com.xxl.service.IUserService;
import com.xxl.service.helper.LoginIdentitySessionHelper;

/**
 * 用户信息
 * @author xuxueli
 */
@Service()
public class UserServiceImpl implements IUserService {

	
	/*
	 * 登陆
	 * @see com.xxl.service.IUserService#login(java.lang.String, java.lang.String)
	 */
	@Override
	public ReturnT<String> login(HttpSession session, String userName, String password) {
		if (StringUtils.isBlank(userName)) {
			return new ReturnT<String>(ReturnCodeEnum.FAIL.code(), "登陆失败,请输入账号");
		}
		if (StringUtils.isBlank(password)) {
			return new ReturnT<String>(ReturnCodeEnum.FAIL.code(), "登陆失败,请输入密码");
		}
		if (!userName.equals("admin") && !password.equals("123456")) {
			return new ReturnT<String>(ReturnCodeEnum.FAIL.code(), "登陆失败,账号邮箱或密码错误");
		}
		
		LoginIdentitySessionHelper.login(session, userName, password);
		return CommonReturn.success;
	}
	
}
