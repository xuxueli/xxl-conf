package com.xxl.conf.admin.service;


import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.ReturnT;

import java.util.Map;

/**
 * @author xuxueli 2015-9-4 18:19:52
 */
public interface IXxlConfNodeService {

	public Map<String,Object> pageList(int offset,
									   int pagesize,
									   String env,
									   String appname,
									   String key,
									   XxlConfUser loginUser);

	public ReturnT<String> delete(String env, String key, XxlConfUser loginUser);

	public ReturnT<String> add(XxlConfNode xxlConfNode, XxlConfUser loginUser);

	public ReturnT<String> update(XxlConfNode xxlConfNode, XxlConfUser loginUser);

    ReturnT<String> syncConf(String env, String appname, XxlConfUser loginUser);

}
