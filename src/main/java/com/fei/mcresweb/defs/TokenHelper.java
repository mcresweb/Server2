package com.fei.mcresweb.defs;

import org.jetbrains.annotations.Contract;

public enum TokenHelper {
    ;
    /**
     * TOKEN长度
     */
    public static final int TOKEN_LEN = 64;

    /**
     * 是否是有效的token(仅初步判断)<br>
     * 判断长度、字符集
     *
     * @return 是否有效
     */
    @Contract("null -> true")
    public static boolean isInvalid(String token) {
        if (token == null || token.length() != TOKEN_LEN)
            return true;
        for (int i = 0; i < token.length(); i++) {
            var c = token.charAt(i);
            if ((c < '0' || c > '9') && (c < 'A' || c > 'Z'))
                return true;
        }
        return false;
    }
}
