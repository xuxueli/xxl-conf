package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.core.model.XxlConfGroup;
import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.IXxlConfGroupDao;
import com.xxl.conf.admin.dao.IXxlConfNodeDao;
import com.xxl.conf.admin.service.IXxlConfNodeService;
import com.xxl.conf.core.XxlConfZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置
 * @author xuxueli 2016-08-15 22:53
 */
@Service()
public class XxlConfNodeServiceImpl implements IXxlConfNodeService {
	
	@Resource
	private IXxlConfNodeDao xxlConfNodeDao;
	@Resource
	private IXxlConfGroupDao xxlConfGroupDao;

	@Override
	public Map<String,Object> pageList(int offset, int pagesize, String nodeGroup, String nodeKey) {

		// xxlConfNode in mysql
		List<XxlConfNode> data = xxlConfNodeDao.pageList(offset, pagesize, nodeGroup, nodeKey);
		int list_count = xxlConfNodeDao.pageListCount(offset, pagesize, nodeGroup, nodeKey);

		// fill value in zk
		if (CollectionUtils.isNotEmpty(data)) {
			for (XxlConfNode node: data) {
				String realNodeValue = XxlConfZkClient.getPathDataByKey(node.getGroupKey());
				node.setNodeValueReal(realNodeValue);
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
	public ReturnT<String> deleteByKey(String nodeGroup, String nodeKey) {
		if (StringUtils.isBlank(nodeGroup) && StringUtils.isBlank(nodeKey)) {
			return new ReturnT<String>(500, "参数缺失");
		}

		xxlConfNodeDao.deleteByKey(nodeGroup, nodeKey);

		String groupKey = XxlConfZkClient.generateGroupKey(nodeGroup, nodeKey);
		XxlConfZkClient.deletePathByKey(groupKey);

		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> add(XxlConfNode xxlConfNode) {
		if (xxlConfNode == null) {
			return new ReturnT<String>(500, "参数缺失");
		}
		if (StringUtils.isBlank(xxlConfNode.getNodeGroup())) {
			return new ReturnT<String>(500, "配置分组不可为空");
		}
		XxlConfGroup group = xxlConfGroupDao.load(xxlConfNode.getNodeGroup());
		if (group==null) {
			return new ReturnT<String>(500, "配置分组不存在");
		}
		if (StringUtils.isBlank(xxlConfNode.getNodeKey())) {
			return new ReturnT<String>(500, "配置Key不可为空");
		}
		XxlConfNode existNode = xxlConfNodeDao.selectByKey(xxlConfNode.getNodeGroup(), xxlConfNode.getNodeKey());
		if (existNode!=null) {
			return new ReturnT<String>(500, "Key对应的配置已经存在,不可重复添加");
		}
		xxlConfNodeDao.insert(xxlConfNode);

		String groupKey = XxlConfZkClient.generateGroupKey(xxlConfNode.getNodeGroup(), xxlConfNode.getNodeKey());
		XxlConfZkClient.setPathDataByKey(groupKey, xxlConfNode.getNodeValue());

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

		String groupKey = XxlConfZkClient.generateGroupKey(xxlConfNode.getNodeGroup(), xxlConfNode.getNodeKey());
		XxlConfZkClient.setPathDataByKey(groupKey, xxlConfNode.getNodeValue());

		return ReturnT.SUCCESS;
	}

}
