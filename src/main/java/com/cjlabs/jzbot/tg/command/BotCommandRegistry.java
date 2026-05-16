package com.cjlabs.jzbot.tg.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Bot 命令注册表。
 * Spring 启动时收集所有 BotCommand Bean，路由和帮助命令都从这里读取。
 */
@Slf4j
@Component
public class BotCommandRegistry {

    private final Map<String, BotCommand> commandMap;

    public BotCommandRegistry(List<BotCommand> commands) {
        Map<String, BotCommand> registeredCommands = new LinkedHashMap<>();
        commands.stream()
                .sorted(Comparator.comparing(BotCommand::getCommand))
                .forEach(command -> register(registeredCommands, command));
        this.commandMap = Collections.unmodifiableMap(registeredCommands);
        log.info("Registered {} bot commands", commandMap.size());
    }

    public Optional<BotCommand> find(String commandText) {
        return Optional.ofNullable(commandMap.get(normalize(commandText)));
    }

    public Collection<BotCommand> list() {
        return commandMap.values();
    }

    private void register(Map<String, BotCommand> commands, BotCommand command) {
        String key = normalize(command.getCommand());
        BotCommand previous = commands.put(key, command);
        if (previous != null) {
            throw new IllegalStateException("Duplicate bot command: " + key);
        }
        log.info("Registered command: {} -> {}", key, command.getClass().getSimpleName());
    }

    private String normalize(String commandText) {
        if (commandText == null || commandText.isBlank()) {
            return "";
        }
        return commandText.trim().toLowerCase(Locale.ROOT);
    }
}
