package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.model.dto.ConfDataLogDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.conf.admin.service.ConfDataLogService;
import com.xxl.conf.admin.service.ConfDataBizService;
import com.xxl.conf.admin.util.I18nUtil;
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

import static com.xxl.conf.admin.controller.biz.ConfDataController.hasPermissionApplication;

/**
* ConfData Controller
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Controller
@RequestMapping("/confdatalog")
public class ConfDataLogController {

    @Resource
    private ConfDataBizService confDataService;
    @Resource
    private ConfDataLogService confDataLogService;
    @Resource
    private ApplicationService applicationService;

    /**
    * 页面
    */
    @RequestMapping
    @XxlSso
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
        if (!hasPermissionApplication(request, applicationService, confData.getAppname())){
            throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
        }

        return "biz/confdatalog";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso
    public Response<PageModel<ConfDataLogDTO>> pageList(HttpServletRequest request,
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

        // valid permission of appname
        if (!hasPermissionApplication(request, applicationService, confData.getAppname())){
            return Response.ofFail(I18nUtil.getString("system_permission_limit"));
        }

        // page
        PageModel<ConfDataLogDTO> pageModel = confDataLogService.pageList(offset, pagesize, dataId);
        return Response.ofSuccess(pageModel);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso
    public Response<String> delete(@RequestParam("ids[]") List<Long> ids, HttpServletRequest request){
        return confDataLogService.delete(ids);
    }

    /**
     * 回滚
     */
    @RequestMapping("/rollback")
    @ResponseBody
    @XxlSso
    public Response<String> rollback(long dataLogId, HttpServletRequest request){
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr( request);
        String optUserName = loginInfoResponse.getData().getUserName();
        return confDataLogService.rollback(optUserName, dataLogId);
    }

}
