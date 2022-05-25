package com.fei.mcresweb.restservice.info;

/**
 * 基础信息
 *
 * @param catalogue 分类数量
 * @param essay     内容数量
 * @param user      用户数量
 * @param day       建站天数
 */
public record BasicInfo(int catalogue, int essay, int user, int day) {
}
