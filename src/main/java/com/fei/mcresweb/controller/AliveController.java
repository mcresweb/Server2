package com.fei.mcresweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * alive服务
 */
@Controller
@RequestMapping("/api/alive")
public class AliveController {
    @GetMapping("/")
    @ResponseBody
    public String alive() {
        return "alive";
    }
}
