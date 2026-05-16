package com.cjlabs.jzbot.tg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {
    TEXT("TEXT", "文本消息"),
    PHOTO("PHOTO", "图片消息"),
    VIDEO("VIDEO", "视频消息"),
    DOCUMENT("DOCUMENT", "文档消息"),
    AUDIO("AUDIO", "音频消息"),
    VOICE("VOICE", "语音消息"),
    STICKER("STICKER", "贴纸消息"),
    LOCATION("LOCATION", "位置消息"),
    CONTACT("CONTACT", "联系人消息");

    private final String code;
    private final String description;
}