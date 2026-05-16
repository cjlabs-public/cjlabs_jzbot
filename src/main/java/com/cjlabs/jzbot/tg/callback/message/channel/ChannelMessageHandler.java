package com.cjlabs.jzbot.tg.callback.message.channel;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * 频道消息处理器。
 */
@Slf4j
@Component
public class ChannelMessageHandler {

    public void handle(Message message, LongPollingTelegramBot bot) {
        log.info("Channel message from channel {}", message.getChatId());
    }
}
