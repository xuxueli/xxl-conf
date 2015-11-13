package com.xxl.dao;

import com.xxl.core.model.UserMain;

/**
 * 配置
 * @author xuxueli
 */
public interface IUserMainDao {
	
	public UserMain selectByPwd(String userName, String password);
	
}
