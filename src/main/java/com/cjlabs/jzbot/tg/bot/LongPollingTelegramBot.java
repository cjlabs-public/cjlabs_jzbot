package com.cjlabs.jzbot.tg.bot;

import com.cjlabs.jzbot.tg.config.BotConfig;
import com.cjlabs.jzbot.tg.command.UpdateRouter;

import com.cjlabs.web.json.FmkJacksonUtil;
import com.cjlabs.web.util.http.ok.FmkOkHttpClientUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * 当前项目唯一的长轮询 Telegram bot。
 */
@Slf4j
public class LongPollingTelegramBot implements SpringLongPollingBot {
    @Getter
    protected final BotConfig botConfig;
    @Getter
    protected final String botUsername;

    protected final UpdateRouter updateRouter;
    @Getter
    protected final TelegramClient telegramClient;

    public LongPollingTelegramBot(BotConfig botConfig,
                                  UpdateRouter updateRouter) {
        this.botConfig = botConfig;
        this.botUsername = botConfig.getUsername();
        this.updateRouter = updateRouter;
        OkHttpClient okHttpClient = FmkOkHttpClientUtil.getClient();
        this.telegramClient = new OkHttpTelegramClient(okHttpClient, botConfig.getToken());
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this::consume;
    }

    /**
     * 处理 Update 列表
     */
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            try {
                log.info("LongPollingTelegramBot|consume|update={}", FmkJacksonUtil.toJson(update));
                handleUpdate(update);
            } catch (Exception e) {
                log.error("Error processing update for bot {}", botConfig.getUsername(), e);
            }
        }
    }

    private void handleUpdate(Update update) {
        updateRouter.route(update, this);
    }
}
