package com.xxl.conf.admin.controller;


import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.service.impl.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by xuxueli on 16/7/30.
 */

@Controller
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class.getName());

    @Resource
    private LoginService loginService;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        return "redirect:/conf";
    }

    @RequestMapping("/toLogin")
    @PermessionLimit(limit=false)
    public String toLogin(Model model, HttpServletRequest request) {
        if (loginService.ifLogin(request) != null) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value="login", method= RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
        // valid
        if (loginService.ifLogin(request) != null) {
            return ReturnT.SUCCESS;
        }

        // param
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)){
            return new ReturnT<String>(500, "账号或密码为空");
        }
        boolean ifRem = (StringUtils.isNotBlank(ifRemember) && "on".equals(ifRemember))?true:false;

        // do login
        return loginService.login(response, userName, password, ifRem);
    }

    @RequestMapping(value="logout", method=RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
        if (loginService.ifLogin(request) != null) {
            loginService.logout(request, response);
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

}
