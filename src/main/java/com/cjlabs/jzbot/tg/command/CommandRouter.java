package com.cjlabs.jzbot.tg.command;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import com.cjlabs.jzbot.tg.common.enums.ChatTypeEnum;
import com.cjlabs.jzbot.tg.callback.message.channel.ChannelMessageHandler;
import com.cjlabs.jzbot.tg.callback.message.group.GroupMessageHandler;
import com.cjlabs.jzbot.tg.callback.message.privatechat.PrivateMessageHandler;
import com.cjlabs.jzbot.tg.util.TelegramHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 命令路由器。
 * 只处理 /start、/help 这种斜杠命令。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandRouter {

    private final BotCommandRegistry commandRegistry;

    public void route(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage() || !update.getMessage().isCommand()) {
            return;
        }

        Message message = update.getMessage();
        String command = TelegramHelper.extractCommand(message);
        BotCommand handler = commandRegistry.find(command).orElse(null);
        if (handler == null) {
            log.info("Unknown command: {}", command);
            return;
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        String chatType = message.getChat().getType();

        try {
            TelegramClient telegramClient = bot.getTelegramClient();
            if (handler instanceof AbstractBotCommand) {
                ((AbstractBotCommand) handler).setTelegramClient(telegramClient);
            }

            handler.execute(update);
            log.info("Command executed: {} by user {} in {} chat {}", command, userId, chatType, chatId);
        } catch (Exception e) {
            log.error("Command execution failed: {} in chat {}", command, chatId, e);
        }
    }
}
