package com.xxl.conf.admin.service;


import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.ReturnT;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2015-9-4 18:19:52
 */
public interface IXxlConfNodeService {

	public boolean ifHasProjectPermission(XxlConfUser loginUser, String loginEnv, String appname);

	public Map<String,Object> pageList(int offset,
									   int pagesize,
									   String appname,
									   String key,
									   XxlConfUser loginUser,
									   String loginEnv);

	public ReturnT<String> delete(String key, XxlConfUser loginUser, String loginEnv);

	public ReturnT<String> add(XxlConfNode xxlConfNode, XxlConfUser loginUser, String loginEnv);

	public ReturnT<String> update(XxlConfNode xxlConfNode, XxlConfUser loginUser, String loginEnv);

    /*ReturnT<String> syncConf(String appname, XxlConfUser loginUser, String loginEnv);*/


    // ---------------------- rest api ----------------------

    public ReturnT<Map<String, String>> find(String accessToken, String env, List<String> keys);

    public DeferredResult<ReturnT<String>> monitor(String accessToken, String env, List<String> keys);

}
