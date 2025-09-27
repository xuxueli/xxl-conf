package com.xxl.conf.admin.controller.base;

import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.model.dto.XxlBootResourceDTO;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

	public static final String XXL_CONF_CURRENT_ENV = "XXL_CONF_CURRENT_ENV";

	@Resource
	private EnvironmentService environmentService;

	@RequestMapping("/")
	@XxlSso
	public String index(HttpServletRequest request, Model model) {

		// menu resource
		List<XxlBootResourceDTO> resourceList = findResourceList(request);
		model.addAttribute("resourceList", resourceList);

		// environment
		Response<List<Environment>> environmentListRet = environmentService.findAll();
		model.addAttribute("environmentList", environmentListRet.getData());

		// current env
		String currentEnv = "";
		if (CollectionTool.isNotEmpty(environmentListRet.getData())) {
			String currentEnvCookie = CookieTool.getValue(request, XXL_CONF_CURRENT_ENV);
			currentEnv = environmentListRet.getData().stream().map(Environment::getEnv).toList().contains(currentEnvCookie)
					?currentEnvCookie
					:environmentListRet.getData().get(0).getEnv();
		}
		model.addAttribute(XXL_CONF_CURRENT_ENV, currentEnv);

		return "base/index";
	}

	/**
	 * fill menu data
	 */
	private List<XxlBootResourceDTO> findResourceList(HttpServletRequest request){
		// login check
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		// init menu-list
		List<XxlBootResourceDTO> resourceDTOList = Arrays.asList(
				new XxlBootResourceDTO(1, 0, "首页",1, "", "/dashboard", "fa fa-home", 1, 0, null),
				new XxlBootResourceDTO(2, 0, "配置中心",1, "", "/confdata", " fa-database", 2, 0, null),
				// TODO，配置管理（配置类型；int、long、boolean、json、text），历史版本（记录，类型；diff、回滚；）
				new XxlBootResourceDTO(3, 0, "注册中心",1, "", "/instance", " fa-cubes", 3, 0, null),
				new XxlBootResourceDTO(4, 0, "服务管理",1, "ADMIN", "/application", " fa-cloud", 4, 0,null),
				new XxlBootResourceDTO(5, 0, "环境管理",1, "ADMIN", "/environment", "fa-server", 5, 0, null),
				new XxlBootResourceDTO(6, 0, "系统管理",0, "ADMIN", "/system", "fa-cog", 6, 0, Arrays.asList(
						new XxlBootResourceDTO(7, 6, "AccessToken",1, "ADMIN", "/accesstoken", "fa-key", 7, 0, null),
						new XxlBootResourceDTO(8, 6, "用户管理",1, "ADMIN", "/user", "fa-users", 8, 0, null)
				)),
				new XxlBootResourceDTO(9, 0, "帮助中心",1, "", "/help", "fa-book", 9, 0, null)
		);
		// filter by role
		if (!XxlSsoHelper.hasRole(loginInfoResponse.getData(), RoleEnum.ADMIN.getValue()).isSuccess()) {
			resourceDTOList = resourceDTOList.stream()
					.filter(resourceDTO -> StringTool.isBlank(resourceDTO.getPermission() ))	// normal user had no permission
					.collect(Collectors.toList());
		}
		resourceDTOList.stream().sorted(Comparator.comparing(XxlBootResourceDTO::getOrder)).toList();
		return resourceDTOList;
	}

	@RequestMapping("/dashboard")
	@XxlSso
	public String dashboard(HttpServletRequest request, Model model) {
		return "base/dashboard";
	}

	@RequestMapping("/help")
	@XxlSso
	public String help() {
		return "base/help";
	}

	@RequestMapping(value = "/errorpage")
	@XxlSso(login = false)
	public ModelAndView errorPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) {

		String exceptionMsg = "HTTP Status Code: "+response.getStatus();

		mv.addObject("exceptionMsg", exceptionMsg);
		mv.setViewName("common/common.errorpage");
		return mv;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
