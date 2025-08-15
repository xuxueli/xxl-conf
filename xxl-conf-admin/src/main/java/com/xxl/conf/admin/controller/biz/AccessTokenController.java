package com.xxl.conf.admin.controller.biz;

import com.xxl.conf.admin.constant.consts.Consts;
import com.xxl.conf.admin.constant.enums.AccessTokenStatuEnum;
import com.xxl.conf.admin.model.dto.AccessTokenDTO;
import com.xxl.conf.admin.model.entity.AccessToken;
import com.xxl.conf.admin.service.AccessTokenService;
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
* AccessToken Controller
*
* Created by xuxueli on '2024-12-08 16:22:29'.
*/
@Controller
@RequestMapping("/accesstoken")
public class AccessTokenController {

    @Resource
    private AccessTokenService accessTokenService;

    /**
    * 页面
    */
    @RequestMapping
    @XxlSso(role = Consts.ADMIN_ROLE)
    public String index(Model model) {

        model.addAttribute("AccessTokenStatuEnum", AccessTokenStatuEnum.values());

        return "biz/accesstoken";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<PageModel<AccessTokenDTO>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                     @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                     @RequestParam(required = false) String accessToken) {
        PageModel<AccessTokenDTO> pageModel = accessTokenService.pageList(offset, pagesize, accessToken);
        return Response.ofSuccess(pageModel);
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<AccessToken> load(int id){
        return accessTokenService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> insert(AccessToken accessToken){
        return accessTokenService.insert(accessToken);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return accessTokenService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(AccessToken accessToken){
        return accessTokenService.update(accessToken);
    }

}
