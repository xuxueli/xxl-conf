package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.EnvironmentService;
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
* Environment Controller
*
* Created by xuxueli on '2024-12-07 15:40:35'.
*/
@Controller
@RequestMapping("/environment")
public class EnvironmentController {

    @Resource
    private EnvironmentService environmentService;

    /**
    * 页面
    */
    @RequestMapping
    @XxlSso(role = Consts.ADMIN_ROLE)
    public String index(Model model) {
        return "biz/environment";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<PageModel<Environment>> pageList(@RequestParam(required = true, defaultValue = "0") int offset,
                                                     @RequestParam(required = true, defaultValue = "10") int pagesize,
                                                     @RequestParam(required = false) String env,
                                                     @RequestParam(required = false) String name) {
        PageModel<Environment> pageModel = environmentService.pageList(offset, pagesize, env, name);
        return Response.ofSuccess(pageModel);
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<Environment> load(int id){
        return environmentService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> insert(Environment environment){
        return environmentService.insert(environment);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return environmentService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(Environment environment){
        return environmentService.update(environment);
    }

}
