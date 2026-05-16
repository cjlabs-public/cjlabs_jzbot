package com.cjlabs.jzbot.tg.common.enums;

import com.cjlabs.domain.enums.IEnumStr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotTypeEnum implements IEnumStr {

    /**
     * 群管理机器人
     */
    GROUP_MANAGE("GROUP_MANAGE", "群管理机器人"),

    /**
     * 双向转发机器人
     */
    BRIDGE("BRIDGE", "双向转发机器人"),

    /**
     * 客服机器人
     */
    CUSTOMER_SERVICE("CUSTOMER_SERVICE", "客服机器人"),

    /**
     * 通知机器人
     */
    NOTIFICATION("NOTIFICATION", "通知机器人"),

    /**
     * 工具机器人
     */
    UTILITY("UTILITY", "工具机器人"),

    /**
     * 信息查询机器人
     */
    INFO_QUERY("INFO_QUERY", "信息查询机器人"),

    ;

    private final String code;
    private final String msg;
}
