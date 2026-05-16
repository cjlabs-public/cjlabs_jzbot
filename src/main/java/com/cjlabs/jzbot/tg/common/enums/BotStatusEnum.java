package com.cjlabs.jzbot.tg.common.enums;

import com.cjlabs.domain.enums.IEnumStr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Bot 运行状态枚举
 */
@Getter
@RequiredArgsConstructor
public enum BotStatusEnum implements IEnumStr {

    /**
     * 已停止
     */
    STOPPED("STOPPED", "已停止"),

    /**
     * 运行中
     */
    RUNNING("RUNNING", "运行中"),

    /**
     * 已暂停
     */
    PAUSED("PAUSED", "已暂停"),

    /**
     * 维护中
     */
    MAINTENANCE("MAINTENANCE", "维护中"),

    ;

    private final String code;
    private final String msg;

}
