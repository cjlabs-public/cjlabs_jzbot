package com.cjlabs.jzbot.tg.command;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import com.cjlabs.jzbot.tg.callback.message.channel.ChannelMessageHandler;
import com.cjlabs.jzbot.tg.callback.message.group.GroupMessageHandler;
import com.cjlabs.jzbot.tg.callback.message.privatechat.PrivateMessageHandler;
import com.cjlabs.jzbot.tg.common.enums.ChatTypeEnum;
import com.cjlabs.jzbot.tg.util.TelegramHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 普通消息路由器。
 * 命令和按钮回调不会走这里，只处理用户直接发给 bot 或群里的普通消息。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRouter {

    private final PrivateMessageHandler privateMessageHandler;
    private final GroupMessageHandler groupMessageHandler;
    private final ChannelMessageHandler channelMessageHandler;

    public void route(Update update, LongPollingTelegramBot bot) {
        if (update.hasChannelPost()) {
            channelMessageHandler.handle(update.getChannelPost(), bot);
            return;
        }

        if (!update.hasMessage()) {
            return;
        }

        ChatTypeEnum chatType = TelegramHelper.getChatType(update);
        if (chatType == null) {
            log.info("Unknown message chat type");
            return;
        }

        switch (chatType) {
            case PRIVATE -> privateMessageHandler.handle(update.getMessage(), bot);
            case GROUP, SUPERGROUP -> groupMessageHandler.handle(update.getMessage(), bot);
            case CHANNEL -> channelMessageHandler.handle(update.getMessage(), bot);
        }
    }
}