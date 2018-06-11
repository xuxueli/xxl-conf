package com.xxl.conf.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xuxueli 2018-06-08 20:55:06
 */
@EnableAutoConfiguration
@SpringBootApplication
public class XxlConfApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlConfApplication.class, args);
	}

}