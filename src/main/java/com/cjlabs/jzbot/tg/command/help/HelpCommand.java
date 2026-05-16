package com.cjlabs.jzbot.tg.command.help;

import com.cjlabs.jzbot.tg.command.AbstractBotCommand;
import com.cjlabs.jzbot.tg.command.BotCommandRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * /help 命令 - 显示帮助信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HelpCommand extends AbstractBotCommand {

    private final ObjectProvider<BotCommandRegistry> commandRegistryProvider;
    
    @Override
    public String getCommand() {
        return "/help";
    }
    
    @Override
    public String getDescription() {
        return "显示帮助信息";
    }
    
    @Override
    protected void doExecute(Update update) {
        Long chatId = update.getMessage().getChatId();
        
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("📚 可用命令列表\n\n");

        commandRegistryProvider.getObject().list()
                .forEach(cmd ->
                        helpMessage.append(String.format("  %s - %s\n", cmd.getCommand(), cmd.getDescription()))
                );

        helpMessage.append("\n");
        
        helpMessage.append("💡 提示：点击命令可直接使用");
        
        // TODO: 发送帮助消息
        messageService.sendMessage(telegramClient, chatId, helpMessage.toString());

        log.info("Help command executed in chat {}", chatId);
    }
}
