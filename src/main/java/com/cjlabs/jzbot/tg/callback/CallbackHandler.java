package com.cjlabs.jzbot.tg.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 回调处理器接口
 * 所有回调处理器都需要实现此接口
 */
public interface CallbackHandler {
    
    /**
     * 获取回调数据前缀（用于路由）
     * 例如: "lang:", "lottery:", "activity:"
     */
    String getCallbackPrefix();
    
    /**
     * 处理回调查询
     * 
     * @param callbackQuery 回调查询对象
     * @param client Telegram 客户端
     * @param data 回调数据（去除前缀后的部分）
     */
    void handle(CallbackQuery callbackQuery, TelegramClient client, String data);
    
    /**
     * 是否需要管理员权限
     */
    default boolean requiresAdmin() {
        return false;
    }
    
    /**
     * 是否仅限群组使用
     */
    default boolean groupOnly() {
        return false;
    }
}