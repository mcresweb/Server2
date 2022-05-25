package com.fei.mcresweb.controller;

import com.fei.mcresweb.restservice.img.UploadResp;
import com.fei.mcresweb.service.ImgService;
import com.fei.mcresweb.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 图片接口
 *
 * @author yuanlu
 */
@Controller
@RequestMapping("/api/img")
public class ImgController {
    @Autowired
    private UserService userService;
    @Autowired
    private ImgService service;

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    @ResponseBody
    public UploadResp upload(HttpServletRequest req, @RequestPart("file") @NonNull MultipartFile file)
        throws IOException {
        return service.uploadImg(userService.getUserIdByCookie(req), file);
    }
}
