package com.cjlabs.jzbot.tg.core.router;

import com.cjlabs.localbaby.tg.command.AbstractBotCommand;
import com.cjlabs.localbaby.tg.command.BotCommand;
import com.cjlabs.localbaby.tg.core.BotManager;
import com.cjlabs.localbaby.tg.core.LongPollingTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 命令路由器
 * 负责将命令分发到对应的命令处理器
 */
@Slf4j
@Component
public class CommandRouter {
    @Lazy
    @Autowired
    private BotManager botManager;
    // @Autowired
    // private PermissionService permissionService;  // TODO: 实现权限服务
    // @Autowired
    // private GroupConfigService groupConfigService;
    // private final I18nHelper i18nHelper;  // TODO: 实现国际化工具
    // private final RateLimiter rateLimiter;  // TODO: 实现频率限制

    /**
     * 路由命令到对应的处理器
     */
    public void route(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage() || !update.getMessage().isCommand()) {
            return;
        }

        Message message = update.getMessage();
        String commandText = message.getText();
        String command = extractCommand(commandText);
        String[] args = extractArgs(commandText);

        BotCommand handler = BotCommand.getCommandMap().get(command);
        if (handler == null) {
            log.info("Unknown command: {}", command);
            return; // 未知命令，静默忽略
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        String chatType = message.getChat().getType();

        try {
            TelegramClient telegramClient = bot.getTelegramClient();

            // 如果是 AbstractBotCommand，注入 TelegramClient
            if (handler instanceof AbstractBotCommand) {
                ((AbstractBotCommand) handler).setTelegramClient(telegramClient);
            }

            // 执行命令
            handler.execute(update);

            log.info("Command executed: {} by user {} in {} chat {}", command, userId, chatType, chatId);

        } catch (Exception e) {
            log.error("Command execution failed: {} in chat {}", command, chatId, e);
        }
    }

    /**
     * 从命令中提取 bot username
     * 例如: "/start@mybot" -> "mybot"
     */
    private String extractBotUsername(String commandText) {
        if (commandText == null) {
            return null;
        }

        int atIndex = commandText.indexOf('@');
        if (atIndex < 0) {
            return null;
        }

        // 获取 @botname 之后的部分（去除参数）
        String remaining = commandText.substring(atIndex + 1);
        int spaceIndex = remaining.indexOf(' ');

        if (spaceIndex > 0) {
            return remaining.substring(0, spaceIndex);
        }

        return remaining.isEmpty() ? null : remaining;
    }

    /**
     * 提取命令名称（去除 @ 和参数）
     * 例如: "/start@botname arg1" -> "/start"
     */
    private String extractCommand(String commandText) {
        if (commandText == null || !commandText.startsWith("/")) {
            return "";
        }

        // 去除 @botname 部分
        int atIndex = commandText.indexOf('@');
        if (atIndex > 0) {
            commandText = commandText.substring(0, atIndex);
        }

        // 去除参数部分
        int spaceIndex = commandText.indexOf(' ');
        if (spaceIndex > 0) {
            commandText = commandText.substring(0, spaceIndex);
        }

        return commandText.toLowerCase();
    }

    /**
     * 提取命令参数
     * 例如: "/start arg1 arg2" -> ["arg1", "arg2"]
     */
    private String[] extractArgs(String commandText) {
        if (commandText == null) {
            return new String[0];
        }

        int spaceIndex = commandText.indexOf(' ');
        if (spaceIndex < 0) {
            return new String[0];
        }

        String argsText = commandText.substring(spaceIndex + 1).trim();
        if (argsText.isEmpty()) {
            return new String[0];
        }

        return argsText.split("\\s+");
    }

}