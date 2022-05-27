package com.fei.mcresweb.controller;

import com.fei.mcresweb.restservice.content.*;
import com.fei.mcresweb.service.ContentService;
import com.fei.mcresweb.service.UserService;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 内容接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService service;
    private final UserService user;

    public ContentController(ContentService service, UserService user) {
        this.service = service;
        this.user = user;
    }

    /**
     * 列出大分类接口
     *
     * @return 大分类列表
     */
    @GetMapping("/list-catalogue")
    @ResponseBody
    public Collection<CatalogueInfo> listCatalogue() {
        return service.listCatalogue();
    }

    /**
     * 列出小分类接口
     *
     * @return 小分类列表
     */
    @GetMapping("/list-category")
    @ResponseBody
    public Collection<CategoryInfo> listCategory(@RequestParam("catalogue") String catalogue) {
        return service.listCategory(catalogue);
    }

    /**
     * 列出内容的接口
     *
     * @return 小分类列表
     */
    @GetMapping("/list-essay")
    @ResponseBody
    public EssayList listEssay(@RequestParam("catalogue") String catalogue, @RequestParam("category") String category,
        @RequestParam("page") int page) {
        return service.listEssay(catalogue, category, page);
    }

    /**
     * 获取内容详细信息的接口
     *
     * @return 详细信息
     */
    @GetMapping("/essay")
    @ResponseBody
    public EssayDetail essay(@RequestParam("id") int id) {
        return service.essay(id);
    }

    /**
     * 获取内容详细信息的接口
     *
     * @return 详细信息
     */
    @GetMapping("/random-essay")
    @ResponseBody
    public EssayDetail randomEssay() {
        val id = service.randomEssayId();
        return id == null ? null : essay(id);
    }

    /**
     * 修改大分类信息
     *
     * @param body body
     * @return 修改结果
     */
    @PostMapping("/mod-catalogue")
    @ResponseBody
    public ModResp modCatalogue(@RequestBody ContentService.ModCatalogue body) {
        return service.modCatalogue(body);
    }

    /**
     * 修改小分类信息
     *
     * @param body body
     * @return 修改结果
     */
    @PostMapping("/mod-category")
    @ResponseBody
    public ModResp modCategory(@RequestBody ContentService.ModCategory body) {
        return service.modCategory(body);
    }

    /**
     * 上传内容信息
     *
     * @param req  req
     * @param body body
     * @return 上传结果
     */
    @PostMapping("/upload-essay")
    @ResponseBody
    public UploadResp<Integer> uploadEssay(HttpServletRequest req, @RequestBody ContentService.UploadEssay body) {
        return service.uploadEssay(user.getUserIdByCookie(req), body);
    }
}
