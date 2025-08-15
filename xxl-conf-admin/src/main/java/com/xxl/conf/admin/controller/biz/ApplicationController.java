package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.sso.core.annotation.XxlSso;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import jakarta.annotation.Resource;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* Service Controller
*
* Created by xuxueli on '2024-12-07 16:55:27'.
*/
@Controller
@RequestMapping("/application")
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    /**
    * 页面
    */
    @RequestMapping
    @XxlSso
    public String index(Model model) {
        return "biz/application";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso
    public Response<PageModel<Application>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                     @RequestParam(required = false, defaultValue = "10") int pagesize) {
        PageModel<Application> pageModel = applicationService.pageList(offset, pagesize);
        return Response.ofSuccess(pageModel);
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @XxlSso
    public Response<Application> load(int id){
        return applicationService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> insert(Application service){
        return applicationService.insert(service);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return applicationService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(Application service){
        return applicationService.update(service);
    }

}
