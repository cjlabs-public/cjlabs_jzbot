package com.cjlabs.jzbot.tg.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 回调处理器抽象基类
 * 提供通用的辅助方法和工具函数
 */
@Slf4j
public abstract class AbstractCallbackHandler implements CallbackHandler {
    
    /**
     * 回答回调查询（显示提示消息）
     */
    protected void answerCallback(TelegramClient client, String callbackQueryId, String text) {
        answerCallback(client, callbackQueryId, text, false);
    }
    
    /**
     * 回答回调查询
     * 
     * @param showAlert 是否显示为警告框（true=弹窗，false=顶部提示）
     */
    protected void answerCallback(TelegramClient client, String callbackQueryId, 
                                 String text, boolean showAlert) {
        if (client == null) {
            log.warn("Cannot answer callback: TelegramClient is null");
            return;
        }
        
        try {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .showAlert(showAlert)
                    .build();
            client.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Failed to answer callback query: {}", callbackQueryId, e);
        }
    }
    
    /**
     * 编辑消息文本
     */
    protected void editMessageText(TelegramClient client, Long chatId, 
                                   Integer messageId, String newText) {
        editMessageText(client, chatId, messageId, newText, null);
    }
    
    /**
     * 编辑消息文本和键盘
     */
    protected void editMessageText(TelegramClient client, Long chatId, 
                                   Integer messageId, String newText, 
                                   InlineKeyboardMarkup keyboard) {
        if (client == null) {
            log.warn("Cannot edit message: TelegramClient is null");
            return;
        }
        
        try {
            EditMessageText.EditMessageTextBuilder builder = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(newText);
            
            if (keyboard != null) {
                builder.replyMarkup(keyboard);
            }
            
            client.execute(builder.build());
        } catch (TelegramApiException e) {
            log.error("Failed to edit message text: chatId={}, messageId={}", 
                    chatId, messageId, e);
        }
    }
    
    /**
     * 仅编辑消息的键盘（不改变文本）
     */
    protected void editMessageKeyboard(TelegramClient client, Long chatId, 
                                      Integer messageId, InlineKeyboardMarkup keyboard) {
        if (client == null) {
            log.warn("Cannot edit keyboard: TelegramClient is null");
            return;
        }
        
        try {
            EditMessageReplyMarkup edit = EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .replyMarkup(keyboard)
                    .build();
            client.execute(edit);
        } catch (TelegramApiException e) {
            log.error("Failed to edit message keyboard: chatId={}, messageId={}", 
                    chatId, messageId, e);
        }
    }
    
    /**
     * 删除消息的键盘
     */
    protected void removeKeyboard(TelegramClient client, Long chatId, Integer messageId) {
        editMessageKeyboard(client, chatId, messageId, null);
    }
    
    /**
     * 发送错误消息（弹窗）
     */
    protected void sendError(TelegramClient client, CallbackQuery query, String errorMsg) {
        answerCallback(client, query.getId(), "❌ " + errorMsg, true);
    }
    
    /**
     * 发送成功消息（顶部提示）
     */
    protected void sendSuccess(TelegramClient client, CallbackQuery query, String successMsg) {
        answerCallback(client, query.getId(), "✅ " + successMsg, false);
    }
    
    /**
     * 发送警告消息（弹窗）
     */
    protected void sendWarning(TelegramClient client, CallbackQuery query, String warningMsg) {
        answerCallback(client, query.getId(), "⚠️ " + warningMsg, true);
    }
    
    /**
     * 发送信息消息（顶部提示）
     */
    protected void sendInfo(TelegramClient client, CallbackQuery query, String infoMsg) {
        answerCallback(client, query.getId(), "ℹ️ " + infoMsg, false);
    }
    
    /**
     * 检查是否为群组聊天
     */
    protected boolean isGroupChat(CallbackQuery query) {
        if (query.getMessage() == null) {
            return false;
        }
        String chatType = query.getMessage().getChat().getType();
        return "group".equals(chatType) || "supergroup".equals(chatType);
    }
    
    /**
     * 检查是否为私聊
     */
    protected boolean isPrivateChat(CallbackQuery query) {
        if (query.getMessage() == null) {
            return false;
        }
        return "private".equals(query.getMessage().getChat().getType());
    }
    
    /**
     * 检查是否为频道
     */
    protected boolean isChannel(CallbackQuery query) {
        if (query.getMessage() == null) {
            return false;
        }
        return "channel".equals(query.getMessage().getChat().getType());
    }
    
    /**
     * 获取用户ID
     */
    protected Long getUserId(CallbackQuery query) {
        return query.getFrom().getId();
    }
    
    /**
     * 获取用户名
     */
    protected String getUsername(CallbackQuery query) {
        return query.getFrom().getUserName();
    }
    
    /**
     * 获取用户显示名称
     */
    protected String getUserDisplayName(CallbackQuery query) {
        String firstName = query.getFrom().getFirstName();
        String lastName = query.getFrom().getLastName();
        if (lastName != null && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName;
    }
    
    /**
     * 获取聊天ID
     */
    protected Long getChatId(CallbackQuery query) {
        return query.getMessage() != null ? query.getMessage().getChatId() : null;
    }
    
    /**
     * 获取消息ID
     */
    protected Integer getMessageId(CallbackQuery query) {
        return query.getMessage() != null ? query.getMessage().getMessageId() : null;
    }
    
    /**
     * 获取聊天标题
     */
    protected String getChatTitle(CallbackQuery query) {
        return query.getMessage() != null ? query.getMessage().getChat().getTitle() : null;
    }
    
    /**
     * 解析回调数据中的参数
     * 例如: "lottery:join:123" -> ["lottery", "join", "123"]
     */
    protected String[] parseCallbackData(String data, String delimiter) {
        if (data == null || data.isEmpty()) {
            return new String[0];
        }
        return data.split(delimiter);
    }
    
    /**
     * 验证回调数据格式
     */
    protected boolean validateCallbackData(String data, int expectedParts, String delimiter) {
        String[] parts = parseCallbackData(data, delimiter);
        return parts.length == expectedParts;
    }
    
    /**
     * 安全地解析Long类型参数
     */
    protected Long parseLongSafely(String value, Long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse long value: {}", value);
            return defaultValue;
        }
    }
    
    /**
     * 安全地解析Integer类型参数
     */
    protected Integer parseIntSafely(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse int value: {}", value);
            return defaultValue;
        }
    }
}
