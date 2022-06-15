package com.fei.mcresweb.restservice.info;

/**
 * 基础信息
 *
 * @param catalogue 分类数量
 * @param essay     内容数量
 * @param user      用户数量
 * @param day       建站天数
 * @param visit     今日访问
 */
public record BasicInfo(String catalogue, String essay, String user, String day, String visit) {
    public BasicInfo(long catalogue, long essay, long user, long day, long visit) {
        this(Long.toString(catalogue), Long.toString(essay), Long.toString(user), Long.toString(day),
            Long.toString(visit));//大数在前端无法处理, 转为字符串
    }

}
