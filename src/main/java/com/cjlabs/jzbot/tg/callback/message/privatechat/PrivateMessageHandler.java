package com.cjlabs.jzbot.tg.callback.message.privatechat;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * 私聊普通消息处理器。
 * 用户直接给 bot 发文本，且不是 /command 时，会走到这里。
 */
@Slf4j
@Component
public class PrivateMessageHandler {

    public void handle(Message message, LongPollingTelegramBot bot) {
        if (message.getText() == null) {
            return;
        }

        log.info("Private message from user {}", message.getFrom().getId());
    }
}
