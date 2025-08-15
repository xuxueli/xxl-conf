package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.ConfDataService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.MapTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* ConfData Controller
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Controller
@RequestMapping("/confdata")
public class ConfDataController {

    @Resource
    private ConfDataService confDataService;
    @Resource
    private ApplicationService applicationService;

    /**
    * 页面
    */
    @RequestMapping
    @XxlSso
    public String index(HttpServletRequest request, Model model) {

        // application
        List<Application> applicationList = findPermissionApplication(request, applicationService);
        model.addAttribute("applicationList", applicationList);

        return "biz/confdata";
    }

    /**
     * find permission application list
     *
     * @param request
     * @return
     */
    public static List<Application> findPermissionApplication(HttpServletRequest request, ApplicationService applicationService){
        List<Application> applicationList = applicationService.findAll().getData();

        // check role - admin
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (!XxlSsoHelper.hasRole(loginInfoResponse.getData(), RoleEnum.ADMIN.getValue()).isSuccess()) {
            // parse appname from login-info
            List<String> appnameList = MapTool.isNotEmpty(loginInfoResponse.getData().getExtraInfo())
                    ? Arrays.asList(loginInfoResponse.getData().getExtraInfo().get("appnameList").split(",")):new ArrayList<>();
            applicationList = applicationList
                    .stream()
                    .filter(application -> appnameList.contains(application.getAppname()))
                    .collect(Collectors.toList());
        }
        return applicationList;
    }

    /**
     * check if has permission application
     */
    public static boolean hasPermissionApplication(HttpServletRequest request, ApplicationService applicationService, String appname){
        List<Application> applicationList = findPermissionApplication(request, applicationService);
        return !applicationList
                .stream()
                .filter(application -> application.getAppname().equals(appname))
                .toList()
                .isEmpty();
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso
    public Response<PageModel<ConfData>> pageList(HttpServletRequest request,
                                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                                  @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                  @RequestParam("env") String env,
                                                  @RequestParam("appname") String appname,
                                                  @RequestParam("key") String key) {

        // valid application
        if (!hasPermissionApplication(request, applicationService, appname)){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        // page
        PageModel<ConfData> pageModel = confDataService.pageList(offset, pagesize, env, appname, key);
        return Response.ofSuccess(pageModel);
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @XxlSso
    public Response<ConfData> load(long id){
        return confDataService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso
    public Response<String> insert(ConfData confData, HttpServletRequest request){

        // valid application
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (!hasPermissionApplication(request, applicationService, confData.getAppname())){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        return confDataService.insert(confData, loginInfoResponse.getData().getUserName());
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso
    public Response<String> delete(@RequestParam("ids[]") List<Long> ids, HttpServletRequest request){
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        return confDataService.delete(ids, loginInfoResponse.getData().getUserName());
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @XxlSso
    public Response<String> update(ConfData confData, HttpServletRequest request){

        // valid application
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (!hasPermissionApplication(request, applicationService, confData.getAppname())){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        return confDataService.update(confData, loginInfoResponse.getData().getUserName());
    }

}
