package com.fei.mcresweb.restservice.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * 基础信息
 */
@Getter
@FieldDefaults(makeFinal = true)
@AllArgsConstructor
public class BasicInfo {
    /**
     * 分类数量
     */
    String catalogue;
    /**
     * 内容数量
     */
    String essay;
    /**
     * 用户数量
     */
    String user;
    /**
     * 建站天数
     */
    String day;
    /**
     * 今日访问
     */
    String visit;

    public BasicInfo(long catalogue, long essay, long user, long day, long visit) {
        this(Long.toString(catalogue), Long.toString(essay), Long.toString(user), Long.toString(day),
            Long.toString(visit));//大数在前端无法处理, 转为字符串
    }

    public BasicInfo withInit() {
        return new InitInfo(catalogue, essay, user, day, visit);
    }

}
