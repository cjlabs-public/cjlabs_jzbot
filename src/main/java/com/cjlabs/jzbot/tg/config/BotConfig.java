package com.cjlabs.jzbot.tg.config;

import lombok.Getter;
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

    private TgBotModelEnum mode = TgBotModelEnum.POLLING;

    private boolean dropPendingUpdates;

    private String commandPrefix = "/";

}
