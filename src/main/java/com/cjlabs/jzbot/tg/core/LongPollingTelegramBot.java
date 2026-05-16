package com.cjlabs.jzbot.tg.core;

import com.cjlabs.localbaby.tg.config.BotConfig;
import com.cjlabs.localbaby.tg.core.router.UpdateRouter;

import com.cjlabs.web.json.FmkJacksonUtil;
import com.cjlabs.web.util.http.ok.FmkOkHttpClientUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.List;

/**
 * 抽象长轮询机器人基类 (TelegramBots 9.2.0)
 * 所有具体的 Bot 实现都应继承此类
 */
@Slf4j
public abstract class LongPollingTelegramBot implements SpringLongPollingBot {
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

    /**
     * 子类实现具体的更新处理逻辑
     * 将当前 bot 实例传递给 UpdateRouter
     */
    protected void handleUpdate(Update update) {
        // 关键：将当前 bot 实例传递给 UpdateRouter
        updateRouter.route(update, this);
    }

    /**
     * 发送消息的便利方法
     */
    protected void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        execute(message);
    }

    /**
     * 执行 Telegram API 方法
     * 使用 BotApiMethod 接口，支持所有 Telegram API 方法
     */
    protected <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        try {
            return telegramClient.execute(method);
        } catch (TelegramApiException e) {
            log.error("Failed to execute method for bot {}", botConfig.getBotName(), e);
            throw e;
        }
    }
}