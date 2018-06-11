package com.xxl.conf.admin.dao;

import com.xxl.conf.admin.core.model.XxlConfNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xuxueli on 16/10/8.
 */
@Mapper
public interface XxlConfNodeDao {

	public List<XxlConfNode> pageList(@Param("offset") int offset,
									  @Param("pagesize") int pagesize,
									  @Param("env") String env,
									  @Param("appname") String appname,
									  @Param("key") String key);
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("env") String env,
							 @Param("appname") String appname,
							 @Param("key") String key);

	public int delete(@Param("env") String env, @Param("key") String key);

	public void insert(XxlConfNode xxlConfNode);

	public XxlConfNode load(@Param("env") String env, @Param("key") String key);

	public int update(XxlConfNode xxlConfNode);
	
}
