package com.xxl.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xxl.core.model.ZNodeEntry;
import com.xxl.dao.IZNodeEntryDao;

/**
 * 配置
 * @author xuxueli
 */
@Repository
public class ZNodeEntryDaoImpl implements IZNodeEntryDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<ZNodeEntry> selectLikeKey(String znodeKey) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("znodeKey", znodeKey);
		return sqlSessionTemplate.selectList("ZNodeEntryMapper.selectLikeKey", params);
	}

	@Override
	public int deleteByKey(String znodeKey) {
		return sqlSessionTemplate.delete("ZNodeEntryMapper.deleteByKey", znodeKey);
	}

	@Override
	public void insert(ZNodeEntry node) {
		sqlSessionTemplate.insert("ZNodeEntryMapper.insert", node);
	}

	@Override
	public ZNodeEntry selectByKey(String znodeKey) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("znodeKey", znodeKey);
		return sqlSessionTemplate.selectOne("ZNodeEntryMapper.selectByKey", params);
	}

	@Override
	public int update(ZNodeEntry node) {
		return sqlSessionTemplate.update("ZNodeEntryMapper.update", node);
	}
	
}
