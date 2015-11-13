package com.xxl.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.xxl.cfg.core.XxlCfgClient;
import com.xxl.core.constant.CommonDic.ReturnCodeEnum;
import com.xxl.core.exception.WebException;
import com.xxl.core.model.ZNodeEntry;
import com.xxl.dao.IZNodeEntryDao;
import com.xxl.service.IZNodeEntryService;

/**
 * 配置
 * @author xuxueli
 */
@Service()
public class ZNodeEntryServiceImpl implements IZNodeEntryService {
	
	@Resource
	private IZNodeEntryDao zNodeEntryDao;

	@Override
	public List<ZNodeEntry> selectLikeKey(String znodeKey) {
		// sqlite filter
		List<ZNodeEntry> dbOriginList = zNodeEntryDao.selectLikeKey(znodeKey);
		Map<String, ZNodeEntry> dbFilterMap = new HashMap<String, ZNodeEntry>();
		if (CollectionUtils.isNotEmpty(dbOriginList)) {
			for (ZNodeEntry item : dbOriginList) {
				dbFilterMap.put(item.getZnodeKey(), item);
			}
		}
		// zookeeper filter
		Map<String, String> zkOriginMap = XxlCfgClient.client.getAllData();
		Map<String, String> zkFilterMap = new HashMap<String, String>();
		if (StringUtils.isBlank(znodeKey)) {
			zkFilterMap = zkOriginMap;
		} else {
			if (MapUtils.isNotEmpty(zkOriginMap)) {
				for (Entry<String, String> item : zkOriginMap.entrySet()) {
					if (StringUtils.isBlank(znodeKey) || item.getKey().startsWith(znodeKey)) {
						zkFilterMap.put(item.getKey(), item.getValue());
					}
				}
			}
		}
		
		// all filter set
		Set<String> filterSet = new HashSet<String>();
		filterSet.addAll(dbFilterMap.keySet());
		filterSet.addAll(zkFilterMap.keySet());
		
		// mix filter
		List<ZNodeEntry> mixFilterList = new ArrayList<ZNodeEntry>();
		if (CollectionUtils.isNotEmpty(filterSet)) {
			for (String key : filterSet) {
				ZNodeEntry node = new ZNodeEntry();
				if (dbFilterMap.containsKey(key)) {
					node = dbFilterMap.get(key);
				}
				if (zkFilterMap.containsKey(key)) {
					node.setZnodeKey(key);
					node.setZnodeValueReal(zkFilterMap.get(key));
				}
				mixFilterList.add(node);
			}
		}
		
		return mixFilterList;
	}

	@Override
	public void deleteByKey(String znodeKey) {
		if (StringUtils.isNotBlank(znodeKey)) {
			zNodeEntryDao.deleteByKey(znodeKey);
			XxlCfgClient.client.delete(znodeKey);
		}
	}

	@Override
	public void updateNode(ZNodeEntry node) {
		if (node == null || StringUtils.isBlank(node.getZnodeKey())) {
			throw new WebException(ReturnCodeEnum.FAIL.code(), "操作失败,znodeKey不可为空");
		}
		int ret = zNodeEntryDao.update(node);
		if (ret < 1) {
			zNodeEntryDao.insert(node);
		}
		XxlCfgClient.client.setData(node.getZnodeKey(), node.getZnodeValue());
	}
	
	
}
