package com.cjlabs.jzbot.tg.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Telegram Bot 配置。
 */
@Configuration
@EnableConfigurationProperties(BotConfig.class)
@ConditionalOnProperty(prefix = "tg", name = "enabled", havingValue = "true")
public class TgConfiguration {

    /**
     * 创建 TelegramBotsLongPollingApplication Bean，用于管理长轮询机器人。
     */
    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication() throws TelegramApiException {
        return new TelegramBotsLongPollingApplication();
    }
}
