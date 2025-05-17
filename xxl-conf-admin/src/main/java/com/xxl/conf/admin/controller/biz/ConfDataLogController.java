package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.ConfDataLogService;
import com.xxl.conf.admin.service.ConfDataService;
import com.xxl.conf.admin.service.impl.LoginService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* ConfData Controller
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Controller
@RequestMapping("/confdatalog")
public class ConfDataLogController {

    @Resource
    private ConfDataService confDataService;
    @Resource
    private ConfDataLogService confDataLogService;
    @Resource
    private ApplicationService applicationService;
    @Resource
    private LoginService loginService;

    /**
    * 页面
    */
    @RequestMapping
    @Permission
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") Long dataId) {

        // valid confData
        ConfData confData = null;
        Response<ConfData> response = confDataService.load(dataId);
        if (response.isSuccess() && response.getData()!=null) {
            confData = response.getData();
        }
        if (confData == null) {
            throw new RuntimeException(I18nUtil.getString("system_param_empty"));
        }
        model.addAttribute("confData", confData);

        // valid permission of appname
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        List<String> appnameList = loginUser.getPermission()!=null? Arrays.asList(loginUser.getPermission().split(",")):new ArrayList<>();
        if (!loginService.isAdmin(request) && !appnameList.contains(confData.getAppname())){
            throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
        }

        return "biz/confdatalog";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @Permission
    public Response<PageModel<ConfDataLog>> pageList(HttpServletRequest request,
                                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                                  @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                  @RequestParam(required = false, defaultValue = "-1") Long dataId) {

        // valid confData
        ConfData confData = null;
        Response<ConfData> response = confDataService.load(dataId);
        if (response.isSuccess() && response.getData()!=null) {
            confData = response.getData();
        }
        if (confData == null) {
            return Response.ofFail(I18nUtil.getString("system_param_empty"));
        }

        // valid application
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        List<String> appnameList = loginUser.getPermission()!=null? Arrays.asList(loginUser.getPermission().split(",")):new ArrayList<>();
        if (!loginService.isAdmin(request) && !appnameList.contains(confData.getAppname())){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        // page
        PageModel<ConfDataLog> pageModel = confDataLogService.pageList(offset, pagesize, dataId);
        return Response.ofSuccess(pageModel);
    }

    /**
    * 删除
    */
    /*@RequestMapping("/delete")
    @ResponseBody
    @Permission
    public Response<String> delete(@RequestParam("ids[]") List<Long> ids, HttpServletRequest request){
        return confDataLogService.delete(ids);
    }*/

}
