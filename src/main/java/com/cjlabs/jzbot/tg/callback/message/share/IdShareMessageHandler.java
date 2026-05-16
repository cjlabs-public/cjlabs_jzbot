package com.cjlabs.jzbot.tg.callback.message.share;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import com.cjlabs.jzbot.tg.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserShared;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Telegram ID 分享消息处理器。
 * 处理 keyboard button 触发的 user_shared 和 chat_shared。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdShareMessageHandler {

    private final TelegramMessageService messageService;

    public void handleUserShared(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage() || update.getMessage().getUserShared() == null) {
            return;
        }

        Long fromUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        UserShared userShared = update.getMessage().getUserShared();
        Long sharedUserId = userShared.getUserId();
        String username = userShared.getUsername();

        log.info("User shared: fromUserId={}, chatId={}, sharedUserId={}", fromUserId, chatId, sharedUserId);

        User sharedUser = getUserInfoFromChat(bot.getTelegramClient(), sharedUserId, chatId);
        if (sharedUser == null) {
            sharedUser = buildSharedUserFallback(sharedUserId, username);
        }

        String userType = sharedUser.getIsBot() ? "BOT" : "USER";
        saveUserShareRecord(sharedUser, userType, chatId);
        sendUserShareConfirmation(bot.getTelegramClient(), chatId, sharedUser, userType);
    }

    public void handleChatShared(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage() || update.getMessage().getChatShared() == null) {
            return;
        }

        Long userId = update.getMessage().getFrom().getId();
        Long privateChatId = update.getMessage().getChatId();
        Long sharedChatId = update.getMessage().getChatShared().getChatId();
        String requestId = update.getMessage().getChatShared().getRequestId();

        log.info("Chat shared: userId={}, privateChatId={}, sharedChatId={}, requestId={}",
                userId, privateChatId, sharedChatId, requestId);

        Chat sharedChat = fetchChatInfo(bot.getTelegramClient(), sharedChatId);
        saveChatShareRecord(userId, sharedChatId, requestId, sharedChat);
        sendChatShareConfirmation(bot.getTelegramClient(), privateChatId, sharedChatId, sharedChat, requestId);
    }

    private User buildSharedUserFallback(Long userId, String username) {
        User user = new User(userId, null, false);
        user.setUserName(username);
        return user;
    }

    private User getUserInfoFromChat(TelegramClient telegramClient, Long userId, Long chatId) {
        try {
            GetChatMember getChatMember = GetChatMember.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .build();

            ChatMember chatMember = telegramClient.execute(getChatMember);
            return chatMember.getUser();
        } catch (Exception e) {
            log.warn("Cannot get user info from chat: {}", e.getMessage());
            return null;
        }
    }

    private Chat fetchChatInfo(TelegramClient telegramClient, Long chatId) {
        try {
            GetChat getChat = GetChat.builder()
                    .chatId(chatId)
                    .build();
            Chat chat = telegramClient.execute(getChat);
            log.info("Fetched chat info: id={}, title={}, type={}", chat.getId(), chat.getTitle(), chat.getType());
            return chat;
        } catch (TelegramApiException e) {
            log.warn("Failed to get chat info for chatId: {} (bot may not be a member)", chatId);
            log.info("Error details: {}", e.getMessage());
            return null;
        }
    }

    private void saveUserShareRecord(User user, String userType, Long fromChatId) {
        log.info("Saving user share record: userId={}, username={}, userType={}, fromChatId={}",
                user.getId(), user.getUserName(), userType, fromChatId);
    }

    private void saveChatShareRecord(Long userId, Long sharedChatId, String requestId, Chat sharedChat) {
        log.info("Saving chat share record: userId={}, sharedChatId={}, requestId={}, fetched={}",
                userId, sharedChatId, requestId, sharedChat != null);
    }

    private void sendUserShareConfirmation(TelegramClient telegramClient, Long chatId, User user, String userType) {
        StringBuilder message = new StringBuilder();

        if ("BOT".equals(userType)) {
            message.append("✅ <b>机器人已分享！</b>\n\n");
            message.append("🤖 <b>被分享机器人信息</b>\n");
            message.append(String.format("├ 机器人 ID: <code>%d</code>\n", user.getId()));
            message.append(String.format("├ 机器人名: @%s\n", user.getUserName()));
        } else {
            message.append("✅ <b>用户已分享！</b>\n\n");
            message.append("👤 <b>被分享用户信息</b>\n");
            message.append(String.format("├ 用户 ID: <code>%d</code>\n", user.getId()));
            if (StringUtils.isNotBlank(user.getUserName())) {
                message.append(String.format("├ 用户名: @%s\n", user.getUserName()));
            }
        }

        messageService.sendHtml(telegramClient, chatId, message.toString());
    }

    private void sendChatShareConfirmation(TelegramClient telegramClient, Long chatId,
                                           Long sharedChatId, Chat sharedChat, String requestId) {
        StringBuilder message = new StringBuilder();

        if (sharedChat != null) {
            message.append("✅ <b>群组/频道已分享！</b>\n\n");
            message.append("📊 <b>群组/频道详情</b>\n");
            message.append(String.format("├ ID: <code>%d</code>\n", sharedChatId));
            message.append(String.format("├ 名称: %s\n", sharedChat.getTitle()));
            message.append(String.format("├ 类型: %s\n", sharedChat.getType()));
            if (StringUtils.isNotBlank(sharedChat.getUserName())) {
                message.append(String.format("├ 用户名: @%s\n", sharedChat.getUserName()));
            }
            message.append(String.format("└ 请求 ID: %s\n", requestId));
        } else {
            message.append("✅ <b>群组/频道已分享（待确认）！</b>\n\n");
            message.append("📌 <b>群组/频道信息</b>\n");
            message.append(String.format("├ ChatID: <code>%d</code>\n", sharedChatId));
            message.append(String.format("└ 请求 ID: %s\n\n", requestId));
            message.append("⚠️ <b>需要手动添加机器人到该群组后，才能读取群组详情。</b>\n");
        }

        messageService.sendHtml(telegramClient, chatId, message.toString());
    }
}
