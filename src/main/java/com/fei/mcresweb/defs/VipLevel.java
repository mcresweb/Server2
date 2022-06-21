package com.fei.mcresweb.defs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * VIP等级
 */
@RequiredArgsConstructor
public enum VipLevel {
    /**
     * 无
     */
    NONE("无"),
    /**
     * VIP
     */
    VIP("VIP"),
    /**
     * VIP+
     */
    VIP_PLUS("VIP+"),
    /**
     * MVP
     */
    MVP("MVP"),
    /**
     * MVP+
     */
    MVP_PLUS("MVP+"),
    /**
     * MVP++
     */
    MVP_PLUS_PLUS("MVP++"),
    /**
     * GOD
     */
    GOD("GOD");
    @Getter
    private final String name;

    public boolean isVip() {
        return this != NONE;
    }
}
