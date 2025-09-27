package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.model.dto.InstanceDTO;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.conf.admin.service.InstanceService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

import static com.xxl.conf.admin.controller.biz.ConfDataController.hasPermissionApplication;

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
    @XxlSso
    public String index(Model model) {

        // enum
        model.addAttribute("InstanceRegisterModelEnum", InstanceRegisterModelEnum.values());

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
    @XxlSso
    public Response<PageModel<InstanceDTO>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                     @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                     String appname,
                                                     String env) {
        PageModel<InstanceDTO> pageModel = instanceService.pageList(offset, pagesize, appname, env);
        return Response.ofSuccess(pageModel);
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @XxlSso
    public Response<Instance> load(int id){
        return instanceService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> insert(Instance instance, HttpServletRequest request){

        // valid application
        if (!hasPermissionApplication(request, applicationService, instance.getAppname())){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        return instanceService.insert(instance);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids, HttpServletRequest request){
        return instanceService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(Instance instance, HttpServletRequest request){

        // valid application
        if (!hasPermissionApplication(request, applicationService, instance.getAppname())){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        return instanceService.update(instance);
    }

}
