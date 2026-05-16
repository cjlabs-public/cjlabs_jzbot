package com.cjlabs.jzbot.tg.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回调路由器
 * 根据回调数据前缀将请求路由到相应的处理器
 * 支持权限检查和错误处理
 */
@Slf4j
@Component
public class CallbackRouter {
    
    private final Map<String, CallbackHandler> handlers = new HashMap<>();
    
    /**
     * 构造函数 - 自动注入所有 CallbackHandler 实现
     */
    public CallbackRouter(List<CallbackHandler> handlerList) {
        for (CallbackHandler handler : handlerList) {
            handlers.put(handler.getCallbackPrefix(), handler);
            log.info("Registered callback handler: {} (prefix: {})", 
                    handler.getClass().getSimpleName(), 
                    handler.getCallbackPrefix());
        }
        log.info("Total {} callback handlers registered", handlers.size());
    }
    
    /**
     * 路由回调查询到相应的处理器
     * 注意：此方法从 UpdateRouter 调用，不需要 TelegramClient 参数
     */
    public void route(Update update, TelegramClient telegramClient) {
        if (!update.hasCallbackQuery()) {
            return;
        }
        
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        Long userId = callbackQuery.getFrom().getId();
        Long chatId = callbackQuery.getMessage() != null ? 
                callbackQuery.getMessage().getChatId() : null;
        
        log.info("Processing callback from user {} in chat {}: {}", userId, chatId, data);
        
        if (data == null || data.isEmpty()) {
            log.warn("Empty callback data received from user {}", userId);
            answerCallbackError(telegramClient, callbackQuery.getId(), "无效的回调数据");
            return;
        }
        
        // 查找匹配的处理器
        CallbackHandler handler = findHandler(data);
        if (handler == null) {
            log.warn("No handler found for callback data: {} from user {}", data, userId);
            answerCallbackError(telegramClient, callbackQuery.getId(), "未找到对应的处理器");
            return;
        }
        
        // 检查群组限制
        if (handler.groupOnly() && !isGroupChat(callbackQuery)) {
            log.warn("Handler {} requires group chat, but received from private chat", 
                    handler.getCallbackPrefix());
            answerCallbackError(telegramClient, callbackQuery.getId(), "此功能仅限群组使用");
            return;
        }
        
        // TODO: 检查管理员权限（需要实现权限检查服务）
        if (handler.requiresAdmin()) {
            log.info("Handler {} requires admin permission", handler.getCallbackPrefix());
            // 这里应该调用权限检查服务
            // if (!permissionService.isAdmin(chatId, userId)) {
            //     answerCallbackError(null, callbackQuery.getId(), "需要管理员权限");
            //     return;
            // }
        }
        
        // 提取实际数据（去除前缀）
        String prefix = handler.getCallbackPrefix();
        String actualData = data.substring(prefix.length());
        
        try {
            handler.handle(callbackQuery, telegramClient, actualData);
            log.info("Successfully handled callback: {}", data);
        } catch (Exception e) {
            log.error("Error handling callback: {} from user {}", data, userId, e);
            answerCallbackError(telegramClient, callbackQuery.getId(),
                    "处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据回调数据查找对应的处理器
     */
    private CallbackHandler findHandler(String data) {
        for (Map.Entry<String, CallbackHandler> entry : handlers.entrySet()) {
            if (data.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * 检查是否为群组聊天
     */
    private boolean isGroupChat(CallbackQuery query) {
        if (query.getMessage() == null) {
            return false;
        }
        String chatType = query.getMessage().getChat().getType();
        return "group".equals(chatType) || "supergroup".equals(chatType);
    }
    
    /**
     * 回答回调查询（错误消息）
     */
    private void answerCallbackError(TelegramClient client, String callbackQueryId, String errorMsg) {
        if (client == null) {
            log.warn("Cannot answer callback query: TelegramClient is null");
            return;
        }

        try {
            org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery answer =
                    org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery.builder()
                            .callbackQueryId(callbackQueryId)
                            .text("❌ " + errorMsg)
                            .showAlert(true)
                            .build();
            client.execute(answer);
        } catch (Exception e) {
            log.error("Failed to answer callback query with error", e);
        }
    }
    
    /**
     * 获取所有已注册的处理器前缀
     */
    public Map<String, CallbackHandler> getHandlers() {
        return new HashMap<>(handlers);
    }
}
