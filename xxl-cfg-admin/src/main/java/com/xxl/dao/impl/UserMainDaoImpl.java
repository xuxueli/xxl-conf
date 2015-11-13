package com.xxl.dao.impl;

import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xxl.core.model.UserMain;
import com.xxl.dao.IUserMainDao;

/**
 * 配置
 * @author xuxueli
 */
@Repository
public class UserMainDaoImpl implements IUserMainDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public UserMain selectByPwd(String userName, String password) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("password", password);
		return sqlSessionTemplate.selectOne("UserMainMapper.selectByPwd", params);
	}

}
