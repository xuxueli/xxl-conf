package com.xxl.conf.sample.controller;

import com.xxl.conf.core.XxlConfClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * XxlConf Agent, support multilingual applications.
 * （配置中心 Agent 服务，实现配置数据多语言支持；）
 *
 * Java应用可通过依赖Client端，方便的获取配置中心的数据；
 * 其他，非Java语言应用可通过该 Agent 的方式获取配置中心配置：可通过部署该 XxlConf Agent 应用，以 Http 接口方式获取配置中心中的配置；从而实现配置中心的多语言支持；
 *
 * 	优点：
 * 		1、跨语言：支持通过Http方式获取多个配置数据，无语言限制；
 * 		2、动态更新：配置变更时，Conf Agent 将会实时感知并更新Local Cache中配置数据，降低配置Agent服务的配置延迟；
 * 		3、高性能：得益于 XxlConf 底层实现的 Local Cache，因此该 Agent 服务性能非常高；单机可承担大量配置请求；
 * 		4、集群部署：XxlConf Agent 支持集群部署，提高配置服务的可用性；；
 *
 * @author xuxueli 2018-05-29 20:43:17
 */
@Controller
@RequestMapping("/confagent")
public class XxlConfAgentController {
	private static Logger logger = LoggerFactory.getLogger(XxlConfAgentController.class);

	/**
	 * 获取配置信息
	 *
	 * @param confKeys	配置Key，多个逗号分隔
	 * @return			配置数据，Map格式
	 */
	@RequestMapping("")
	@ResponseBody
	public Map<String, String> index(String confKeys){

		Map<String, String> confMap = new HashMap<>();

		if (confKeys!=null && confKeys.trim().length()>0) {
			for (String confKey: confKeys.split(",")) {
				if (confKey!=null && confKey.trim().length()>0) {
					String confValue = XxlConfClient.get(confKey.trim(), null);
					if (confValue!=null) {
						confMap.put(confKey, confValue);
					}
				}
			}
		}

		return confMap;
	}

}
