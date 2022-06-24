package com.fei.mcresweb.controller;

import com.fei.mcresweb.config.CallCooling;
import com.fei.mcresweb.config.UserAuth;
import com.fei.mcresweb.restservice.search.SearchResult;
import com.fei.mcresweb.service.ContentSearchService;
import lombok.NonNull;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 搜索接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/search")
public class SearchController {

    private final ContentSearchService contentSearchService;

    public SearchController(ContentSearchService contentSearchService) {
        this.contentSearchService = contentSearchService;
    }

    /**
     * 搜索内容
     *
     * @return 搜索结果
     */
    @GetMapping("/essay")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.LOGIN)
    @CallCooling(value = 5 * 1000, unlimited = CallCooling.UnlimitedType.VIP)
    public SearchResult summon(//
        @RequestParam("t") @NonNull String txt,//
        @RequestParam(value = "catalogue", required = false) @Nullable String catalogue,//
        @RequestParam(value = "category", required = false) @Nullable String category,//
        @RequestParam(value = "user", required = false) @Nullable String user,//
        @NonNull HttpServletResponse resp//
    ) {
        try {
            return contentSearchService.searchEssay(txt, catalogue, category, user);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            } catch (IOException ignored) {
            }
            return null;
        }
    }
}
