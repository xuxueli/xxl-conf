package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.model.dto.InstanceDTO;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.conf.admin.service.InstanceService;
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
* Instance Controller
*
* Created by xuxueli on '2024-12-07 21:44:18'.
*/
@Controller
@RequestMapping("/instance")
public class InstanceController {

    @Resource
    private InstanceService instanceService;
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

        // enum
        model.addAttribute("InstanceRegisterModelEnum", InstanceRegisterModelEnum.values());

        // data
        Response<List<Environment>> environmentListRet = environmentService.findAll();
        model.addAttribute("environmentList", environmentListRet.getData());

        Response<List<Application>> applicationListRet = applicationService.findAll();
        model.addAttribute("applicationList", applicationListRet.getData());

        return "biz/instance";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @Permission
    public Response<PageModel<InstanceDTO>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                     @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                     @RequestParam() String appname,
                                                     @RequestParam() String env) {
        PageModel<InstanceDTO> pageModel = instanceService.pageList(offset, pagesize, appname, env);
        return new ResponseBuilder<PageModel<InstanceDTO>>().success(pageModel).build();
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @Permission
    public Response<Instance> load(int id){
        return instanceService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> insert(Instance instance){
        return instanceService.insert(instance);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return instanceService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> update(Instance instance){
        return instanceService.update(instance);
    }

}
