package com.xxl.conf.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Value注解，作用于字段并可实时感触数据变更
 * User: chenchen_839@126.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlValue {
    /**
     * 查询key
     * @return
     */
    String key();

    /**
     * 默认值
     * @return
     */
    String defaultValue() default "";

    /**
     * 是否需要实时感触数据变更，默认为true
     * @return
     */
    boolean callback() default true;
}
