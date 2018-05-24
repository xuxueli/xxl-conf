package com.xxl.conf.sample.demo;

import com.xxl.conf.core.annotation.XxlConf;

/**
 *  测试示例（可删除）
 *
 *  @author xuxueli
 */
public class DemoConf {

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

}
