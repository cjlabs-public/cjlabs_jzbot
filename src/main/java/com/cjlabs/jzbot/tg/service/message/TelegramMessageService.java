package com.cjlabs.jzbot.tg.service.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Telegram 消息发送服务
 * 统一处理所有消息发送逻辑
 */
@Slf4j
@Service
public class TelegramMessageService {

    /**
     * 发送普通文本消息
     */
    public void sendMessage(TelegramClient client, Long chatId, String text) {
        sendMessage(client, chatId, text, null, null);
    }

    /**
     * 发送带键盘的消息
     */
    public void sendMessageWithKeyboard(TelegramClient client,
                                        Long chatId,
                                        String text,
                                        ReplyKeyboard keyboard) {
        sendMessage(client, chatId, text, null, keyboard);
    }

    /**
     * 发送 Markdown 格式消息
     */
    public void sendMarkdown(TelegramClient client, Long chatId, String text) {
        sendMessage(client, chatId, text, ParseMode.MARKDOWN, null);
    }

    /**
     * 发送 HTML 格式消息
     */
    public void sendHtml(TelegramClient client, Long chatId, String text) {
        sendMessage(client, chatId, text, ParseMode.HTML, null);
    }

    /**
     * 发送错误消息
     */
    public void sendErrorMessage(TelegramClient client, Long chatId, String errorMsg) {
        String message = "❌ " + errorMsg;
        sendMessage(client, chatId, message, null, null);
    }

    /**
     * 发送成功消息
     */
    public void sendSuccessMessage(TelegramClient client, Long chatId, String successMsg) {
        String message = "✅ " + successMsg;
        sendMessage(client, chatId, message, null, null);
    }

    /**
     * 发送警告消息
     */
    public void sendWarningMessage(TelegramClient client, Long chatId, String warningMsg) {
        String message = "⚠️ " + warningMsg;
        sendMessage(client, chatId, message, null, null);
    }

    /**
     * 发送信息消息
     */
    public void sendInfoMessage(TelegramClient client, Long chatId, String infoMsg) {
        String message = "ℹ️ " + infoMsg;
        sendMessage(client, chatId, message, null, null);
    }

    /**
     * 删除消息
     */
    public void deleteMessage(TelegramClient client, Long chatId, Integer messageId) {
        if (client == null) {
            log.warn("Cannot delete message: TelegramClient is null");
            return;
        }

        try {
            DeleteMessage delete = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();
            client.execute(delete);
        } catch (TelegramApiException e) {
            log.error("Failed to delete message: chatId={}, messageId={}",
                    chatId, messageId, e);
        }
    }

    /**
     * 核心发送方法
     */
    private void sendMessage(TelegramClient client, Long chatId, String text,
                             String parseMode, ReplyKeyboard keyboard) {
        if (client == null) {
            log.warn("Cannot send message: TelegramClient is null");
            return;
        }

        if (text == null || text.trim().isEmpty()) {
            log.warn("Cannot send empty message");
            return;
        }

        try {
            SendMessage.SendMessageBuilder builder = SendMessage.builder()
                    .chatId(chatId)
                    .text(text);

            if (parseMode != null) {
                builder.parseMode(parseMode);
            }

            if (keyboard != null) {
                builder.replyMarkup(keyboard);
            }

            client.execute(builder.build());
            log.info("Message sent to chatId={}", chatId);

        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}: {}", chatId, e.getMessage(), e);
        }
    }
}