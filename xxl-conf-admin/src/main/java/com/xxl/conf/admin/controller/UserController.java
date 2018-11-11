package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.model.XxlConfEnv;
import com.xxl.conf.admin.core.model.XxlConfProject;
import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.JacksonUtil;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfEnvDao;
import com.xxl.conf.admin.dao.XxlConfProjectDao;
import com.xxl.conf.admin.dao.XxlConfUserDao;
import com.xxl.conf.admin.service.impl.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-03-01
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private XxlConfUserDao xxlConfUserDao;
    @Resource
    private XxlConfProjectDao xxlConfProjectDao;
    @Resource
    private XxlConfEnvDao xxlConfEnvDao;

    @RequestMapping("")
    @PermessionLimit(adminuser = true)
    public String index(Model model){

        List<XxlConfProject> projectList = xxlConfProjectDao.findAll();
        model.addAttribute("projectList", projectList);

        List<XxlConfEnv> envList = xxlConfEnvDao.findAll();
        model.addAttribute("envList", envList);

        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @PermessionLimit(adminuser = true)
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
     * add
     *
     * @return
     */
    @RequestMapping("/add")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> add(XxlConfUser xxlConfUser){

        // valid
        if (StringUtils.isBlank(xxlConfUser.getUsername())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名不可为空");
        }
        if (StringUtils.isBlank(xxlConfUser.getPassword())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        if (!(xxlConfUser.getPassword().length()>=4 && xxlConfUser.getPassword().length()<=100)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
        }

        // passowrd md5
        String md5Password = DigestUtils.md5DigestAsHex(xxlConfUser.getPassword().getBytes());
        xxlConfUser.setPassword(md5Password);

        int ret = xxlConfUserDao.add(xxlConfUser);
        return ret>0? ReturnT.SUCCESS: ReturnT.FAIL;
    }

    /**
     * delete
     *
     * @return
     */
    @RequestMapping("/delete")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> delete(HttpServletRequest request, String username){

        XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
        if (loginUser.getUsername().equals(username)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "禁止操作当前登录账号");
        }

        /*List<XxlConfUser> adminList = xxlConfUserDao.pageList(0, 1 , null, 1);
        if (adminList.size()<2) {

        }*/

        xxlConfUserDao.delete(username);
        return ReturnT.SUCCESS;
    }

    /**
     * update
     *
     * @return
     */
    @RequestMapping("/update")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> update(HttpServletRequest request, XxlConfUser xxlConfUser){

        XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
        if (loginUser.getUsername().equals(xxlConfUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "禁止操作当前登录账号");
        }

        // valid
        if (StringUtils.isBlank(xxlConfUser.getUsername())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名不可为空");
        }

        XxlConfUser existUser = xxlConfUserDao.load(xxlConfUser.getUsername());
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名非法");
        }

        if (StringUtils.isNotBlank(xxlConfUser.getPassword())) {
            if (!(xxlConfUser.getPassword().length()>=4 && xxlConfUser.getPassword().length()<=50)) {
                return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
            }
            // passowrd md5
            String md5Password = DigestUtils.md5DigestAsHex(xxlConfUser.getPassword().getBytes());
            existUser.setPassword(md5Password);
        }
        existUser.setPermission(xxlConfUser.getPermission());

        int ret = xxlConfUserDao.update(existUser);
        return ret>0? ReturnT.SUCCESS: ReturnT.FAIL;
    }

    @RequestMapping("/updatePermissionData")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> updatePermissionData(HttpServletRequest request,
                                                    String username,
                                                    @RequestParam(required = false) String[] permissionData){

        XxlConfUser existUser = xxlConfUserDao.load(username);
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "参数非法");
        }

        String permissionDataArrStr = permissionData!=null?StringUtils.join(permissionData, ","):"";
        existUser.setPermissionData(permissionDataArrStr);
        xxlConfUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password){

        // new password(md5)
        if (StringUtils.isBlank(password)){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        if (!(password.length()>=4 && password.length()<=100)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // update pwd
        XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);

        XxlConfUser existUser = xxlConfUserDao.load(loginUser.getUsername());
        existUser.setPassword(md5Password);
        xxlConfUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

}
