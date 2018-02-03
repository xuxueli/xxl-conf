package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.core.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * Login Service
 *
 * @author xuxueli 2018-02-04 03:25:55
 */
@Configuration
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "XXL_CONF_LOGIN_IDENTITY";

    @Value("${xxl.conf.login.username}")
    private String username;    // can not user @Value or XML in mvc inteceptor，because inteceptor work with mvc, init before service

    @Value("${xxl.conf.login.password}")
    private String password;

    private String makeToken(String username, String password){
        String tokenTmp = DigestUtils.md5DigestAsHex(String.valueOf(username + "_" + password).getBytes());	// md5
        tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);	// md5-hex
        return tokenTmp;
    }

    public boolean login(HttpServletResponse response, String usernameParam, String passwordParam, boolean ifRemember){

        String loginTolen = makeToken(username, password);
        String paramToken = makeToken(usernameParam, passwordParam);

        if (!loginTolen.equals(paramToken)){
            return false;
        }

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginTolen, ifRemember);
        return true;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response){
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
    }

    public boolean ifLogin(HttpServletRequest request){

        String loginTolen = makeToken(username, password);
        String paramToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);

        if (paramToken==null || !loginTolen.equals(paramToken.trim())) {
            return false;
        }
        return true;
    }

}
