package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfUserDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-03-01
 */
@Controller
@RequestMapping("/user")
public class XxlConfUserController {

    @Resource
    private XxlConfUserDao xxlConfUserDao;

    @RequestMapping("")
    public String index(Model model, String appname){
        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username,
                                        int permission) {
        // xxlConfNode in mysql
        List<XxlConfUser> data = xxlConfUserDao.pageList(start, length, username, permission);
        int list_count = xxlConfUserDao.pageListCount(start, length, username, permission);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("data", data);
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        return maps;
    }

    /**
     * delete
     *
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(String username){
        xxlConfUserDao.delete(username);
        return ReturnT.SUCCESS;
    }

    /**
     * add
     *
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(XxlConfUser xxlConfUser){

        // valid
        if (StringUtils.isBlank(xxlConfUser.getUsername())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名不可为空");
        }
        if (StringUtils.isBlank(xxlConfUser.getPassword())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }

        // passowrd md5
        String md5Password = DigestUtils.md5DigestAsHex(xxlConfUser.getPassword().getBytes());
        xxlConfUser.setPassword(md5Password);

        int ret = xxlConfUserDao.add(xxlConfUser);
        return ret>0? ReturnT.SUCCESS: ReturnT.FAIL;
    }

    /**
     * update
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(XxlConfUser xxlConfUser){

        // valid
        if (StringUtils.isBlank(xxlConfUser.getUsername())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名不可为空");
        }
        if (StringUtils.isBlank(xxlConfUser.getPassword())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }

        XxlConfUser existUser = xxlConfUserDao.load(xxlConfUser.getUsername());
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名非法");
        }

        if (StringUtils.isNotBlank(xxlConfUser.getPassword())) {
            // passowrd md5
            String md5Password = DigestUtils.md5DigestAsHex(xxlConfUser.getPassword().getBytes());
            existUser.setPassword(md5Password);
        }
        existUser.setPermission(xxlConfUser.getPermission());

        int ret = xxlConfUserDao.update(existUser);
        return ret>0? ReturnT.SUCCESS: ReturnT.FAIL;
    }

}
