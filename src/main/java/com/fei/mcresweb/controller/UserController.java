package com.fei.mcresweb.controller;

import com.fei.mcresweb.defs.ConfType;
import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.CookiesManager;
import com.fei.mcresweb.restservice.user.*;
import com.fei.mcresweb.service.UserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;
    private final ConfigManager configManager;
    @Autowired
    private CookiesManager cookiesManager;

    public UserController(UserService service, ConfigManager configManager) {
        this.service = service;
        this.configManager = configManager;
    }

    /**
     * 用户登录接口
     *
     * @param req 请求体
     * @return 登录结果信息
     */
    @PostMapping("/login")
    @ResponseBody
    public LoginInfo login(@RequestBody UserService.loginReq req, HttpServletResponse resp) {
        val info = service.login(req);
        if (info.isSuccess()) {
            resp.addCookie(service.summonTokenCookie(info.getUserid()));
        }
        return info;
    }

    /**
     * 登录数据接口
     *
     * @return 服务器登录数据信息
     */
    @GetMapping("/salt")
    @ResponseBody
    public SaltInfo salt() {
        return new SaltInfo(configManager.getOrSummon(ConfType.LOGIN_SALT, true), System.currentTimeMillis());
    }

    /**
     * 用户登出接口
     */
    @GetMapping("/logout")
    public void logout(HttpServletResponse resp) {
        resp.addCookie(cookiesManager.cookie(CookiesManager.CookiesField.LOGIN, null));
    }

    /**
     * 用户注册接口
     *
     * @param req 注册数据
     * @return 注册结果信息
     */
    @PostMapping("/register")
    @ResponseBody
    public RegisterInfo register(@RequestBody UserService.registerReq req, HttpServletResponse resp) {
        val info = service.register(req);
        if (info.isSuccess()) {
            resp.addCookie(service.summonTokenCookie(info.getUserid()));
        }
        return info;
    }

    /**
     * 查询其它用户信息的接口
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/info")
    @ResponseBody
    public OtherUserInfo info(@RequestParam("id") int id) {
        return service.infoOther(id);
    }

    /**
     * 查询自己用户信息的接口
     *
     * @return 用户信息
     */
    @GetMapping("/me")
    @ResponseBody
    public MyUserInfo me(HttpServletRequest req) {
        val id = service.getUserIdByCookie(req);
        return id == null ? null : service.infoMe(id);
    }

}
