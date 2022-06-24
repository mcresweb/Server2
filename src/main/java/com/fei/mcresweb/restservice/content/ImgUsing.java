package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.EssayImgs;
import lombok.NonNull;

/**
 * 使用方式
 *
 * @param head 是否显示在头部
 * @param list 是否显示在内容列表
 */
public record ImgUsing(boolean head, boolean list) {
    public ImgUsing(@NonNull EssayImgs img) {
        this(img.isShowInHead(), img.isShowInList());
    }
}