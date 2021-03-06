package com.fei.mcresweb.controller;

import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.config.UserAuth;
import com.fei.mcresweb.restservice.content.*;
import com.fei.mcresweb.service.ContentService;
import com.fei.mcresweb.service.UserService;
import lombok.NonNull;
import lombok.val;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

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
     * @return 内容列表
     */
    @GetMapping("/list-essay")
    @ResponseBody
    public EssayList listEssay(HttpServletResponse resp, @RequestParam("catalogue") String catalogue,
        @RequestParam("category") String category, @RequestParam("page") int page) {
        val data = service.listEssay(catalogue, category, page);
        if (data == null)
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return data;
    }

    /**
     * 推荐内容
     *
     * @param id 目标essay
     * @return 内容列表
     */
    @GetMapping("/recommend/get")
    @ResponseBody
    public EssayList recommendEssay(HttpServletResponse resp, @RequestParam("id") Integer id) {
        return service.recommendEssay(id);
    }

    /**
     * 获取推荐内容信息
     *
     * @param id 目标essay
     * @return 推荐信息
     */
    @GetMapping("/recommend/info")
    @ResponseBody
    public EssayRecommendList.EssayRecommendInfo recommendEssayInfo(HttpServletResponse resp,
        @RequestParam("id") int id) {
        val data = service.recommendEssayInfo(id);
        if (data == null)
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return data;
    }

    /**
     * 添加推荐内容
     *
     * @param req    req
     * @param id     推荐ID
     * @param expire 过期时间戳
     */
    @GetMapping("/recommend/add")
    @UserAuth(UserAuth.AuthType.ADMIN)
    public void addEssayRecommend(HttpServletRequest req, HttpServletResponse resp, @RequestParam("id") int id,
        @RequestParam(value = "expire", required = false) Long expire) {
        service.addEssayRecommend(user.getUserIdByCookie(req), id, expire);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 列出推荐内容
     *
     * @param page 页码(0开始)
     * @return 推荐内容信息列表
     */
    @GetMapping("/recommend/list")
    @UserAuth(UserAuth.AuthType.ADMIN)
    @ResponseBody
    public EssayRecommendList listRecommendEssay(@RequestParam(value = "page", defaultValue = "0") int page) {
        return service.listRecommendEssay(page);
    }

    /**
     * 获取内容详细信息的接口
     *
     * @return 详细信息
     */
    @GetMapping("/essay")
    @ResponseBody
    public EssayDetail essay(@RequestParam("id") int id, HttpServletResponse resp) {
        val detail = service.essay(id);
        if (detail == null)
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return detail;
    }

    /**
     * 获取内容的编辑数据
     *
     * @return 编辑数据
     */
    @GetMapping("/essay-edit")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public UploadEssay essayEditData(@RequestParam("id") int id, HttpServletResponse resp) {
        val detail = service.getEssayEditData(id);
        if (detail == null)
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return detail;
    }

    /**
     * 修改内容信息
     *
     * @param req  req
     * @param body body
     * @return 修改结果
     */
    @PostMapping("/essay-edit")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public UploadResp<Integer> editEssay(HttpServletRequest req, @RequestParam("id") int id,
        @RequestBody UploadEssay body) {
        return service.uploadEssay(I18n.loc(req), user.getUserIdByCookie(req), id, body);
    }

    /**
     * 获取内容详细信息的接口
     *
     * @return 详细信息
     */
    @GetMapping("/random-essay")
    @ResponseBody
    public EssayDetail randomEssay(HttpServletResponse resp) {
        val id = service.randomEssayId();
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return essay(id, resp);
    }

    /**
     * 修改大分类信息
     *
     * @param body body
     * @return 修改结果
     */
    @PostMapping("/mod-catalogue")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public ModResp modCatalogue(HttpServletRequest req, @RequestBody ContentService.ModCatalogue body) {
        return service.modCatalogue(I18n.loc(req), body);
    }

    /**
     * 修改小分类信息
     *
     * @param body body
     * @return 修改结果
     */
    @PostMapping("/mod-category")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public ModResp modCategory(HttpServletRequest req, @RequestBody ContentService.ModCategory body) {
        return service.modCategory(I18n.loc(req), body);
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
    @UserAuth(UserAuth.AuthType.ADMIN)
    public UploadResp<Integer> uploadEssay(HttpServletRequest req, @RequestBody UploadEssay body) {
        return service.uploadEssay(I18n.loc(req), user.getUserIdByCookie(req), null, body);
    }

    /**
     * 上传资源文件
     *
     * @param req   req
     * @param id    内容ID
     * @param files 资源文件
     * @return 上传结果
     */
    @PostMapping("/upload-file")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public UploadResp<Collection<UUID>> uploadFile(HttpServletRequest req, @RequestParam("essay") int id,
        @RequestPart("file") MultipartFile[] files) {
        return service.uploadFile(I18n.loc(req), user.getUserIdByCookie(req), id, files);
    }

    /**
     * 上传资源文件
     *
     * @param req   req
     * @param id    内容ID
     * @param files 资源文件
     * @return 上传结果
     */
    @GetMapping("/remove-file")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public ModResp removeFile(HttpServletRequest req, @RequestParam("essay") int id,
        @RequestParam("file") @NonNull UUID files) {
        return service.removeFile(id, files);
    }

    /**
     * 列出文件列表
     *
     * @param req   req
     * @param essay 内容ID
     * @return 文件列表
     */
    @GetMapping("/list-file")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.VIP)
    public FileListResp listFile(HttpServletRequest req, @RequestParam("essay") int essay) {
        return service.listFile(I18n.loc(req), user.getUserIdByCookie(req), essay);
    }

    /**
     * 获取文件
     *
     * @param req   req
     * @param essay 内容ID
     * @param file  文件ID
     * @return 文件
     */
    @GetMapping("/file")
    @UserAuth(UserAuth.AuthType.VIP)
    public ResponseEntity<FileSystemResource> getFile(HttpServletRequest req, @RequestParam("essay") int essay,
        @RequestParam("file") UUID file, @RequestHeader("User-Agent") String userAgent) throws IOException {

        if (!user.isVip(user.getUserIdByCookie(req)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        val res = service.getFile(essay, file);
        if (res == null)
            return ResponseEntity.notFound().build();
        val info = service.getFileInfo(essay, file);
        if (info == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()//
            .cacheControl(CacheControl.empty().cachePublic())//
            .header("Content-Disposition",
                (userAgent.contains("MSIE") ? "attachment;filename=" : "attacher;filename*=UTF-8''")
                    + URLEncoder.encode(info.name(), StandardCharsets.UTF_8))//
            .lastModified(res.lastModified())//
            .contentLength(res.contentLength())//
            .contentType(MediaType.APPLICATION_OCTET_STREAM)//
            .body(res);
    }
}
