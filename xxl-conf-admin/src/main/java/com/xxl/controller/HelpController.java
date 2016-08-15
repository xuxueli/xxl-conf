package com.xxl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xxl.controller.annotation.PermessionType;

/**
 * 帮助
 * @author xuxueli
 */
@Controller
@RequestMapping("/help")
public class HelpController {
	
	@RequestMapping("")
	@PermessionType
	public String index(){
		return "help/index";
	}
	
}
