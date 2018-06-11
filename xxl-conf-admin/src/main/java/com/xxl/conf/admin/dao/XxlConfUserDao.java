package com.xxl.conf.admin.dao;

import com.xxl.conf.admin.core.model.XxlConfUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-03-01
 */
@Mapper
public interface XxlConfUserDao {

    public List<XxlConfUser> pageList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("username") String username,
                                      @Param("permission") int permission);
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("username") String username,
                             @Param("permission") int permission);

    public int add(XxlConfUser xxlConfUser);

    public int update(XxlConfUser xxlConfUser);

    public int delete(@Param("username") String username);

    public XxlConfUser load(@Param("username") String username);

}
