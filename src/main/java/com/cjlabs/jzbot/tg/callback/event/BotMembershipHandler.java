package com.cjlabs.jzbot.tg.callback.event;

import com.cjlabs.jzbot.tg.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Bot 自身在群里的成员状态处理。
 * 例如 bot 被加入群、被移除、权限变化。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BotMembershipHandler {

    private final TelegramMessageService messageService;

    public void handle(Update update, TelegramClient telegramClient) {
        if (!update.hasMyChatMember()) {
            return;
        }

        try {
            ChatMemberUpdated myChatMember = update.getMyChatMember();
            Chat chat = myChatMember.getChat();
            Long chatId = chat.getId();

            String oldStatus = myChatMember.getOldChatMember().getStatus();
            ChatMember newChatMember = myChatMember.getNewChatMember();
            String newStatus = newChatMember.getStatus();

            log.info("Bot status changed in chat {}: {} -> {}", chatId, oldStatus, newStatus);

            if (isBotAdded(oldStatus, newStatus)) {
                handleBotAdded(chatId, chat, newChatMember, telegramClient);
            } else if (isBotRemoved(oldStatus, newStatus)) {
                handleBotRemoved(chatId, chat);
            } else if (isBotPermissionsChanged(oldStatus, newStatus)) {
                handleBotPermissionsChanged(chatId, newStatus);
            }
        } catch (Exception e) {
            log.error("Error handling bot chat member update", e);
        }
    }

    private boolean isBotAdded(String oldStatus, String newStatus) {
        return isInactiveStatus(oldStatus) && isActiveStatus(newStatus);
    }

    private boolean isBotRemoved(String oldStatus, String newStatus) {
        return isActiveStatus(oldStatus) && ("left".equals(newStatus) || "kicked".equals(newStatus));
    }

    private boolean isBotPermissionsChanged(String oldStatus, String newStatus) {
        return isActiveStatus(oldStatus) && isActiveStatus(newStatus) && !oldStatus.equals(newStatus);
    }

    private boolean isActiveStatus(String status) {
        return "member".equals(status) || "administrator".equals(status) || "restricted".equals(status);
    }

    private boolean isInactiveStatus(String status) {
        return "left".equals(status) || "kicked".equals(status) || "creator".equals(status);
    }

    private void handleBotAdded(Long chatId, Chat chat, ChatMember newChatMember, TelegramClient telegramClient) {
        log.info("Bot successfully added to chat: chatId={}, chatTitle={}, status={}",
                chatId, chat.getTitle(), newChatMember.getStatus());

        Chat detailedChat = fetchDetailedChatInfo(telegramClient, chatId);
        activateChatBinding(chatId);
        saveChatInfo(chatId, detailedChat != null ? detailedChat : chat);
        if (detailedChat != null) {
            logChatStatistics(chatId, detailedChat);
        }
        if (telegramClient != null) {
            sendWelcomeMessageToChat(telegramClient, chatId, newChatMember);
        }
    }

    private void handleBotRemoved(Long chatId, Chat chat) {
        log.warn("Bot removed from chat: chatId={}, chatTitle={}", chatId, chat.getTitle());
        deactivateChatBinding(chatId);
        clearChatData(chatId);
    }

    private void handleBotPermissionsChanged(Long chatId, String newStatus) {
        log.info("Bot permissions changed in chat: chatId={}, newStatus={}", chatId, newStatus);
        updateBotPermissions(chatId, newStatus);
    }

    private Chat fetchDetailedChatInfo(TelegramClient telegramClient, Long chatId) {
        if (telegramClient == null) {
            return null;
        }

        try {
            GetChat getChat = GetChat.builder()
                    .chatId(chatId)
                    .build();
            Chat detailedChat = telegramClient.execute(getChat);
            log.info("Fetched chat info: chatId={}, title={}, type={}, username={}",
                    chatId,
                    detailedChat.getTitle(),
                    detailedChat.getType(),
                    detailedChat.getUserName());
            return detailedChat;
        } catch (Exception e) {
            log.warn("Failed to fetch detailed chat info for chatId: {}", chatId, e);
            return null;
        }
    }

    private void activateChatBinding(Long chatId) {
        log.info("Activating chat binding for chatId: {}", chatId);
    }

    private void deactivateChatBinding(Long chatId) {
        log.info("Deactivating chat binding for chatId: {}", chatId);
    }

    private void saveChatInfo(Long chatId, Chat chat) {
        log.info("Saving chat info: chatId={}, title={}, type={}", chatId, chat.getTitle(), chat.getType());
    }

    private void clearChatData(Long chatId) {
        log.info("Clearing chat data for chatId: {}", chatId);
    }

    private void updateBotPermissions(Long chatId, String newStatus) {
        log.info("Updating bot permissions for chatId: {}, status={}", chatId, newStatus);
    }

    private void logChatStatistics(Long chatId, Chat chat) {
        log.info("Chat statistics: chatId={}, title={}, type={}, username={}",
                chatId,
                chat.getTitle(),
                chat.getType(),
                chat.getUserName());
    }

    private void sendWelcomeMessageToChat(TelegramClient telegramClient, Long chatId, ChatMember newChatMember) {
        String message = """
                👋 <b>欢迎使用本机器人！</b>

                ✅ <b>机器人已成功添加到群组</b>
                📌 <b>群组 ID</b>: <code>%d</code>
                📌 <b>机器人名称</b>: <code>%s</code>

                💡 <b>可用命令</b>
                • /help - 查看帮助信息
                """.formatted(chatId, newChatMember.getUser().getUserName());

        messageService.sendHtml(telegramClient, chatId, message);
        log.info("Welcome message sent to chat: {}", chatId);
    }
}
