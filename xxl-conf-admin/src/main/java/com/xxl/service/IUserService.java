package com.xxl.service;

import javax.servlet.http.HttpSession;

import com.xxl.core.result.ReturnT;

public interface IUserService {

	/**
	 * 登陆
	 * @param password
	 * @return
	 */
	ReturnT<String> login(HttpSession session, String userName, String password);

}
