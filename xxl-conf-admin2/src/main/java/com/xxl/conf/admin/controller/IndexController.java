package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.service.impl.LoginService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {


	@Resource
	private LoginService loginService;


	@RequestMapping("/")
	@Permission
	public String defaultpage(Model model) {
		return "redirect:/index";
	}

	@RequestMapping("/index")
	@Permission
	public String index(HttpServletRequest request, Model model) {
		return "index";
	}

	@RequestMapping("/help")
	@Permission
	public String help() {


		return "help";
	}

	@RequestMapping("/toLogin")
	@Permission(login = false)
	public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response,ModelAndView modelAndView) {
		LoginUserDTO loginUserDTO = loginService.getLoginUser(request);
		if (loginUserDTO != null) {
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}
		return new ModelAndView("login");
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	@Permission(login=false)
	public Response<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
		boolean ifRem = StringTool.isNotBlank(ifRemember) && "on".equals(ifRemember);
		return loginService.login(response, userName, password, ifRem);
	}
	
	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@Permission(login=false)
	public Response<String> logout(HttpServletRequest request, HttpServletResponse response){
		return loginService.logout(request, response);
	}

	@RequestMapping(value = "/errorpage")
	@Permission(login = false)
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
