package com.xxl.conf.admin.service;


import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.util.ReturnT;

import java.util.Map;

/**
 * 配置
 * @author xuxueli 2015-9-4 18:19:52
 */
public interface IXxlConfNodeService {

	public Map<String,Object> pageList(int offset, int pagesize, String nodeKey);

	public ReturnT<String> deleteByKey(String znodeKey);

	public ReturnT<String> freshConf(XxlConfNode node);

}
