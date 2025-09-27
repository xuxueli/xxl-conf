package com.xxl.conf.admin.service;

import com.xxl.conf.admin.model.dto.UserDTO;
import com.xxl.conf.admin.model.entity.User;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import java.util.List;

/**
 * user service
 *
 * @author xuxueli
 */
public interface UserService {

    /**
     * 新增
     */
    public Response<String> insert(UserDTO xxlJobUser);

    /**
     * 删除
     */
    public Response<String> delete(int id);

    /**
     * 删除
     */
    Response<String> deleteByIds(List<Integer> userIds, int optUserId);

    /**
     * 更新
     */
    public Response<String> update(UserDTO xxlJobUser, String optUserName);

    /**
     * 修改密码
     */
    public Response<String> updatePwd(String optUserName, String oldPassword, String password);

    /**
     * Load查询
     */
    public Response<User> loadByUserName(String username);

    /**
     * Load查询
     */
    Response<User> loadById(Integer integer);

    /**
     * 授权权限
     */
    public Response<String> grantAppnames(String username, String appnames);

    /**
     * 分页查询
     */
    public PageModel<UserDTO> pageList(int offset, int pagesize, String username, int status);

    /**
     * 更新token
     */
    Response<String> updateToken(Integer id, String token);

}
