package com.cjlabs.jzbot.tg.config;

import com.cjlabs.domain.enums.IEnumStr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TgBotModelEnum implements IEnumStr {

    POLLING("polling", ""),

    WEBHOOK("webhook", ""),
    ;

    private final String code;

    private final String msg;
}
