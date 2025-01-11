package com.xxl.conf.admin.web.interceptor;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.constant.enums.RoleEnum;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.dto.ResourceDTO;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.conf.admin.service.impl.LoginService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.exception.BizException;
import com.xxl.tool.freemarker.FreemarkerTool;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
			modelAndView.addObject("I18nUtil", FreemarkerTool.generateStaticModel(I18nUtil.class.getName()));

			// default menu
			List<ResourceDTO> resourceDTOList = Arrays.asList(
					new ResourceDTO(1, 0, "首页",0, "", "/index", "fa fa-home", 1, 0),
					new ResourceDTO(2, 0, "注册中心",0, "", "/instance", " fa-cubes", 2, 0),
					new ResourceDTO(3, 0, "应用管理",0, "ADMIN", "/application", " fa-cloud", 3, 0),
					new ResourceDTO(4, 0, "环境管理",0, "ADMIN", "/environment", "fa-cog", 4, 0),
					new ResourceDTO(5, 0, "鉴权管理",0, "ADMIN", "/accesstoken", "fa-key", 5, 0),
					new ResourceDTO(6, 0, "用户管理",0, "ADMIN", "/user", "fa-users", 6, 0),
					new ResourceDTO(7, 0, "帮助中心",0, "", "/help", "fa-book", 7, 0)
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

	}

}
