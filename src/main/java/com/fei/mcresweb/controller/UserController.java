package com.fei.mcresweb.controller;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.config.UserAuth;
import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.Configs;
import com.fei.mcresweb.defs.CookiesManager;
import com.fei.mcresweb.restservice.user.*;
import com.fei.mcresweb.service.UserService;
import lombok.val;
import org.springframework.http.HttpStatus;
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
    private final CookiesManager cookiesManager;

    public UserController(UserService service, ConfigManager configManager, CookiesManager cookiesManager) {
        this.service = service;
        this.configManager = configManager;
        this.cookiesManager = cookiesManager;
    }

    /**
     * 用户登录接口
     *
     * @param body 请求体
     * @return 登录结果信息
     */
    @PostMapping("/login")
    @ResponseBody
    public LoginInfo login(HttpServletRequest req, HttpServletResponse resp, @RequestBody UserService.loginReq body) {
        val info = service.login(I18n.loc(req), body);
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
        return new SaltInfo(configManager.getOrSummon(Configs.LOGIN_SALT, true), System.currentTimeMillis(),
            configManager.getOrSummon(Configs.VAPTCHA_VID, true));
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
     * @param body 注册数据
     * @return 注册结果信息
     */
    @PostMapping("/register")
    @ResponseBody
    public RegisterInfo register(HttpServletRequest req, HttpServletResponse resp,
        @RequestBody UserService.registerReq body) {
        val info = service.register(I18n.loc(req), body);
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
        return service.infoMe(service.getUserIdByCookie(req));
    }

    /**
     * 查询自己VIP信息的接口
     *
     * @return VIP信息
     */
    @GetMapping("/me-vip")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.LOGIN)
    public VipInfo meVip(HttpServletRequest req) {
        return service.vipUser(service.getUserIdByCookie(req));
    }

    /**
     * 验证码信息(VID)
     *
     * @return VID
     */
    @GetMapping("/vaptcha")
    @ResponseBody
    public String vaptcha() {
        if (Tool.valid(configManager.getOrSummon(Configs.VAPTCHA_KEY, true)))
            return configManager.getOrSummon(Configs.VAPTCHA_VID, true);
        return "";
    }

    /**
     * 查询自己用户信息的接口
     */
    @PostMapping("/vaptcha")
    @ResponseStatus(HttpStatus.OK)
    @UserAuth(UserAuth.AuthType.ADMIN)
    public void setVaptcha(HttpServletRequest req, @RequestBody UserService.SetVaptchaReq data) {
        if (!service.isAdmin(service.getUserIdByCookie(req)))
            return;
        service.setVaptcha(data);
    }

}
