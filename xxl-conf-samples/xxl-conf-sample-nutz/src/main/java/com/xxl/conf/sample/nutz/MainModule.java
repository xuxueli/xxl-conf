package com.xxl.conf.sample.nutz;

import com.xxl.conf.sample.nutz.config.NutzSetup;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * nutz module
 *
 * @author xuxueli 2018-05-24
 */
@IocBy(type = ComboIocProvider.class,
        args = {"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
                "com.xxl.conf.sample.nutz"})
@Encoding(input = "utf-8", output = "utf-8")
@Modules(scanPackage = true)
@Localization("msg")
@Ok("json")
@Fail("json")
@SetupBy(NutzSetup.class)
public class MainModule {

}
