package com.xxl.conf.sample.core.constant;

import com.xxl.conf.core.annotation.XxlConf;

/**
 *  测试示例（可删除）
 *
 *  @author xuxueli
 */
public class DemoConf {

	@XxlConf("default.key02")
	public String paramByAnno;

	public String paramByXml;

	public void setParamByXml(String paramByXml) {
		this.paramByXml = paramByXml;
	}

}
