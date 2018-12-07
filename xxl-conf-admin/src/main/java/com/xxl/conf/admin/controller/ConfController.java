package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.model.XxlConfProject;
import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.JacksonUtil;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfProjectDao;
import com.xxl.conf.admin.service.IXxlConfNodeService;
import com.xxl.conf.admin.service.impl.LoginService;
import com.xxl.conf.core.model.XxlConfParamVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.xxl.conf.admin.controller.interceptor.EnvInterceptor.CURRENT_ENV;

/**
 * 配置管理
 *
 * @author xuxueli
 */
@Controller
@RequestMapping("/conf")
public class ConfController {

	@Resource
	private XxlConfProjectDao xxlConfProjectDao;
	@Resource
	private IXxlConfNodeService xxlConfNodeService;

	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, String appname){

		List<XxlConfProject> list = xxlConfProjectDao.findAll();
		if (list==null || list.size()==0) {
			throw new RuntimeException("系统异常，无可用项目");
		}

		XxlConfProject project = list.get(0);
		for (XxlConfProject item: list) {
			if (item.getAppname().equals(appname)) {
				project = item;
			}
		}

		boolean ifHasProjectPermission = xxlConfNodeService.ifHasProjectPermission(
				(XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY),
				(String) request.getAttribute(CURRENT_ENV),
				project.getAppname());

		model.addAttribute("ProjectList", list);
		model.addAttribute("project", project);
		model.addAttribute("ifHasProjectPermission", ifHasProjectPermission);

		return "conf/conf.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										String appname,
										String key) {

		XxlConfUser xxlConfUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		String loginEnv = (String) request.getAttribute(CURRENT_ENV);

		return xxlConfNodeService.pageList(start, length, appname, key, xxlConfUser, loginEnv);
	}

	/**
	 * get
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public ReturnT<String> delete(HttpServletRequest request, String key){

		XxlConfUser xxlConfUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		String loginEnv = (String) request.getAttribute(CURRENT_ENV);

		return xxlConfNodeService.delete(key, xxlConfUser, loginEnv);
	}

	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(HttpServletRequest request, XxlConfNode xxlConfNode){

		XxlConfUser xxlConfUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		String loginEnv = (String) request.getAttribute(CURRENT_ENV);

		// fill env
		xxlConfNode.setEnv(loginEnv);

		return xxlConfNodeService.add(xxlConfNode, xxlConfUser, loginEnv);
	}
	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(HttpServletRequest request, XxlConfNode xxlConfNode){

		XxlConfUser xxlConfUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		String loginEnv = (String) request.getAttribute(CURRENT_ENV);

		// fill env
		xxlConfNode.setEnv(loginEnv);

		return xxlConfNodeService.update(xxlConfNode, xxlConfUser, loginEnv);
	}

	/*@RequestMapping("/syncConf")
	@ResponseBody
	public ReturnT<String> syncConf(HttpServletRequest request,
										String appname) {

		XxlConfUser xxlConfUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		String loginEnv = (String) request.getAttribute(CURRENT_ENV);

		return xxlConfNodeService.syncConf(appname, xxlConfUser, loginEnv);
	}*/


	// ---------------------- rest api ----------------------

    @Value("${xxl.conf.access.token}")
    private String accessToken;


	/**
	 * 配置查询 API
	 *
	 * 说明：查询配置数据；
	 *
	 * ------
	 * 地址格式：{配置中心跟地址}/find
	 *
	 * 请求参数说明：
	 *  1、accessToken：请求令牌；
	 *  2、env：环境标识
	 *  3、keys：配置Key列表
	 *
	 * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
	 *
	 *     {
	 *         "accessToken" : "xx",
	 *         "env" : "xx",
	 *         "keys" : [
	 *             "key01",
	 *             "key02"
	 *         ]
	 *     }
	 *
	 * @param data
	 * @return
	 */
	@RequestMapping("/find")
	@ResponseBody
	@PermessionLimit(limit = false)
	public ReturnT<Map<String, String>> find(@RequestBody(required = false) String data){

		// parse data
		XxlConfParamVO confParamVO = null;
		try {
			confParamVO = (XxlConfParamVO) JacksonUtil.readValue(data, XxlConfParamVO.class);
		} catch (Exception e) { }

		// parse param
		String accessToken = null;
		String env = null;
		List<String> keys = null;
		if (confParamVO != null) {
			accessToken = confParamVO.getAccessToken();
			env = confParamVO.getEnv();
			keys = confParamVO.getKeys();
		}

		return xxlConfNodeService.find(accessToken, env, keys);
	}

	/**
	 * 配置监控 API
	 *
	 * 说明：long-polling 接口，主动阻塞一段时间（默认30s）；直至阻塞超时或配置信息变动时响应；
	 *
	 * ------
	 * 地址格式：{配置中心跟地址}/find
	 *
	 * 请求参数说明：
	 *  1、accessToken：请求令牌；
	 *  2、env：环境标识
	 *  3、keys：配置Key列表
	 *
	 * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
	 *
	 *     {
	 *         "accessToken" : "xx",
	 *         "env" : "xx",
	 *         "keys" : [
	 *             "key01",
	 *             "key02"
	 *         ]
	 *     }
	 *
	 * @param data
	 * @return
	 */
	@RequestMapping("/monitor")
	@ResponseBody
	@PermessionLimit(limit = false)
	public DeferredResult<ReturnT<String>> monitor(@RequestBody(required = false) String data){

		// parse data
		XxlConfParamVO confParamVO = null;
		try {
			confParamVO = (XxlConfParamVO) JacksonUtil.readValue(data, XxlConfParamVO.class);
		} catch (Exception e) { }

		// parse param
		String accessToken = null;
		String env = null;
		List<String> keys = null;
		if (confParamVO != null) {
			accessToken = confParamVO.getAccessToken();
			env = confParamVO.getEnv();
			keys = confParamVO.getKeys();
		}

		return xxlConfNodeService.monitor(accessToken, env, keys);
	}


}
