package com.xxl.conf.core.annotation;

import java.lang.annotation.*;

/**
 * xxl conf annotaion
 *
 * @author xuxueli 2018-02-04 00:34:30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XxlConf {

    /**
     * conf appname; if empty, the appname of current service will be used
     *
     * @return
     */
    String appname() default "";

    /**
     * conf key
     *
     * @return
     */
    String value();

    /**
     * conf default value
     *
     * @return
     */
    String defaultValue() default "";

    /**
     *  whether you need a callback refresh, when the value changes.
     *
     * @return
     */
    boolean callback() default true;
}