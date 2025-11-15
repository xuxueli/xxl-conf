package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.mapper.UserMapper;
import com.xxl.conf.admin.model.adaptor.UserAdaptor;
import com.xxl.conf.admin.model.dto.UserDTO;
import com.xxl.conf.admin.model.entity.User;
import com.xxl.conf.admin.service.UserService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.encrypt.SHA256Tool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * user service
 *
 * @author xuxueli
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 新增
     */
    @Override
    public Response<String> insert(UserDTO xxlJobUser) {

        // adapt
        User user = UserAdaptor.adapt(xxlJobUser);

        // valid empty
        if (user == null) {
            return Response.ofFail(I18nUtil.getString("system_param_empty"));
        }
        // valid username
        if (StringTool.isBlank(user.getUsername())) {
            return Response.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("user_username"));
        }
        user.setUsername(user.getUsername().trim());
        if (!(user.getUsername().length()>=4 && user.getUsername().length()<=20)) {
            return Response.ofFail(I18nUtil.getString("system_lengh_limit")+"[4-20]");
        }
        // valid password
        if (StringTool.isBlank(user.getPassword())) {
            return Response.ofFail( I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        user.setPassword(user.getPassword().trim());
        if (!(user.getPassword().length()>=4 && user.getPassword().length()<=20)) {
            return Response.ofFail( I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // hash password
        String passwordHash = SHA256Tool.sha256(user.getPassword());
        user.setPassword(passwordHash);

        // valid user role
        if (RoleEnum.matchByValue(user.getRole()) == null) {
            return Response.ofFail("操作失败，角色ID非法");
        }

        // check repeat
        User existUser = userMapper.loadByUserName(user.getUsername());
        if (existUser != null) {
            return Response.ofFail( I18nUtil.getString("user_username_repeat") );
        }

        // save user
        userMapper.insert(user);

        return Response.ofSuccess();
    }

    /**
     * 删除
     */
    @Override
    public Response<String> delete(int id) {
        int ret = userMapper.delete(id);
        return ret>0? Response.ofSuccess() : Response.ofFail();
    }

    /**
     * 删除
     */
    @Override
    public Response<String> deleteByIds(List<Integer> userIds, int optUserId) {

        // valid
        if (CollectionTool.isEmpty(userIds)) {
            return Response.ofFail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("user_tips"));
        }

        // avoid opt login seft
        if (userIds.contains(optUserId)) {
            return Response.ofFail( I18nUtil.getString("user_update_loginuser_limit") );
        }

        int ret = userMapper.deleteByIds(userIds);
        return ret>0? Response.ofSuccess() : Response.ofFail();
    }

    /**
     * 更新
     */
    @Override
    public Response<String> update(UserDTO xxlJobUser, String optUserName) {

        // adapt
        User user = UserAdaptor.adapt(xxlJobUser);

        // avoid opt login seft
        if (optUserName.equals(user.getUsername())) {
            return Response.ofFail( I18nUtil.getString("user_update_loginuser_limit") );
        }

        // valid password
        if (StringTool.isNotBlank(user.getPassword())) {
            user.setPassword(user.getPassword().trim());
            if (!(user.getPassword().length()>=4 && user.getPassword().length()<=20)) {
                return Response.ofFail(  I18nUtil.getString("system_lengh_limit")+"[4-20]" );
            }
            // hash password
            String passwordHash = SHA256Tool.sha256(user.getPassword());
            user.setPassword(passwordHash);
        } else {
            user.setPassword(null);
        }

        // valid user role
        if (RoleEnum.matchByValue(user.getRole()) == null) {
            return Response.ofFail("操作失败，角色ID非法");
        }

        // update user
        int ret = userMapper.update(user);

        return ret>0? Response.ofSuccess() : Response.ofFail();
    }

    /**
     * 修改密码
     */
    public Response<String> updatePwd(String optUserName, String oldPassword, String password){
        // valid password
        if (StringTool.isBlank(oldPassword)){
            Response.ofFail( I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd") );
        }
        if (StringTool.isBlank(password)){
            Response.ofFail( I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_newpwd") );
        }
        password = password.trim();
        if (!(password.length()>=4 && password.length()<=20)) {
            Response.ofFail( I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }

        // md5 password
        String oldPasswordHash = SHA256Tool.sha256(oldPassword);
        String passwordHash = SHA256Tool.sha256(password);

        // valid old pwd
        User existUser = userMapper.loadByUserName(optUserName);
        if (!oldPasswordHash.equals(existUser.getPassword())) {
            return Response.ofFail(I18nUtil.getString("change_pwd_field_oldpwd") + I18nUtil.getString("system_error"));
        }

        // update pwd
        existUser.setPassword(passwordHash);
        userMapper.update(existUser);

        return Response.ofSuccess();
    }

    /**
     * Load查询
     */
    @Override
    public Response<User> loadByUserName(String username){
        User record = userMapper.loadByUserName(username);
        return Response.ofSuccess(record);
    }

    @Override
    public Response<User> loadById(Integer integer) {
        User record = userMapper.loadById(integer);
        return Response.ofSuccess(record);
    }

    @Override
    public Response<String> grantAppnames(String username, String appnames) {
        // update
        User existUser = userMapper.loadByUserName(username);
        existUser.setAppnames(appnames);
        userMapper.update(existUser);
        return Response.ofSuccess();
    }

    /**
     * 分页查询
     */
    @Override
    public PageModel<UserDTO> pageList(int offset, int pagesize, String username, int status) {

        // data
        List<User> pageList = userMapper.pageList(offset, pagesize, username, status);
        int totalCount = userMapper.pageListCount(offset, pagesize, username, status);

        // adaptor
        List<UserDTO> pageListDto = new ArrayList<>();
        if (CollectionTool.isNotEmpty(pageList)) {
            // dto list
            pageListDto = pageList
                    .stream()
                    .map(item-> UserAdaptor.adapt(item))
                    .collect(Collectors.toList());
        }

        // result
        PageModel<UserDTO> pageModel = new PageModel<UserDTO>();
        pageModel.setData(pageListDto);
        pageModel.setTotal(totalCount);

        return pageModel;
    }

    @Override
    public Response<String> updateToken(Integer id, String token) {
        int ret = userMapper.updateToken(id, token);
        return ret>0? Response.ofSuccess() : Response.ofFail();
    }

}
