package com.xxl.conf.admin.dao;


import com.xxl.conf.admin.core.model.XxlConfNode;

import java.util.List;
import java.util.Map;


/**
 * 配置
 * @author xuxueli
 */
public interface IXxlConfNodeDao {

	public List<XxlConfNode> pageList(Map<String, Object> params);
	public int pageListCount(Map<String, Object> params);

	public int deleteByKey(String nodeKey);

	public void insert(XxlConfNode xxlConfNode);

	public XxlConfNode selectByKey(String nodeKey);

	public int update(XxlConfNode xxlConfNode);
	
}
