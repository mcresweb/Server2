package com.fei.mcresweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@Controller
@RequestMapping("/api/errors")
public class ErrorController {
    private final HashMap<String, Object> errMap;

    public ErrorController() {
        errMap = new HashMap<>();
        errMap.put("err", "internal error");
    }

    @RequestMapping("/")
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object err() {
        return errMap;
    }
}
