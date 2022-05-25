package com.fei.mcresweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * alive服务
 */
@Controller
@RequestMapping("/api/alive")
public class AliveController {
    @GetMapping("/")
    public String alive() {
        return "alive";
    }
}
