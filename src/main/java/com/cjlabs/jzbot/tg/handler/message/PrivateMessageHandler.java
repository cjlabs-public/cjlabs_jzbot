package com.cjlabs.jzbot.tg.handler.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 私聊消息处理器
 * 处理私聊中的非命令消息
 */
@Slf4j
@Component
public class PrivateMessageHandler {

    /**
     * 处理私聊消息
     */
    public void handle(Update update) {
        if (!update.hasMessage() || update.getMessage().getText() == null) {
            return;
        }
        
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();

        log.info("Processing private message from user {}", userId);

        // TODO: 实现私聊消息处理逻辑
        // 例如：客服对话、用户咨询等
    }
}

