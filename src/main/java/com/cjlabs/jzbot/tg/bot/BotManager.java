package com.cjlabs.jzbot.tg.bot;

import com.cjlabs.jzbot.tg.config.BotConfig;
import com.cjlabs.jzbot.tg.command.UpdateRouter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Bot 管理器 (TelegramBots 9.2.0 版本)
 * 负责创建并注册当前唯一的 Telegram 机器人
 */
@Slf4j
@Service
public class BotManager implements ApplicationListener<ApplicationReadyEvent> {
    @Getter
    private final TelegramBotsLongPollingApplication botsApi;
    @Getter
    private final UpdateRouter updateRouter;
    private final BotConfig botConfig;
    @Getter
    private LongPollingTelegramBot bot;

    public BotManager(TelegramBotsLongPollingApplication botsApi,
                      UpdateRouter updateRouter,
                      BotConfig botConfig) {
        this.botsApi = botsApi;
        this.updateRouter = updateRouter;
        this.botConfig = botConfig;
    }

    /**
     * 获取当前唯一的 bot。
     */
    public LongPollingTelegramBot getCurrentBot() {
        return bot;
    }

    public synchronized void registerBot() throws TelegramApiException {
        if (bot != null) {
            log.info("Bot already registered: {}", bot.getBotUsername());
            return;
        }

        bot = createBot(botConfig);
        botsApi.registerBot(bot.getBotToken(), bot.getUpdatesConsumer());
        log.info("Bot registered: {} ({})", botConfig.getUsername(), botConfig.getToken());
    }

    private LongPollingTelegramBot createBot(BotConfig config) {
        return new LongPollingTelegramBot(config, updateRouter);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Initializing bot...");
        try {
            registerBot();
        } catch (Exception e) {
            log.error("Failed to initialize bot: {}", botConfig.getUsername(), e);
        }
        log.info("Bot initialized successfully: {}", botConfig.getUsername());
    }
}
