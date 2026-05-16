
package com.cjlabs.jzbot.tg.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bot 命令自动注册器
 * 在 Spring 应用启动完成后，将所有 BotCommand Bean 自动注册到 COMMAND_MAP
 */
@Slf4j
@Component
public class BotCommandRegistrar implements ApplicationListener<ApplicationReadyEvent> {

    private final List<BotCommand> allBotCommandList;

    public BotCommandRegistrar(List<BotCommand> allBotCommandList) {
        this.allBotCommandList = allBotCommandList;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 清空旧的注册（如果有的话）
        BotCommand.COMMAND_MAP.clear();

        // 注册所有 BotCommand Bean
        for (BotCommand command : allBotCommandList) {
            command.register();
            log.info("✅ Registered command: {} -> {}", command.getCommand(), command.getClass().getSimpleName());
        }

        log.info("📊 Total commands registered: {}", BotCommand.COMMAND_MAP.size());

        // 打印已注册的命令列表（调试用）
        if (log.isDebugEnabled()) {
            log.info("=== Registered Bot Commands ===");
            BotCommand.COMMAND_MAP.forEach((cmd, handler) ->
                    log.info("  {} - {} ({})", cmd, handler.getDescription(), handler.getClass().getSimpleName())
            );
        }
    }
}
