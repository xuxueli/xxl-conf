package com.xxl.conf.admin.dao.impl;

import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.dao.IXxlConfNodeDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置
 * @author xuxueli
 */
@Repository
public class XxlConfNodeDaoImpl implements IXxlConfNodeDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<XxlConfNode> pageList(Map<String, Object> params) {
		return sqlSessionTemplate.selectList("XxlConfNodeMapper.pageList", params);
	}

	@Override
	public int pageListCount(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("XxlConfNodeMapper.pageListCount", params);
	}

	@Override
	public int deleteByKey(String nodeGroup, String nodeKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.delete("XxlConfNodeMapper.deleteByKey", params);
	}

	@Override
	public void insert(XxlConfNode node) {
		sqlSessionTemplate.insert("XxlConfNodeMapper.insert", node);
	}

	@Override
	public XxlConfNode selectByKey(String nodeGroup, String nodeKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.selectOne("XxlConfNodeMapper.selectByKey", params);
	}

	@Override
	public int update(XxlConfNode node) {
		return sqlSessionTemplate.update("XxlConfNodeMapper.update", node);
	}
	
}
