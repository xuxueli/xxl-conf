package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.IXxlConfNodeDao;
import com.xxl.conf.admin.service.IXxlConfNodeService;
import com.xxl.conf.core.XxlCfgClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 配置
 * @author xuxueli
 */
@Service()
public class XxlConfNodeServiceImpl implements IXxlConfNodeService {
	
	@Resource
	private IXxlConfNodeDao xxlConfNodeDao;

	@Override
	public Map<String,Object> pageList(int offset, int pagesize, String nodeKey) {

		// xxlConfNode in mysql
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("nodeKey", nodeKey);

		List<XxlConfNode> data = xxlConfNodeDao.pageList(params);
		int list_count = xxlConfNodeDao.pageListCount(params);

		// xxlConfNode in mysql, fill value in zookeeper
		Set<String> dataSet = new HashSet<String>();
		Map<String, String> zkOriginMap = XxlCfgClient.client.getAllData();
		if (CollectionUtils.isNotEmpty(data)) {
			for (XxlConfNode node: data) {
				if (MapUtils.isNotEmpty(zkOriginMap)) {
					node.setNodeValueReal(zkOriginMap.get(node.getNodeKey()));
				}
				dataSet.add(node.getNodeKey());
			}
		}

		// add xxlConfNode only in zookeeper
		if (MapUtils.isNotEmpty(zkOriginMap)) {
			for (Map.Entry<String, String> zkNode: zkOriginMap.entrySet()) {
				if (!dataSet.contains(zkNode.getKey())) {
					XxlConfNode node = new XxlConfNode();
					node.setNodeKey(zkNode.getKey());
					node.setNodeValueReal(zkNode.getValue());
				}
			}
		}

		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("data", data);
		maps.put("recordsTotal", list_count);		// 总记录数
		maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
		return maps;

	}

	@Override
	public ReturnT<String> deleteByKey(String znodeKey) {
		if (StringUtils.isNotBlank(znodeKey)) {
			xxlConfNodeDao.deleteByKey(znodeKey);
			XxlCfgClient.client.delete(znodeKey);
		}
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> add(XxlConfNode xxlConfNode) {
		if (xxlConfNode == null || StringUtils.isBlank(xxlConfNode.getNodeKey())) {
			return new ReturnT<String>(500, "Key不可为空");
		}
		XxlConfNode existNode = xxlConfNodeDao.selectByKey(xxlConfNode.getNodeKey());
		if (existNode!=null) {
			return new ReturnT<String>(500, "Key对应的配置已经存在,不可重复添加");
		}
		xxlConfNodeDao.insert(xxlConfNode);
		XxlCfgClient.client.setData(xxlConfNode.getNodeKey(), xxlConfNode.getNodeValue());
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> update(XxlConfNode xxlConfNode) {
		if (xxlConfNode == null || StringUtils.isBlank(xxlConfNode.getNodeKey())) {
			return new ReturnT<String>(500, "Key不可为空");
		}
		int ret = xxlConfNodeDao.update(xxlConfNode);
		if (ret < 1) {
			return new ReturnT<String>(500, "Key对应的配置不存在,请确认");
		}
		XxlCfgClient.client.setData(xxlConfNode.getNodeKey(), xxlConfNode.getNodeValue());
		return ReturnT.SUCCESS;
	}

}
