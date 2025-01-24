package com.xxl.conf.core.factory;

import com.xxl.conf.core.data.XxlConfLocalCacheHelper;
import com.xxl.conf.core.data.XxlConfRemoteHelper;
import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.listener.XxlConfListenerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XxlConf Base Factory
 *
 * @author xuxueli 2015-9-12 19:42:49
 */
public class XxlConfFactory {
	private static final Logger logger = LoggerFactory.getLogger(XxlConfFactory.class);

	// ---------------------- instance ----------------------
	private static XxlConfFactory xxlConfFactory;
	public static XxlConfFactory getInstance() {
		if (xxlConfFactory == null) {
			throw new XxlConfException("xxl-conf factory not init.");
		}
		return xxlConfFactory;
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

	public XxlConfFactory(String appname, String env, String address, String accesstoken) {
		this.appname = appname;
		this.env = env;
		this.address = address;
		this.accesstoken = accesstoken;

		// instance
		this.xxlConfFactory = this;
	}

	/**
	 * addressList
	 */
	private List<String> addressList = new ArrayList<>();

	public List<String> getAddressList() {
		return addressList;
	}

	/**
	 * valid config
	 */
	private void validConfig(){
		// valid
		if (appname==null || appname.trim().length()==0) {
			throw new XxlConfException("xxl-conf appname can not be empty");
		}
		if (env==null || env.trim().length()==0) {
			throw new XxlConfException("xxl-conf env can not be empty");
		}
		if (address==null || address.trim().length()==0) {
			throw new XxlConfException("xxl-conf env can not be empty");
		}

		// address
		if (!address.contains(",")) {
			addressList.add(address);
		} else {
			addressList.addAll(
					Arrays.stream(address.split(","))
							.filter(item -> !item.trim().isEmpty())
							.collect(Collectors.toList()));
		}

	}

	// ---------------------- start / stop ----------------------

	private XxlConfRemoteHelper remoteHelper;
	private XxlConfLocalCacheHelper localCacheHelper;
	private XxlConfListenerRepository listenerRepository;

	public XxlConfRemoteHelper getRemoteHelper() {
		return remoteHelper;
	}
	public XxlConfListenerRepository getListenerRepository() {
		return listenerRepository;
	}
	public XxlConfLocalCacheHelper getLocalCacheHelper() {
		return localCacheHelper;
	}

	/**
	 * start
	 */
	public void start() {
        try {
			// valid
			validConfig();

			// init
			listenerRepository = new XxlConfListenerRepository(this);
			remoteHelper = new XxlConfRemoteHelper(this);
			localCacheHelper = new XxlConfLocalCacheHelper(this);

			// start (+thread, cycle refresh + monitor, notify change-data)
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
			localCacheHelper.stop();
			logger.info(">>>>>>>>>>> xxl-conf stopped.");
        } catch (Exception e) {
			logger.info(">>>>>>>>>>> xxl-conf stop error:{}", e.getMessage(), e);
        }
    }

}
