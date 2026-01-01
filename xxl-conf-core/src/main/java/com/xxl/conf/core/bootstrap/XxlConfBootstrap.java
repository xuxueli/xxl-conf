package com.xxl.conf.core.bootstrap;

import com.xxl.conf.core.confdata.XxlConfLocalCacheHelper;
import com.xxl.conf.core.constant.Consts;
import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.listener.XxlConfListenerHelper;
import com.xxl.conf.core.openapi.confdata.ConfDataService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.error.BizException;
import com.xxl.tool.http.HttpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * XxlConf Base Bootstrap
 *
 * @author xuxueli 2015-9-12 19:42:49
 */
public class XxlConfBootstrap {
	private static final Logger logger = LoggerFactory.getLogger(XxlConfBootstrap.class);

	// ---------------------- instance ----------------------
	private static XxlConfBootstrap xxlConfBootstrap;
	public static XxlConfBootstrap getInstance() {
		if (xxlConfBootstrap == null) {
			throw new XxlConfException("xxl-conf factory not init.");
		}
		return xxlConfBootstrap;
	}

	// ---------------------- base conf ----------------------

	private String appname;
	private String env;
	private String address;
	private String accesstoken;

	public String getAppname() {
		return appname;
	}
	public String getEnv() {
		return env;
	}
	public String getAddress() {
		return address;
	}
	public String getAccesstoken() {
		return accesstoken;
	}

	public XxlConfBootstrap(String appname, String env, String address, String accesstoken) {
		this.appname = appname;
		this.env = env;
		this.address = address;
		this.accesstoken = accesstoken;

		// instance
		XxlConfBootstrap.xxlConfBootstrap = this;
	}

	// ---------------------- valid config ----------------------

	/**
	 * client list
	 */
	private final List<ConfDataService> clientList = new ArrayList<>();
	private final List<ConfDataService> monitorClientList = new ArrayList<>();

	/**
	 * build client
	 */
	private void buildClient(){
		// valid
		if (StringTool.isBlank(address)) {
			throw new XxlConfException("xxl-conf address can not be empty");
		}
		if (StringTool.isBlank(accesstoken)) {
			throw new BizException("xxl-conf accesstoken can not be empty.");
		}
		if (StringTool.isBlank(appname)) {
			throw new XxlConfException("xxl-conf appname can not be empty");
		}
		if (StringTool.isBlank(env)) {
			throw new XxlConfException("xxl-conf env can not be empty");
		}

		// broker client
		List<String> addressList = Arrays.stream(address.split(",")).filter(StringTool::isNotBlank).toList();
		for (String url : addressList) {
			String finalUrl = url + "/openapi/confdata";
			clientList.add(HttpTool.createClient()
					.url(finalUrl)
					.timeout(3 * 1000)
					.header(Consts.XXL_CONF_ACCESS_TOKEN, accesstoken)
					.proxy(ConfDataService.class));
			monitorClientList.add(HttpTool.createClient()
					.url(finalUrl)
					.timeout(60 * 1000)
					.header(Consts.XXL_CONF_ACCESS_TOKEN, accesstoken)
					.proxy(ConfDataService.class));
		}
	}

	/**
	 * load client
	 */
	public ConfDataService loadClient(){
		return clientList.get(ThreadLocalRandom.current().nextInt(clientList.size()));
	}

	/**
	 * load monitor client
	 */
	public ConfDataService loadMonitorClient(){
		return monitorClientList.get(ThreadLocalRandom.current().nextInt(monitorClientList.size()));
	}

	// ---------------------- start / stop ----------------------

	private XxlConfLocalCacheHelper localCacheHelper;
	private XxlConfListenerHelper listenerHelper;

	public XxlConfListenerHelper getListenerHelper() {
		return listenerHelper;
	}
	public XxlConfLocalCacheHelper getLocalCacheHelper() {
		return localCacheHelper;
	}

	/**
	 * start
	 */
	public void start() {
        try {
			// build broker client
			buildClient();

			// 1、XxlConfListenerHelper
			listenerHelper = new XxlConfListenerHelper(this);
			listenerHelper.start();

			// 2、XxlConfLocalCacheHelper (+thread, cycle refresh + monitor, notify change-data)
			localCacheHelper = new XxlConfLocalCacheHelper(this);
			localCacheHelper.start();

			logger.info(">>>>>>>>>>> xxl-conf started.");
        } catch (Exception e) {
			logger.info(">>>>>>>>>>> xxl-conf start error:{}", e.getMessage(), e);
        }
    }

	/**
	 * stop
	 */
	public void stop() {
        try {

			// 1、XxlConfListenerHelper
			if (listenerHelper != null) {
				listenerHelper.stop();
			}

			// 2、XxlConfLocalCacheHelper
			if (localCacheHelper != null) {
				localCacheHelper.stop();
			}
			logger.info(">>>>>>>>>>> xxl-conf stopped.");
        } catch (Exception e) {
			logger.info(">>>>>>>>>>> xxl-conf stop error:{}", e.getMessage(), e);
        }
    }

}
