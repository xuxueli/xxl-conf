package com.xxl.conf.admin.web.interceptor;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.dto.ResourceDTO;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.conf.admin.service.impl.LoginService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.exception.BizException;
import com.xxl.tool.freemarker.FtlTool;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限拦截
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {

	@Resource
	private LoginService loginService;
	@Resource
	private EnvironmentService environmentService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// handler method
		if (!(handler instanceof HandlerMethod)) {
			return true;	// proceed with the next interceptor
		}
		HandlerMethod method = (HandlerMethod)handler;

		// parse permission config
		Permission permission = method.getMethodAnnotation(Permission.class);
		if (permission == null) {
			throw new BizException("权限拦截，请求路径权限未设置");
		}
		if (!permission.login()) {
			return true;	// not need login ,not valid permission, pass
		}

		// valid login
		LoginUserDTO loginUser = loginService.checkLogin(request, response);
		if (loginUser == null) {
			response.setStatus(302);
			response.setHeader("location", request.getContextPath() + "/toLogin");
			return false;
		}
		request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);

		// valid permission
		if (StringTool.isNotBlank(permission.value())) {
			// need permisson
			RoleEnum roleEnum = RoleEnum.matchByValue(loginUser.getRole());
			if (roleEnum != null && roleEnum.getPermissions().contains(permission.value())) {
				return true;
			} else {
				throw new BizException(I18nUtil.getString("system_permission_limit"));
			}
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			// i18n, static method
			modelAndView.addObject("I18nUtil", FtlTool.generateStaticModel(I18nUtil.class.getName()));

			// fill menu data
			fillMenuData(request, modelAndView);

			// fild env data
			fillEnvData(request, modelAndView);
		}
	}

	/**
	 * fill menu data
	 *
	 * @param request
	 * @param modelAndView
	 */
	private void fillMenuData(HttpServletRequest request, ModelAndView modelAndView){
		// fill menu-list
		List<ResourceDTO> resourceDTOList = Arrays.asList(
				new ResourceDTO(1, 0, "首页",1, "", "/index", "fa fa-home", 1, 0, null),
				new ResourceDTO(2, 0, "配置中心",1, "", "/confdata", " fa-database", 2, 0, null),
				// TODO，配置管理（配置类型；int、long、boolean、json、text），历史版本（记录，类型；diff、回滚；）

				new ResourceDTO(3, 0, "注册中心",1, "", "/instance", " fa-cubes", 3, 0, null),
				new ResourceDTO(4, 0, "服务管理",1, "ADMIN", "/application", " fa-cloud", 4, 0,null),
				new ResourceDTO(5, 0, "环境管理",1, "ADMIN", "/environment", "fa-server", 5, 0, null),
				new ResourceDTO(6, 0, "系统管理",0, "ADMIN", "/system", "fa-cog", 6, 0, Arrays.asList(
						new ResourceDTO(7, 6, "AccessToken",1, "ADMIN", "/accesstoken", "fa-key", 7, 0, null),
						new ResourceDTO(8, 6, "用户管理",1, "ADMIN", "/user", "fa-users", 8, 0, null)
				)),
				new ResourceDTO(9, 0, "帮助中心",1, "", "/help", "fa-book", 9, 0, null)
		);
		// valid
		if (!loginService.isAdmin(request)) {
			resourceDTOList = resourceDTOList.stream()
					.filter(resourceDTO -> StringTool.isBlank(resourceDTO.getPermission() ))	// normal user had no permission
					.collect(Collectors.toList());
		}
		resourceDTOList.stream().sorted(Comparator.comparing(ResourceDTO::getOrder)).collect(Collectors.toList());

		modelAndView.addObject("resourceList", resourceDTOList);
	}

	public static final String XXL_CONF_CURRENT_ENV = "XXL_CONF_CURRENT_ENV";

	/**
	 * fill env data
	 *
	 * @param request
	 * @param modelAndView
	 */
	private void fillEnvData(HttpServletRequest request, ModelAndView modelAndView){


		// fill env-list
		Response<List<Environment>> environmentListRet = environmentService.findAll();
		List<Environment> environmentList = environmentListRet.getData();
		modelAndView.addObject("environmentList", environmentList);

		// current env
		String currentEnv = "";
		if (environmentList!=null && !environmentList.isEmpty()) {
			String currentEnvCookie = CookieTool.getValue(request, XXL_CONF_CURRENT_ENV);
			currentEnv = environmentList.stream().map(Environment::getEnv).collect(Collectors.toList()).contains(currentEnvCookie)
					?currentEnvCookie
					:environmentList.get(0).getEnv();
		}
		modelAndView.addObject(XXL_CONF_CURRENT_ENV, currentEnv);
	}

}
