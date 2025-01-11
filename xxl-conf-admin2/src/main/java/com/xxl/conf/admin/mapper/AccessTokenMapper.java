package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.model.entity.AccessToken;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* AccessToken Mapper
*
* Created by xuxueli on '2024-12-08 16:22:29'.
*/
@Mapper
public interface AccessTokenMapper {

    /**
    * 新增
    */
    public int insert(@Param("accessToken") AccessToken accessToken);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("accessToken") AccessToken accessToken);

    /**
    * Load查询
    */
    public AccessToken load(@Param("id") int id);

    /**
    * 分页查询Data
    */
	public List<AccessToken> pageList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("accessToken") String accessToken);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("accessToken") String accessToken);

    /**
    * 查询有效AccessToken
    */
    List<AccessToken> queryValidityAccessToken(@Param("status") int status);


}
