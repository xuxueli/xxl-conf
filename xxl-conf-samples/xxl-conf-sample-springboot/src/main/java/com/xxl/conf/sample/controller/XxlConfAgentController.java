package com.xxl.conf.sample.controller;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.listener.XxlConfListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * XxlConf Agent, support multilingual applications.
 * （配置中心Agent服务，实现配置数据多语言支持；）
 *
 * Java应用可通过 "Client方式" 方便的获取配置中心的数据；非Java语言应用，可通过该 "配置中心Agent服务" 获取配置中心配置；从而实现配置数据多语言支持；
 * "配置中心Agent服务" 本质上是一个获取配置中心中配置数据的Http接口。
 *
 * 	特点：
 * 		1、跨语言：支持通过Http方式获取多个配置数据，无语言限制；
 * 		2、动态更新：配置变更时，Agent将会实时感知并更新Local Cache中配置数据，保证实时性；第三方应用内部配置实时性，可通过 "周期性轮训" 或者 "long-polling" 实现；
 * 		3、高性能：得益于 XxlConf 底层实现的 Local Cache，因此该 Agent 服务性能非常高；单机可承担大量配置请求；
 * 		4、集群部署：XxlConf Agent 支持集群部署，提高配置服务的可用性；
 *
 * @author xuxueli 2018-05-29 20:43:17
 */
@Controller
@RequestMapping("/xxlconfagent")
public class XxlConfAgentController {
	private static Logger logger = LoggerFactory.getLogger(XxlConfAgentController.class);

	private static String ACCESS_TOKEN = "token";

	/**
	 * 获取配置信息
	 *
	 * @param confKeys	配置Key，多个逗号分隔
	 * @param async		trne=同步请求响应；false（默认）=异步请求响应，配置变更时返回；
	 *
	 * @return			配置数据，Map格式
	 */
	@RequestMapping("")
	@ResponseBody
	public Object index(String accessToken,
						String confKeys,
						String async){

		// access token valid
		if (ACCESS_TOKEN!=null && ACCESS_TOKEN.trim().length()>0) {
			if (!ACCESS_TOKEN.equals(accessToken)) {
				return new ReturnT<String>(ReturnT.FAIL.getCode(), "request fail, [accessToken] unvalid.");
			}
		}

		// conf key list
		List<String> confKeyList = null;
		if (confKeys!=null && confKeys.trim().length()>0) {
			confKeyList = new ArrayList<>();
			for (String confKey: confKeys.split(",")) {
				if (confKey!=null && confKey.trim().length()>0) {
					confKeyList.add(confKey.trim());
				}
			}
		}

		// valid
		if (confKeyList==null || confKeyList.size()==0) {
			return new ReturnT<String>(ReturnT.FAIL.getCode(), "request fail, [confKeyList] can not be null.");
		}

		if (Boolean.valueOf(async)) {
			// async, listen conf update by long-polling

			DeferredResult<ReturnT<Map<String, String>>> deferredResult = new DeferredResult<ReturnT<Map<String, String>>>();

			for (String confKey: confKeyList) {
				List<DeferredResult<ReturnT<Map<String, String>>>> clientListenerList = clientListenerMap.get(confKey);
				if (clientListenerList == null) {
					clientListenerList = new ArrayList<>();
					clientListenerMap.put(confKey, clientListenerList);
				}
				clientListenerList.add(deferredResult);
			}
			return deferredResult;
		} else {
			// sync, query conf info

			Map<String, String> confMap = new HashMap<>();
			for (String confKey: confKeyList) {
				String confValue = XxlConfClient.get(confKey.trim(), null);
				if (confValue!=null) {
					confMap.put(confKey, confValue);
				}
			}
			return new ReturnT<Map<String, String>>(confMap);
		}

	}


	// ---------------------- listener conf ----------------------
	private static ConcurrentHashMap<String, List<DeferredResult<ReturnT<Map<String, String>>>>> clientListenerMap = new ConcurrentHashMap<>();

	@PostConstruct
	public void initAgentListener(){
		XxlConfClient.addListener(null, new XxlConfListener() {
			@Override
			public void onChange(String key, String value) throws Exception {

				List<DeferredResult<ReturnT<Map<String, String>>>> clientListenerList = clientListenerMap.get(key);
				if (clientListenerList != null) {
					for (DeferredResult<ReturnT<Map<String, String>>> deferredResult: clientListenerList) {

						if (deferredResult.isSetOrExpired()) {
							continue;
						}

						Map<String, String> confMap = new HashMap<>();
						confMap.put(key, value);

						deferredResult.setResult(new ReturnT<Map<String, String>>(confMap));
					}
				}
			}
		});
	}


	// ---------------------- return vo ----------------------

	public static class ReturnT<T> {
		public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
		public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);

		private int code;
		private String msg;
		private T content;

		public ReturnT(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}
		public ReturnT(T content) {
			this.code = 200;
			this.content = content;
		}

		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public T getContent() {
			return content;
		}
		public void setContent(T content) {
			this.content = content;
		}

	}


}
