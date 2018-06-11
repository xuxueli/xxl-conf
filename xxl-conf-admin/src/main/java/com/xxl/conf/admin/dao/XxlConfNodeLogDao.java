package com.xxl.conf.admin.dao;

import com.xxl.conf.admin.core.model.XxlConfNodeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xuxueli on 16/10/8.
 */
@Mapper
public interface XxlConfNodeLogDao {

	public List<XxlConfNodeLog> findByKey(@Param("env") String env, @Param("key") String key);

	public void add(XxlConfNodeLog xxlConfNode);

	public int deleteTimeout(@Param("env") String env,
							 @Param("key") String key,
							 @Param("length") int length);

}
