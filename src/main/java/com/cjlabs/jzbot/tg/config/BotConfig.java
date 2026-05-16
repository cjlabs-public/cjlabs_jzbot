package com.cjlabs.jzbot.tg.config;

import com.cjlabs.domain.enums.IEnumStr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "tg")
public class BotConfig {

    private boolean enabled = true;

    // private Long botId;

    private String botName;

    private String username;

    private String token;

    private Mode mode = Mode.POLLING;

    private boolean dropPendingUpdates;

    private String commandPrefix = "/";

    @Getter
    @RequiredArgsConstructor
    public enum Mode implements IEnumStr {
        POLLING("polling", ""),
        WEBHOOK("webhook", "");

        private final String code;
        private final String msg;
    }
}
