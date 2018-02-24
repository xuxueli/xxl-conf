package com.xxl.conf.sample.demo;

import com.xxl.conf.core.annotation.XxlConf;
import org.springframework.stereotype.Component;

/**
 *  测试示例（可删除）
 *
 *  @author xuxueli
 */
@Component
public class DemoConf {

	@XxlConf("default.key02")
	public String paramByAnno;

	public String paramByXml = "XML方式配置，请前往 (xxl-conf-sample-spring) 项目参考查看，springboot项目不推荐采用该方式";

	public void setParamByXml(String paramByXml) {
		this.paramByXml = paramByXml;
	}

}
