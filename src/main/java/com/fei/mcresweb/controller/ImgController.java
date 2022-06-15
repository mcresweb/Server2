package com.fei.mcresweb.controller;

import com.fei.mcresweb.config.UserAuth;
import com.fei.mcresweb.restservice.img.UploadResp;
import com.fei.mcresweb.service.ImgService;
import com.fei.mcresweb.service.UserService;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 图片接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/img")
public class ImgController {
    private final UserService userService;
    private final ImgService service;

    public ImgController(UserService userService, ImgService service) {
        this.userService = userService;
        this.service = service;
    }

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    @ResponseBody
    @UserAuth(UserAuth.AuthType.ADMIN)
    public UploadResp upload(@NonNull HttpServletRequest req) throws IOException {
        System.out.println(req.getContentType());
        return service.uploadImg(userService.getUserIdByCookie(req), req.getInputStream(), req.getContentLengthLong());
    }

    @GetMapping("/get/{uuid}")
    public void getImg(@PathVariable("uuid") @NonNull UUID uuid, @NonNull HttpServletResponse resp) throws Exception {
        resp.setContentType(imgType);
        service.getImg(uuid).getBinaryStream().transferTo(resp.getOutputStream());
    }

    public static final String imgType = "image/webp";
}
