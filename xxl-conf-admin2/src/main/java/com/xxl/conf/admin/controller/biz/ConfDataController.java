package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.ConfDataService;
import com.xxl.conf.admin.service.EnvironmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import javax.annotation.Resource;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.ResponseBuilder;

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
    private EnvironmentService environmentService;
    @Resource
    private ApplicationService applicationService;

    /**
    * 页面
    */
    @RequestMapping
    @Permission
    public String index(Model model) {

        // data
        Response<List<Environment>> environmentListRet = environmentService.findAll();
        model.addAttribute("environmentList", environmentListRet.getData());

        Response<List<Application>> applicationListRet = applicationService.findAll();
        model.addAttribute("applicationList", applicationListRet.getData());

        return "biz/confdata";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @Permission
    public Response<PageModel<ConfData>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                  @RequestParam(required = false, defaultValue = "10") int pagesize) {
        PageModel<ConfData> pageModel = confDataService.pageList(offset, pagesize);
        return new ResponseBuilder<PageModel<ConfData>>().success(pageModel).build();
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @Permission
    public Response<ConfData> load(int id){
        return confDataService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @Permission
    public Response<String> insert(ConfData confData){
        return confDataService.insert(confData);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @Permission
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return confDataService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @Permission
    public Response<String> update(ConfData confData){
        return confDataService.update(confData);
    }

}
