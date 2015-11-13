package com.xxl.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.controller.annotation.PermessionType;
import com.xxl.core.model.ZNodeEntry;
import com.xxl.core.result.ReturnT;
import com.xxl.service.IZNodeEntryService;

/**
 * 配置管理
 * @author xuxueli
 */
@Controller
@RequestMapping("/zkcfg")
public class ZkCfgController {
	
	@Resource
	private IZNodeEntryService zNodeEntryService;
	
	@RequestMapping("")
	@PermessionType
	public String index(Model model, String znodeKey){
		
		List<ZNodeEntry> fileterData = zNodeEntryService.selectLikeKey(znodeKey);
		
		model.addAttribute("znodeKey", znodeKey);
		model.addAttribute("fileterData", fileterData);
		return "zkcfg/index";
	}
	
	/**
	 * get
	 * @param key
	 * @return
	 */
	@RequestMapping("delete")
	@ResponseBody
	@PermessionType
	public ReturnT<String> delete(String znodeKey){
		zNodeEntryService.deleteByKey(znodeKey);
		return new ReturnT<String>();
	}
	
	/**
	 * create/update
	 * @param znodeKey
	 * @param znodeValue
	 * @return
	 */
	@RequestMapping("setData")
	@ResponseBody
	@PermessionType
	public ReturnT<String> setData(ZNodeEntry node){
		zNodeEntryService.updateNode(node);
		return new ReturnT<String>();
	}
	
}
