package com.cjlabs.jzbot.tg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Telegram Bot 配置 (9.2.0)
 * <p>
 * 参考: <a href="https://github.com/rubenlagus/TelegramBots">...</a>
 */
@Configuration
public class TelegramBotConfig {

    /**
     * 创建 TelegramBotsLongPollingApplication Bean
     * 用于管理所有长轮询机器人
     */
    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication() throws TelegramApiException {
        return new TelegramBotsLongPollingApplication();
    }
}