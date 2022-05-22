package com.fei.mcresweb.controller;

import com.fei.mcresweb.restservice.keyword.KeywordList;
import com.fei.mcresweb.restservice.keyword.RemoveResp;
import com.fei.mcresweb.restservice.keyword.UseResult;
import com.fei.mcresweb.service.KeywordService;
import com.fei.mcresweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 会员码接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/keyword")
public class KeywordController {
    @Autowired
    private KeywordService service;
    @Autowired
    private UserService userService;

    /**
     * 生成会员码接口
     *
     * @param body 请求体
     * @return 会员码列表
     */
    @PostMapping("/summon")
    @ResponseBody
    public String[] summon(HttpServletRequest req, @RequestBody KeywordService.SummonReq body) {
        return service.summon(userService.getUserIdByCookie(req), body);
    }

    /**
     * 列出会员码
     */
    @GetMapping("/list")
    @ResponseBody
    public KeywordList list(HttpServletRequest req, @RequestParam(value = "type", defaultValue = "0") int type,
        @RequestParam(value = "summoner", required = false) Integer summoner,
        @RequestParam(value = "user", required = false) Integer user,
        @RequestParam(value = "page", defaultValue = "0") int page) {
        return service.listKeyword(userService.getUserIdByCookie(req), type, summoner, user, page);
    }

    /**
     * 使用会员码
     */
    @PostMapping("/use")
    @ResponseBody
    public UseResult use(HttpServletRequest req, @RequestBody KeywordService.UseReq body) {
        return service.useKeyword(userService.getUserIdByCookie(req), body);
    }

    /**
     * 移除会员码
     */
    @PostMapping("/remove")
    @ResponseBody
    public RemoveResp remove(HttpServletRequest req, @RequestBody KeywordService.RemoveReq body) {
        return service.removeKeyword(userService.getUserIdByCookie(req), body);
    }

}
