package com.xxl.service;

import java.util.List;

import com.xxl.core.model.ZNodeEntry;

/**
 * 配置
 * @author xuxueli 2015-9-4 18:19:52
 */
public interface IZNodeEntryService {
	
	public List<ZNodeEntry> selectLikeKey(String znodeKey);

	public void deleteByKey(String znodeKey);

	public void updateNode(ZNodeEntry node);

}
