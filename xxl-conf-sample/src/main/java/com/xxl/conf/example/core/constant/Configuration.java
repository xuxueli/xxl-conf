package com.xxl.conf.example.core.constant;

import org.springframework.beans.factory.annotation.Value;

/**
 * 测试用,可删除
 */
public class Configuration {

	@Value("${default.key02}")
	public String paramByAnno;

	public String paramByXml;

	public void setParamByXml(String paramByXml) {
		this.paramByXml = paramByXml;
	}

}
