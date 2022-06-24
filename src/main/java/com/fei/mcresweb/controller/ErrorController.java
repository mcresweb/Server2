package com.fei.mcresweb.controller;

import com.fei.mcresweb.config.I18n;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class ErrorController {

    @RequestMapping("/errors")
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object err(HttpServletRequest req) {
        return I18n.msg("internal-error", I18n.loc(req));
    }
}
