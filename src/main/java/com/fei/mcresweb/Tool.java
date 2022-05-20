package com.fei.mcresweb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tool {
    public static String byte2hex(byte[] buf) {
        return new BigInteger(1, buf).toString(16);
    }

    /**
     * 验证字符串是否有效
     *
     * @param str 字符串
     * @return 非null且非空
     */
    public static boolean valid(String str) {
        return str != null && !str.isEmpty();
    }

}
