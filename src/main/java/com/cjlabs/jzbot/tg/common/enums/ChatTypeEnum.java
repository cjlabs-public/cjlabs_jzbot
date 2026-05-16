package com.cjlabs.jzbot.tg.common.enums;

import com.cjlabs.domain.enums.IEnumStr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

/**
 * Telegram 聊天类型枚举
 * 对应 Telegram API 中的 chat_type 字段
 */
@Getter
@RequiredArgsConstructor
public enum ChatTypeEnum implements IEnumStr {

    PRIVATE("private", "私聊"),
    GROUP("group", "群组"),
    SUPERGROUP("supergroup", "超级群组"),
    CHANNEL("channel", "频道"),

    ;

    private static final Set<ChatTypeEnum> GROUP_TYPES = EnumSet.of(GROUP, SUPERGROUP);

    /**
     * Telegram API 中的类型值
     */
    private final String code;

    /**
     * 中文描述
     */
    private final String msg;

    /**
     * 检查是否为群组类型（包括普通群组和超级群组）
     */
    public boolean isGroup() {
        return GROUP_TYPES.contains(this);
    }

    /**
     * 检查是否为群组类型（包括普通群组和超级群组）
     */
    public boolean isNotGroup() {
        return !isGroup();
    }

    /**
     * 检查是否为私聊
     */
    public boolean isPrivate() {
        return this == PRIVATE;
    }

    /**
     * 检查是否为私聊
     */
    public boolean isNotPrivate() {
        return !isPrivate();
    }

    /**
     * 检查是否为频道
     */
    public boolean isChannel() {
        return this == CHANNEL;
    }

    /**
     * 检查是否为频道
     */
    public boolean isNotChannel() {
        return !isChannel();
    }
}
