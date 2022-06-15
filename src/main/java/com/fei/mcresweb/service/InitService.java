package com.fei.mcresweb.service;

/**
 * 初始化服务
 */
public interface InitService {
    /**
     * 生成临时验证码在后端文件系统内
     *
     * @return 生成路径(相对路径)
     */
    String summonID();

    /**
     * 移除临时验证码
     *
     * @return 是否成功移除<br>false: 不存在
     */
    boolean removeID();

    /**
     * 检查输入的临时验证码是否正确
     *
     * @param id 输入的验证码
     * @return 是否正确
     */
    boolean checkID(String id);
}
