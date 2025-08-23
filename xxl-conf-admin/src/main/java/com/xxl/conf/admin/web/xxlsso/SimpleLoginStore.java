package com.xxl.conf.admin.web.xxlsso;

import com.xxl.conf.admin.model.entity.User;
import com.xxl.conf.admin.service.UserService;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Simple LoginStore
 *
 * 1、store by database；
 * 2、If you have higher performance requirements, it is recommended to use RedisLoginStore；
 *
 * @author xuxueli 2025-08-03
 */
@Component
public class SimpleLoginStore implements LoginStore {


    @Resource
    private UserService userService;


    @Override
    public Response<String> set(LoginInfo loginInfo) {

        // parse token-signature
        String token_sign = loginInfo.getSignature();

        // write token by UserId
        return userService.updateToken(Integer.valueOf(loginInfo.getUserId()), token_sign);
    }

    @Override
    public Response<String> update(LoginInfo loginInfo) {
        return Response.ofFail("not support");
    }

    @Override
    public Response<String> remove(String userId) {
        // delete token-signature
        return userService.updateToken(Integer.valueOf(userId), "");
    }

    /**
     * check through DB query
     */
    @Override
    public Response<LoginInfo> get(String userId) {

        // load login-user
        Response<User> userResponse = userService.loadById(Integer.valueOf(userId));
        if (!userResponse.isSuccess()) {
            return Response.ofFail("userId invalid.");
        }

        // find role
        List<String> roleList = StringTool.isNotBlank(userResponse.getData().getRole())
                ? Arrays.asList(userResponse.getData().getRole())
                :null;

        // fill extraInfo (appname list)
        Map<String, String> extraInfo = MapTool.newHashMap("appnameList", userResponse.getData().getAppnames());

        // build LoginInfo
        LoginInfo loginInfo = new LoginInfo(userId, userResponse.getData().getToken());
        loginInfo.setUserName(userResponse.getData().getUsername());
        loginInfo.setRealName(userResponse.getData().getRealName());
        loginInfo.setRoleList(roleList);
        loginInfo.setExtraInfo(extraInfo);

        return Response.ofSuccess(loginInfo);
    }

}
