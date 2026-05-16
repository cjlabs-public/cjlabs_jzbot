package com.cjlabs.jzbot.tg.command;

import com.cjlabs.domain.enums.IEnumStr;

import com.cjlabs.localbaby.tg.core.BotManager;
import com.cjlabs.localbaby.tg.enums.ChatTypeEnum;
import com.cjlabs.localbaby.tg.service.message.TelegramMessageService;
import com.cjlabs.localbaby.tg.util.TelegramHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

/**
 * 命令处理器抽象基类
 */
@Slf4j
public abstract class AbstractBotCommand implements BotCommand {
    @Autowired
    protected TelegramMessageService messageService;
    @Autowired
    protected BotManager botManager;

    /**
     * -- SETTER --
     * 设置 TelegramClient（由 Router 调用）
     */
    @Setter
    protected TelegramClient telegramClient;

    @Override
    public final void execute(Update update) {
        // 前置检查
        if (!preCheck(update)) {
            return;
        }

        // 场景检查（群组/私聊限制）
        if (!checkChatType(update)) {
            handleWrongChatType(update);
            return;
        }

        // 权限检查
        if (!checkPermission(update)) {
            handleNoPermission(update);
            return;
        }

        try {
            // 执行命令(子类实现)
            doExecute(update);
        } catch (Exception e) {
            handleError(update, e);
        }
    }

    /**
     * 前置检查
     */
    protected boolean preCheck(Update update) {
        if (!update.hasMessage()) {
            log.info("Update has no message, skipping");
            return false;
        }

        if (telegramClient == null) {
            log.error("TelegramClient is null for command: {}", getCommand());
            return false;
        }

        return true;
    }

    /**
     * 检查聊天类型是否符合命令要求
     */
    protected ChatTypeEnum getChatType(Update update) {
        Chat chat = update.getMessage().getChat();
        String chatType = chat.getType();

        Optional<ChatTypeEnum> enumOptional = IEnumStr.getEnumByCode(chatType, ChatTypeEnum.class);

        if (enumOptional.isEmpty()) {
            log.info("AbstractBotCommand|getChatType|enumOptional is null");
            return null;
        }

        return enumOptional.get();
    }

    /**
     * 检查聊天类型是否符合命令要求
     */
    protected boolean checkChatType(Update update) {
        ChatTypeEnum chatType = getChatType(update);

        if (chatType == null) {
            log.warn("Unable to determine chat type for command: {}", getCommand());
            return false;
        }

        // 检查群组限制
        if (groupOnly() && chatType.isNotGroup()) {
            return false;
        }

        // 检查私聊限制
        if (privateOnly() && chatType.isNotPrivate()) {
            return false;
        }

        return true;
    }

    /**
     * 处理错误的聊天类型
     */
    protected void handleWrongChatType(Update update) {
        Long chatId = update.getMessage().getChatId();
        ChatTypeEnum chatType = getChatType(update);

        String errorMsg;
        if (groupOnly() && (chatType == null || !chatType.isGroup())) {
            errorMsg = "此命令只能在群组中使用";
            log.info("Command {} requires group chat, but called in {}", getCommand(), chatType);
        } else if (privateOnly() && (chatType == null || !chatType.isPrivate())) {
            errorMsg = "此命令只能在私聊中使用";
            log.info("Command {} requires private chat, but called in {}", getCommand(), chatType);
        } else {
            return;
        }

        messageService.sendErrorMessage(telegramClient, chatId, errorMsg);
    }

    /**
     * 权限检查
     */
    protected boolean checkPermission(Update update) {
        // TODO: 实现权限检查逻辑
        // if (requiresAdmin() && !permissionService.isAdmin(chatId, userId)) {
        //     return false;
        // }
        return true;
    }


    /**
     * 处理无权限情况
     */
    protected void handleNoPermission(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        String errorMsg = "您没有权限执行此命令";
        messageService.sendErrorMessage(telegramClient, chatId, errorMsg);

        log.warn("User {} attempted to execute {} without permission in chat {}", userId, getCommand(), chatId);
    }

    /**
     * 执行命令的具体逻辑（子类实现）
     */
    protected abstract void doExecute(Update update) throws Exception;

    /**
     * 错误处理
     */
    protected void handleError(Update update, Exception e) {
        log.error("命令执行失败: {}", getCommand(), e);

        Long chatId = update.getMessage().getChatId();
        String errorMsg = "命令执行失败，请稍后重试";

        // 开发环境可以显示详细错误
        if (log.isDebugEnabled()) {
            errorMsg += ": " + e.getMessage();
        }

        messageService.sendErrorMessage(telegramClient, chatId, errorMsg);
    }

    /**
     * 获取命令参数
     */
    protected String[] getArgs(Update update) {
        return TelegramHelper.extractCommandArgs(update.getMessage());
    }

    /**
     * 获取第一个参数
     */
    protected String getFirstArg(Update update) {
        String[] args = getArgs(update);
        return args.length > 0 ? args[0] : null;
    }

}