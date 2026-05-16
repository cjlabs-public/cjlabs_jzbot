package com.cjlabs.jzbot.tg.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 机器人命令接口
 */
public interface BotCommand {

    /**
     * 获取命令名称（如 /start）
     */
    String getCommand();

    /**
     * 获取命令描述
     */
    String getDescription();

    /**
     * 是否需要管理员权限
     */
    default boolean requiresAdmin() {
        return false;
    }

    /**
     * 是否只能在群组使用
     */
    default boolean groupOnly() {
        return false;
    }

    /**
     * 是否只能在私聊使用
     */
    default boolean privateOnly() {
        return false;
    }

    /**
     * 执行命令
     */
    void execute(Update update);

    /**
     * 获取命令使用说明
     */
    default String getUsage() {
        return getCommand() + " - " + getDescription();
    }
}
