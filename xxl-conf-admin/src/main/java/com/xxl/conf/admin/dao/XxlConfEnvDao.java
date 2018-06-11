package com.xxl.conf.admin.dao;

import com.xxl.conf.admin.core.model.XxlConfEnv;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xuxueli on 2018-05-30
 */
@Mapper
public interface XxlConfEnvDao {

    public List<XxlConfEnv> findAll();

    public int save(XxlConfEnv xxlConfEnv);

    public int update(XxlConfEnv xxlConfEnv);

    public int delete(@Param("env") String env);

    public XxlConfEnv load(@Param("env") String env);

}