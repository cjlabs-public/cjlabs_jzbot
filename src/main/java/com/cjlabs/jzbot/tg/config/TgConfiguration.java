package com.cjlabs.jzbot.tg.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BotConfig.class)
@ConditionalOnProperty(prefix = "tg", name = "enabled", havingValue = "true")
public class TgConfiguration {
}
