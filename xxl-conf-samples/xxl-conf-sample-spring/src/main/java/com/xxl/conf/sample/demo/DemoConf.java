package com.xxl.conf.sample.demo;

import com.xxl.conf.core.annotation.XxlConf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *  测试示例（可删除）
 *
 *  @author xuxueli
 */
@Component
public class DemoConf {


	// ------------- XXL-CONF，配置数据从配置中心获取 -------------

	/**
	 * XXL-CONF：@XxlConf 注解方式
	 */
	@XxlConf("default.key02")
	public String paramByAnno;


	/**
	 * XXL-CONF：$XxlConf{default.key03} XML占位符方式
	 */
	public String paramByXml;

	public void setParamByXml(String paramByXml) {
		this.paramByXml = paramByXml;
	}


	// ------------- 原生方式，配置数据从本地配置文件获取 -------------

	/**
	 * 原生方式：@Value 注解方式
	 */
	@Value("${local.anno}")
	public String localByAnno;


	/**
	 * 原生方式：XML占位符方式
	 */
	public String localByXml;

	public void setLocalByXml(String localByXml) {
		this.localByXml = localByXml;
	}

}
