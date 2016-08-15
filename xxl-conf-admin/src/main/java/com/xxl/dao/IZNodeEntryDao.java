package com.xxl.dao;

import java.util.List;

import com.xxl.core.model.ZNodeEntry;


/**
 * 配置
 * @author xuxueli
 */
public interface IZNodeEntryDao {
	
	public List<ZNodeEntry> selectLikeKey(String znodeKey);

	public int deleteByKey(String znodeKey);

	public void insert(ZNodeEntry node);

	public ZNodeEntry selectByKey(String znodeKey);

	public int update(ZNodeEntry node);
	
}
