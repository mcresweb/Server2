package com.fei.mcresweb.controller;

import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.config.UserAuth;
import com.fei.mcresweb.defs.TokenHelper;
import com.fei.mcresweb.restservice.keyword.KeywordList;
import com.fei.mcresweb.restservice.keyword.RemoveResp;
import com.fei.mcresweb.restservice.keyword.UseResult;
import com.fei.mcresweb.service.KeywordService;
import com.fei.mcresweb.service.UserService;
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
    private final KeywordService service;
    private final UserService userService;

    public KeywordController(KeywordService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    /**
     * 生成会员码接口
     *
     * @param body 请求体
     * @return 会员码列表
     */
    @PostMapping("/summon")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public String[] summon(HttpServletRequest req, @RequestBody KeywordService.SummonReq body) {
        return service.summon(userService.getUserIdByCookie(req), body);
    }

    /**
     * 列出会员码
     */
    @PostMapping("/list")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public KeywordList list(HttpServletRequest req, @RequestBody KeywordService.SearchReq body) {
        return service.listKeyword(userService.getUserIdByCookie(req), body);
    }

    /**
     * 使用会员码
     */
    @PostMapping("/use")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.LOGIN)
    public UseResult use(HttpServletRequest req, @RequestBody KeywordService.UseReq body) {
        return service.useKeyword(I18n.loc(req), userService.getUserIdByCookie(req), body);
    }

    /**
     * 移除会员码
     */
    @PostMapping("/remove")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public RemoveResp remove(HttpServletRequest req, @RequestBody KeywordService.RemoveReq body) {
        return service.removeKeyword(userService.getUserIdByCookie(req), body);
    }

    /**
     * 获取token长度
     *
     * @return 长度
     */
    @GetMapping("/token-len")
    @ResponseBody
    public int tokenLen() {
        return TokenHelper.TOKEN_LEN;
    }

}
