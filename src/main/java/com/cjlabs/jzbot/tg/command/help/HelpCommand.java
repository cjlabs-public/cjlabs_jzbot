package com.cjlabs.jzbot.tg.command.help;

import com.cjlabs.localbaby.tg.command.AbstractBotCommand;
import com.cjlabs.localbaby.tg.command.BotCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * /help 命令 - 显示帮助信息
 */
@Slf4j
@Component
public class HelpCommand extends AbstractBotCommand {
    
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

        BotCommand.getCommandMap().values()
                // .sorted(Comparator.comparing(BotCommand::getCommand))
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
