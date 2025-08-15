package com.xxl.conf.admin.controller.login;

import com.xxl.conf.admin.constant.enums.UserStatuEnum;
import com.xxl.conf.admin.mapper.UserMapper;
import com.xxl.conf.admin.model.entity.User;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

	@Resource
	private UserMapper userMapper;

	@RequestMapping("/login")
	@XxlSso(login = false)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {

		// xxl-sso, logincheck
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithCookie(request, response);

		if (loginInfoResponse.isSuccess()) {
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}
		return new ModelAndView("login");
	}

	@RequestMapping(value="/doLogin", method=RequestMethod.POST)
	@ResponseBody
	@XxlSso(login = false)
	public Response<String> doLogin(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){

		// param
		boolean ifRem = StringTool.isNotBlank(ifRemember) && "on".equals(ifRemember);
		if (StringTool.isBlank(userName) || StringTool.isBlank(password)){
			return Response.ofFail( I18nUtil.getString("login_param_empty") );
		}

		// valid user, empty、status、passowrd
		User user = userMapper.loadByUserName(userName);
		if (user == null) {
			return Response.ofFail( I18nUtil.getString("login_param_unvalid") );
		}
		if (user.getStatus() != UserStatuEnum.NORMAL.getValue()) {
			return Response.ofFail( I18nUtil.getString("login_status_invalid") );
		}
		String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
		if (!passwordMd5.equals(user.getPassword())) {
			return Response.ofFail( I18nUtil.getString("login_param_unvalid") );
		}

		// xxl-sso, do login
		LoginInfo loginInfo = new LoginInfo(String.valueOf(user.getId()), UUIDTool.getSimpleUUID());
		return XxlSsoHelper.loginWithCookie(loginInfo, response, ifRem);
	}

	@RequestMapping(value="/logout", method=RequestMethod.POST)
	@ResponseBody
	@XxlSso(login = false)
	public Response<String> logout(HttpServletRequest request, HttpServletResponse response){
		// xxl-sso, do logout
		return XxlSsoHelper.logoutWithCookie(request, response);
	}

}
