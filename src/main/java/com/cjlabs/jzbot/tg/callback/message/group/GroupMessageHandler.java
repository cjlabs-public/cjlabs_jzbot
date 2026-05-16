package com.cjlabs.jzbot.tg.callback.message.group;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * 群聊普通消息处理器。
 * 群成员在群里发普通文本，且不是 /command 时，会走到这里。
 */
@Slf4j
@Component
public class GroupMessageHandler {

    public void handle(Message message, LongPollingTelegramBot bot) {
        if (message.getText() == null) {
            return;
        }

        log.info("Group message from user {} in chat {}", message.getFrom().getId(), message.getChatId());
    }
}
