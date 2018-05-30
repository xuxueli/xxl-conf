package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.model.XxlConfEnv;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfEnvDao;
import com.xxl.conf.admin.dao.XxlConfNodeDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 环境管理
 *
 * @author xuxueli 2018-05-30
 */
@Controller
@RequestMapping("/env")
public class EnvController {
	
	@Resource
	private XxlConfEnvDao xxlConfEnvDao;
    @Resource
    private XxlConfNodeDao xxlConfNodeDao;


	@RequestMapping
	@PermessionLimit(adminuser = true)
	public String index(Model model) {

		List<XxlConfEnv> list = xxlConfEnvDao.findAll();
		model.addAttribute("list", list);

		return "env/env.index";
	}

	@RequestMapping("/save")
	@PermessionLimit(adminuser = true)
	@ResponseBody
	public ReturnT<String> save(XxlConfEnv xxlConfEnv){

		// valid
		if (StringUtils.isBlank(xxlConfEnv.getEnv())) {
			return new ReturnT<String>(500, "Env不可为空");
		}
		if (xxlConfEnv.getEnv().length()<3 || xxlConfEnv.getEnv().length()>50) {
			return new ReturnT<String>(500, "Env长度限制为4~50");
		}
		if (StringUtils.isBlank(xxlConfEnv.getTitle())) {
			return new ReturnT<String>(500, "请输入Env名称");
		}

		// valid repeat
		XxlConfEnv existEnv = xxlConfEnvDao.load(xxlConfEnv.getEnv());
		if (existEnv != null) {
			return new ReturnT<String>(500, "Env已存在，请勿重复添加");
		}

		int ret = xxlConfEnvDao.save(xxlConfEnv);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@PermessionLimit(adminuser = true)
	@ResponseBody
	public ReturnT<String> update(XxlConfEnv xxlConfEnv){

		// valid
		if (StringUtils.isBlank(xxlConfEnv.getEnv())) {
			return new ReturnT<String>(500, "Env不可为空");
		}
		if (StringUtils.isBlank(xxlConfEnv.getTitle())) {
			return new ReturnT<String>(500, "请输入Env名称");
		}

		int ret = xxlConfEnvDao.update(xxlConfEnv);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@PermessionLimit(adminuser = true)
	@ResponseBody
	public ReturnT<String> remove(String env){

		if (StringUtils.isBlank(env)) {
			return new ReturnT<String>(500, "参数Env非法");
		}

        // valid
        int list_count = xxlConfNodeDao.pageListCount(0, 10, env, null, null);
        if (list_count > 0) {
            return new ReturnT<String>(500, "拒绝删除，该Env下存在配置数据");
        }

		// valid can not be empty
		List<XxlConfEnv> allList = xxlConfEnvDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<String>(500, "拒绝删除, 需要至少预留一个Env");
		}

		int ret = xxlConfEnvDao.delete(env);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}
