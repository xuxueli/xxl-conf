package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.constant.enums.UserStatuEnum;
import com.xxl.conf.admin.mapper.UserMapper;
import com.xxl.conf.admin.model.adaptor.UserAdaptor;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.entity.User;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.response.Response;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Configuration
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "XXL_CONF_LOGIN_IDENTITY";

    @Resource
    private UserMapper xxlJobUserMapper;

    // ********************** for token **********************

    /**
     * make token from user
     */
    private String makeToken(LoginUserDTO loginUserDTO){
        String tokenJson = GsonTool.toJson(loginUserDTO);
        String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
        return tokenHex;
    }

    /**
     * parse token to user
     */
    private LoginUserDTO parseToken(String tokenHex){
        LoginUserDTO loginUser = null;
        if (tokenHex != null) {
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
            loginUser = GsonTool.fromJson(tokenJson, LoginUserDTO.class);
        }
        return loginUser;
    }

    // ********************** for login **********************

    /**
     * login (write cookie)
     *
     * @param response
     * @param username
     * @param password
     * @param ifRemember
     * @return
     */
    public Response<String> login(HttpServletResponse response, String username, String password, boolean ifRemember){

        // param
        if (StringTool.isBlank(username) || StringTool.isBlank(password)){
            return Response.ofFail( I18nUtil.getString("login_param_empty") );
        }

        // valid user, empty、status、passowrd
        User user = xxlJobUserMapper.loadByUserName(username);
        if (user == null) {
            return Response.ofFail( I18nUtil.getString("login_param_unvalid") );
        }
        if (user.getStatus() != UserStatuEnum.NORMAL.getValue()) {
            return Response.ofFail( I18nUtil.getString("login_status_invalid") );
        }
        String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!passwordMd5.equals(user.getPassword())) {
            return Response.ofFail( I18nUtil.getString("login_param_unvalid") );
        }

        // find resource

        // make token
        LoginUserDTO loginUserDTO = UserAdaptor.adapt2LoginUser(user);
        String loginToken = makeToken(loginUserDTO);

        // do login
        CookieTool.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
        return Response.ofSuccess();
    }

    /**
     * logout (remove cookie)
     *
     * @param request
     * @param response
     */
    public Response<String> logout(HttpServletRequest request, HttpServletResponse response){
        CookieTool.remove(request, response, LOGIN_IDENTITY_KEY);
        return Response.ofSuccess();
    }

    /**
     * check iflogin (match cookie and db, del cookie if invalid)
     *
     * @param request
     * @return
     */
    public LoginUserDTO checkLogin(HttpServletRequest request, HttpServletResponse response){
        String cookieToken = CookieTool.getValue(request, LOGIN_IDENTITY_KEY);
        if (cookieToken != null) {
            LoginUserDTO loginUser = null;
            try {
                loginUser = parseToken(cookieToken);
            } catch (Exception e) {
                logout(request, response);
            }
            if (loginUser != null) {
                User dbUser = xxlJobUserMapper.loadByUserName(loginUser.getUsername());
                if (dbUser != null) {
                    if (loginUser.getPassword().equals(dbUser.getPassword())) {
                        return loginUser;
                    }
                }
            }
        }
        return null;
    }

    /**
     * get login user (from request, copy from cookie)
     *
     * @param request
     * @return
     */
    public LoginUserDTO getLoginUser(HttpServletRequest request){
        LoginUserDTO loginUser = (LoginUserDTO) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        return loginUser;
    }

    /**
     * login user is admin
     *
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request){
        LoginUserDTO loginUser = getLoginUser(request);
        return loginUser != null && RoleEnum.matchByValue(loginUser.getRole()) == RoleEnum.ADMIN;
    }

}
