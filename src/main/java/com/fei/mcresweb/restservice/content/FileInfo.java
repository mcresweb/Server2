package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.EssayFileInfo;

import java.util.UUID;

/**
 * 文件信息
 *
 * @param id   文件UUID
 * @param name 文件名
 * @param size 文件大小(byte)
 * @param sha1 文件sha1摘要
 */
public record FileInfo(UUID id, String name, long size, String sha1) {
    public FileInfo(EssayFileInfo info) {
        this(info.getFileId(), info.getName(), info.getSize(), info.getSha1());
    }
}
