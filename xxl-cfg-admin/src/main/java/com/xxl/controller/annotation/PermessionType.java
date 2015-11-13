package com.xxl.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限类型
 * @author xuxueli
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermessionType {
	
	/**
	 * 登陆拦截
	 */
	boolean loginState() default true;
	
}
