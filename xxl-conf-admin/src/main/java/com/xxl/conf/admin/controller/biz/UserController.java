package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.constant.enums.UserStatuEnum;
import com.xxl.conf.admin.model.dto.UserDTO;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.UserService;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
@RequestMapping("/user")
public class UserController {
    public static final String ADMIN_ROLE = "ADMIN";

    @Resource
    private UserService userService;
    @Resource
    private ApplicationService applicationService;

    @RequestMapping
    @XxlSso(role = Consts.ADMIN_ROLE)
    public String index(Model model) {

        model.addAttribute("UserStatuEnum", UserStatuEnum.values());
        model.addAttribute("RoleEnum", RoleEnum.values());

        // applicationList
        Response<List<Application>> appResp = applicationService.findAll();
        List<Application> applicationList = appResp!=null?appResp.getData():null;
        model.addAttribute("applicationList", applicationList);

        return "biz/user";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<PageModel<UserDTO>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                 @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                 String username,
                                                 @RequestParam(required = false, defaultValue = "-1") int status) {

        PageModel<UserDTO> pageModel = userService.pageList(offset, pagesize, username, status);
        return Response.ofSuccess(pageModel);
    }

    @RequestMapping("/add")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> add(UserDTO xxlJobUser) {
        return userService.insert(xxlJobUser);
    }

    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(HttpServletRequest request, UserDTO xxlJobUser) {
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        return userService.update(xxlJobUser, loginInfoResponse.getData().getUserName());
    }

    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(HttpServletRequest request,
                                   @RequestParam("ids[]") List<Integer> ids) {
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        return userService.deleteByIds(ids, Integer.parseInt(loginInfoResponse.getData().getUserId()));
    }

    @RequestMapping("/grantAppnames")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> grantAppnames(@RequestParam("username") String username,
                                          @RequestParam("appnames") String appnames) {
        return userService.grantAppnames(username, appnames);
    }

}
