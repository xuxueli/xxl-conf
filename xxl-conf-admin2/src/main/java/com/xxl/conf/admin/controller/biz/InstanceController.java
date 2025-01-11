package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.model.dto.InstanceDTO;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.conf.admin.service.InstanceService;
import com.xxl.conf.admin.service.impl.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    @Resource
    private LoginService loginService;

    /**
    * 页面
    */
    @RequestMapping
    @Permission
    public String index(Model model) {

        // enum
        model.addAttribute("InstanceRegisterModelEnum", InstanceRegisterModelEnum.values());

        // env
        Response<List<Environment>> environmentListRet = environmentService.findAll();
        model.addAttribute("environmentList", environmentListRet.getData());

        // application
        List<Application> applicationList = applicationService.findAll().getData();
        model.addAttribute("applicationList", applicationList);

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
    public Response<String> insert(Instance instance, HttpServletRequest request){
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        return instanceService.insert(instance,loginUser, loginService.isAdmin(request));
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids, HttpServletRequest request){
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        return instanceService.delete(ids,loginUser, loginService.isAdmin(request));
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @Permission(Consts.ADMIN_PERMISSION)
    public Response<String> update(Instance instance, HttpServletRequest request){
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        return instanceService.update(instance,loginUser, loginService.isAdmin(request));
    }

}
