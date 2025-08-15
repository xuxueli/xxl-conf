package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.EnvironmentService;
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
    @Permission(Consts.ADMIN_PERMISSION)
    public String index(Model model) {
        return "biz/environment";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
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
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<Environment> load(int id){
        return environmentService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> insert(Environment environment){
        return environmentService.insert(environment);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return environmentService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> update(Environment environment){
        return environmentService.update(environment);
    }

}
