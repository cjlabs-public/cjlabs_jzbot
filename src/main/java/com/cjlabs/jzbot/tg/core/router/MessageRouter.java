package com.cjlabs.jzbot.tg.core.router;

import com.cjlabs.domain.enums.IEnumStr;

import com.cjlabs.localbaby.tg.core.LongPollingTelegramBot;
import com.cjlabs.localbaby.tg.enums.ChatTypeEnum;
import com.cjlabs.localbaby.tg.handler.message.ChannelMessageHandler;
import com.cjlabs.localbaby.tg.handler.message.GroupMessageHandler;
import com.cjlabs.localbaby.tg.handler.message.PrivateMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;

import java.util.Optional;

/**
 * 消息路由器
 * 根据聊天类型将消息路由到不同的处理器
 */
@Slf4j
@Component
public class MessageRouter {

    @Autowired
    private GroupMessageHandler groupMessageHandler;

    @Autowired
    private PrivateMessageHandler privateMessageHandler;

    @Autowired
    private ChannelMessageHandler channelMessageHandler;

    /**
     * 路由普通消息到对应的处理器
     */
    public void route(Update update, LongPollingTelegramBot bot) {
        // 处理频道消息
        if (update.hasChannelPost()) {
            channelMessageHandler.handle(update);
            return;
        }

        // 处理普通消息
        if (!update.hasMessage()) {
            return;
        }

        Chat chat = update.getMessage().getChat();
        String chatType = chat.getType();

        Optional<ChatTypeEnum> enumOptional = IEnumStr.getEnumByCode(chatType, ChatTypeEnum.class);

        if (enumOptional.isEmpty()) {
            log.info("MessageRouter|route|enumOptional is null");
            return;
        }

        ChatTypeEnum chatTypeEnum = enumOptional.get();

        // 根据聊天类型路由到不同的处理器
        switch (chatTypeEnum) {
            case ChatTypeEnum.PRIVATE -> {
                log.info("Routing to PrivateMessageHandler");
                privateMessageHandler.handle(update);
            }
            case ChatTypeEnum.GROUP, ChatTypeEnum.SUPERGROUP -> {
                log.info("Routing to GroupMessageHandler");
                groupMessageHandler.handle(update);
            }
            case ChatTypeEnum.CHANNEL -> {
                log.info("Routing to ChannelMessageHandler");
                channelMessageHandler.handle(update);
            }
            default -> log.warn("Unknown chat type: {}", chatType);
        }
    }
}