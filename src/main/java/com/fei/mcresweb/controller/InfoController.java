package com.fei.mcresweb.controller;

import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.Configs;
import com.fei.mcresweb.restservice.info.BasicInfo;
import com.fei.mcresweb.restservice.info.RegisterInfo;
import com.fei.mcresweb.service.ContentService;
import com.fei.mcresweb.service.UserService;
import lombok.val;
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
    private final GlobalHandler globalHandler;
    private final ContentService contentService;
    private final UserService userService;

    public InfoController(ConfigManager configManager, GlobalHandler globalHandler, ContentService contentService,
        UserService userService) {
        this.configManager = configManager;
        this.globalHandler = globalHandler;
        this.contentService = contentService;
        this.userService = userService;
    }

    /**
     * 基础信息
     */
    @GetMapping("/basic")
    @ResponseBody
    public BasicInfo basic() {
        val bi = new BasicInfo(contentService.countCatalogue(), contentService.countEssay(), userService.countUser(),
            (System.currentTimeMillis() - configManager.getOrSummon(Configs.UNBOX_TIME, true)) / millisecond2day + 1,
            globalHandler.getHttpCount());
        return userService.needInit() ? bi.withInit() : bi;
    }
    
    private static final long millisecond2day = 1000L * 60 * 60 * 24;

    /**
     * 注册信息
     */
    @GetMapping("/register")
    @ResponseBody

    public RegisterInfo register() {
        return new RegisterInfo(configManager.getOrSummon(Configs.USERNAME_LENGTH, true));
    }
}
