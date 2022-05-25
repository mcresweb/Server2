package com.fei.mcresweb.controller;

import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.Configs;
import com.fei.mcresweb.restservice.info.BasicInfo;
import com.fei.mcresweb.restservice.info.RegisterInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 信息接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/info")
public class InfoController {
    private final ConfigManager configManager;

    public InfoController(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * 基础信息
     */
    @GetMapping("/basic")
    @ResponseBody
    public BasicInfo basic() {
        return new BasicInfo(3, 102, 5, 120);
    }

    /**
     * 注册信息
     */
    @GetMapping("/register")
    @ResponseBody
    public RegisterInfo register() {
        return new RegisterInfo(configManager.getOrSummon(Configs.USERNAME_LENGTH, true));
    }
}
