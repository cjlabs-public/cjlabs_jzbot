package com.cjlabs.jzbot.tg.handler.share;

import com.cjlabs.localbaby.tg.core.LongPollingTelegramBot;
import com.cjlabs.localbaby.tg.service.message.TelegramMessageService;

import com.cjlabs.web.json.FmkJacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserShared;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * 处理 user_shared 和 chat_shared 事件
 * 当用户点击"分享用户"、"分享群组"、"分享频道"按钮时触发
 */
@Slf4j
@Component
public class IdShareEventHandler {

    @Autowired
    private TelegramMessageService messageService;

    /**
     * 处理 user_shared 事件（用户分享了一个用户）
     */
    public void handleUserShared(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage() || update.getMessage().getUserShared() == null) {
            return;
        }

        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        UserShared userShared = update.getMessage().getUserShared();
        Long sharedUserId = userShared.getUserId();
        String username = userShared.getUsername();
        List<PhotoSize> photoSizeList = userShared.getPhoto();

        log.info("IdShareEventHandler|handleUserShared|photoSizeList={}", FmkJacksonUtil.toJson(photoSizeList));
        log.info("User shared: userId={}, chatId={}, sharedUserId={}", userId, chatId, sharedUserId);

        // ✅ 方式1：根据简单的启发式判断
        // 机器人特征：必须有用户名，且用户名通常以字母开头（简单判断）
        boolean isBot = false;

        // ✅ 方式2：尝试从共同的群组中获取用户信息（更准确，但需要在共同群组中）
        User user = null;
        try {
            user = getUserInfoFromChat(bot.getTelegramClient(), userId, chatId);
            log.info("Successfully determined user type from chat: isBot={}", isBot);
        } catch (Exception e) {
            log.info("Could not get user info from chat, using heuristic method");
        }

        // ✅ 方式2：尝试从共同的群组中获取用户信息（更准确，但需要在共同群组中）
        User sharedUser = null;
        try {
            sharedUser = getUserInfoFromChat(bot.getTelegramClient(), sharedUserId, chatId);
            isBot = sharedUser.getIsBot();
            log.info("Successfully determined user type from chat: isBot={}", isBot);
        } catch (Exception e) {
            log.info("Could not get user info from chat, using heuristic method");
        }

        // ✅ 判断被分享的是否为机器人
        // User sharedUser = getSharedUserInfo(bot.getTelegramClient(), sharedUserId);
        // boolean isBot = sharedUser != null && sharedUser.getIsBot();

        if (isBot) {
            // 🤖 处理机器人的逻辑
            log.info("Shared user is a bot: botId={}, botUsername={}", sharedUserId, sharedUser.getUserName());
            handleSharedBot(bot.getTelegramClient(), chatId, sharedUser);
        } else {
            // 👤 处理普通用户的逻辑
            log.info("Shared user is a regular user: userId={}, username={}", sharedUserId, username);
            handleSharedUser(bot.getTelegramClient(), chatId, user);
        }

        // ✅ 尝试获取用户头像（验证用户存在）
        // getUserProfilePhotos(bot.getTelegramClient(), sharedUserId, username);

        // getUserInfoFromChat(bot.getTelegramClient(), sharedUserId, chatId);

        // ✅ 保存用户分享记录
        // saveUserShareRecord(userId, sharedUserId, username);

        // 发送确认消息
        // sendUserShareConfirmation(bot.getTelegramClient(), chatId, sharedUserId, username);

    }

    /**
     * 获取被分享用户的信息
     */
    private User getSharedUserInfo(TelegramClient telegramClient, Long userId) {
        try {
            GetUserProfilePhotos getUserPhotos = GetUserProfilePhotos.builder()
                    .userId(userId)
                    .limit(1)
                    .build();

            var photos = telegramClient.execute(getUserPhotos);
            log.info("Successfully verified user: userId={}, totalPhotos={}", userId, photos.getTotalCount());

            // 创建 User 对象用于后续判断
            // 注意：无法直接从 GetUserProfilePhotos 获取 isBot 信息
            // 需要通过其他方式判断
            return null;  // 这里需要优化
        } catch (Exception e) {
            log.warn("Failed to get user profile photos for userId: {}", userId, e);
            return null;
        }
    }


    /**
     * 处理被分享的普通用户
     */
    private void handleSharedUser(TelegramClient telegramClient, Long chatId, User bot) {
        log.info("Processing shared regular user: userId={}, username={}", bot.getId(), bot.getUserName());

        // ✅ 保存用户分享记录
        saveUserShareRecord(bot, "USER", chatId);

        // 发送用户信息确认消息
        sendUserShareConfirmation(telegramClient, chatId, bot, "USER");
    }

    /**
     * 处理被分享的机器人
     */
    private void handleSharedBot(TelegramClient telegramClient, Long chatId, User bot) {
        log.info("Processing shared bot: botId={}, botUsername={}", bot.getId(), bot.getUserName());

        // ✅ 保存机器人分享记录
        saveUserShareRecord(bot, "BOT", chatId);

        // 发送机器人信息确认消息
        sendBotShareConfirmation(telegramClient, chatId, bot);
    }


    /**
     * 发送机器人分享确认消息
     */
    private void sendBotShareConfirmation(TelegramClient telegramClient,
                                          Long chatId,
                                          User user) {
        // 这个方法现在可以删除，因为 sendUserShareConfirmation 已经支持了
        // 或者保留它作为一个便捷方法调用 sendUserShareConfirmation
        sendUserShareConfirmation(telegramClient, chatId, user, "BOT");
    }

    /**
     * 获取用户的头像信息（轻量级方式验证用户）
     * <p>
     * 注意：这是 bot 能获取的最多的用户信息
     * Telegram 出于隐私考虑，不允许 bot 获取用户的姓名等个人信息
     */
    private void getUserProfilePhotos(TelegramClient telegramClient, Long userId, String username) {
        try {
            GetUserProfilePhotos getUserPhotos = GetUserProfilePhotos.builder()
                    .userId(userId)
                    .limit(1)  // 只获取一张头像
                    .build();

            var photos = telegramClient.execute(getUserPhotos);
            int totalCount = photos.getTotalCount();

            log.info("Successfully verified user: userId={}, username={}, totalPhotos={}", userId, username, totalCount);
        } catch (Exception e) {
            log.warn("Failed to get user profile photos for userId: {}", userId, e);
        }
    }

    /**
     * 如果用户在共同的群组中，可以通过群组查询用户信息
     * <p>
     * 场景：
     * 1. 用户在群组中
     * 2. 你的 bot 也在同一个群组中
     * 3. 可以通过 GetChatMember 获取用户信息
     */
    private User getUserInfoFromChat(TelegramClient telegramClient, Long userId, Long chatId) {
        try {
            GetChatMember getChatMember = GetChatMember.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .build();

            ChatMember chatMember = telegramClient.execute(getChatMember);
            User user = chatMember.getUser();

            log.info("User info from chat: firstName={}, lastName={}, username={}",
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUserName());

            // 现在你有了更多信息！
            return user;
        } catch (Exception e) {
            log.warn("Cannot get user info from chat: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 处理 chat_shared 事件（用户分享了群组或频道）
     *
     * @param update Telegram Update 对象
     * @param bot    Telegram 客户端
     */
    public void handleChatShared(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage() || update.getMessage().getChatShared() == null) {
            return;
        }

        Long userId = update.getMessage().getFrom().getId();
        Long privateChatId = update.getMessage().getChatId();
        Long sharedChatId = update.getMessage().getChatShared().getChatId();
        String requestId = update.getMessage().getChatShared().getRequestId();

        log.info("Chat shared: userId={}, privateChatId={}, sharedChatId={}, requestId={}", userId, privateChatId, sharedChatId, requestId);

        // 尝试获取被分享群组/频道的详细信息
        Chat sharedChat = fetchChatInfo(bot.getTelegramClient(), sharedChatId);

        // ✅ 保存聊天分享记录到数据库
        saveChatShareRecord(userId, sharedChatId, requestId, sharedChat);

        // 发送确认消息（无论是否成功获取群组信息）
        sendChatShareConfirmation(bot.getTelegramClient(), privateChatId, sharedChatId, sharedChat, requestId);
    }

    /**
     * 获取群组/频道的详细信息
     * <p>
     * 注意：如果 bot 不在群组中，此方法会返回 null
     * 这是正常的，因为用户分享群组时 bot 不一定已经被添加到该群组中
     *
     * @param telegramClient Telegram 客户端
     * @param chatId         聊天 ID
     * @return Chat 对象，如果获取失败返回 null
     */
    private Chat fetchChatInfo(TelegramClient telegramClient, Long chatId) {
        try {
            GetChat getChat = GetChat.builder()
                    .chatId(chatId)
                    .build();
            Chat chat = telegramClient.execute(getChat);
            log.info("Successfully fetched chat info: id={}, title={}, type={}",
                    chat.getId(), chat.getTitle(), chat.getType());
            return chat;
        } catch (TelegramApiException e) {
            // 这是预期的错误 - bot 可能还没被添加到群组中
            log.warn("Failed to get chat info for chatId: {} (bot may not be a member)", chatId);
            log.info("Error details: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 保存用户分享记录到数据库
     *
     * @param user       被分享的用户/机器人 ID
     * @param userType   用户类型 ("USER" 或 "BOT")
     * @param fromChatId 分享来自哪个聊天的 ID
     */
    private void saveUserShareRecord(User user, String userType, Long fromChatId) {
        try {
            log.info("Saving user share record: userId={}, username={}, userType={}, fromChatId={}",
                    user.getId(), user.getUserName(), userType, fromChatId);

            // TODO: 调用业务服务保存记录
            // userShareService.saveRecord(userId, username, userType, fromChatId, LocalDateTime.now());

            log.info("User share record saved successfully");
        } catch (Exception e) {
            log.error("Failed to save user share record", e);
        }
    }

    /**
     * 保存聊天分享记录到数据库
     *
     * @param userId       分享用户的 ID
     * @param sharedChatId 被分享的聊天 ID
     * @param requestId    请求 ID
     * @param sharedChat   被分享的聊天信息（可能为 null）
     */
    private void saveChatShareRecord(Long userId, Long sharedChatId, String requestId, Chat sharedChat) {
        try {
            log.info("Saving chat share record: userId={}, sharedChatId={}, requestId={}",
                    userId, sharedChatId, requestId);

            // TODO: 调用业务服务保存记录
            // 建议保存以下信息：
            // - userId: 分享用户的 ID
            // - sharedChatId: 被分享的聊天 ID
            // - chatTitle: 聊天标题（如果成功获取）
            // - chatType: 聊天类型 (group/supergroup/channel)
            // - chatUsername: 聊天用户名（可选）
            // - requestId: 请求 ID（用于追踪）
            // - createdTime: 创建时间
            // - status: 状态 (PENDING - 待添加 / ACTIVE - 已激活)

            // String status = sharedChat != null ? "PENDING" : "PENDING";
            // chatBindingService.saveRecord(userId, sharedChatId,
            //         sharedChat != null ? sharedChat.getTitle() : null,
            //         sharedChat != null ? sharedChat.getType() : null,
            //         requestId, status, LocalDateTime.now());

            log.info("Chat share record saved successfully");
        } catch (Exception e) {
            log.error("Failed to save chat share record", e);
        }
    }


    /**
     * 发送用户分享确认消息
     *
     * @param telegramClient Telegram 客户端
     * @param chatId         聊天 ID
     * @param user           被分享的用户 ID
     * @param userType       用户类型 ("USER" 或 "BOT")
     */
    private void sendUserShareConfirmation(TelegramClient telegramClient,
                                           Long chatId,
                                           User user,
                                           String userType) {
        StringBuilder message = new StringBuilder();

        if ("BOT".equals(userType)) {
            // 机器人的确认消息
            message.append("✅ <b>机器人已分享！</b>\n\n");
            message.append("🤖 <b>被分享机器人信息</b>\n");
            message.append(String.format("├ 机器人 ID: <code>%d</code>\n", user.getId()));
            message.append(String.format("├ 机器人名: @%s\n", user.getUserName()));

            message.append("\n💡 <b>你可以：</b>\n");
            message.append("1️⃣ 复制机器人 ID\n");
            message.append("2️⃣ 查看机器人功能\n");
            message.append("3️⃣ 邀请该机器人到群组\n");
        } else {
            // 普通用户的确认消息
            message.append("✅ <b>用户已分享！</b>\n\n");
            message.append("👤 <b>被分享用户信息</b>\n");
            message.append(String.format("├ 用户 ID: <code>%d</code>\n", user.getId()));

            if (StringUtils.isNotBlank(user.getUserName())) {
                message.append(String.format("├ 用户名: @%s\n", user.getUserName()));
            }

            message.append("\n💡 <b>你可以：</b>\n");
            message.append("1️⃣ 复制用户 ID\n");
            message.append("2️⃣ 用于查询或管理\n");
            message.append("3️⃣ 与其他功能集成\n");
        }

        messageService.sendHtml(telegramClient, chatId, message.toString());
    }

    /**
     * 发送聊天分享确认消息
     * <p>
     * 流程：
     * 1. 如果成功获取了群组信息 → 显示详细信息
     * 2. 如果获取失败 → 只显示 ID（bot 还未被添加到群组）
     * 3. 用户需要手动将 bot 添加到群组才能完成绑定
     *
     * @param telegramClient Telegram 客户端
     * @param chatId         聊天 ID
     * @param sharedChatId   被分享的聊天 ID
     * @param sharedChat     被分享的聊天信息（可能为 null）
     * @param requestId      请求 ID
     */
    private void sendChatShareConfirmation(TelegramClient telegramClient, Long chatId,
                                           Long sharedChatId, Chat sharedChat, String requestId) {
        StringBuilder message = new StringBuilder();

        if (sharedChat != null) {
            // ✅ 情况1：成功获取群组信息 - Bot 已在群组中或有权限访问
            message.append("✅ <b>群组/频道已分享！</b>\n\n");
            message.append("📊 <b>群组/频道详情</b>\n");
            message.append(String.format("├ ID: <code>%d</code>\n", sharedChatId));
            message.append(String.format("├ 名称: %s\n", sharedChat.getTitle()));
            message.append(String.format("├ 类型: %s\n", sharedChat.getType()));

            if (sharedChat.getUserName() != null && !sharedChat.getUserName().isEmpty()) {
                message.append(String.format("├ 用户名: @%s\n", sharedChat.getUserName()));
            }

            message.append(String.format("└ 请求 ID: %s\n\n", requestId));

            message.append("💡 <b>后续步骤</b>\n");
            message.append("1️⃣ 机器人已被添加到群组\n");
            message.append("2️⃣ 输入 /help 查看功能\n");
            message.append("3️⃣ 配置群组权限\n");
        } else {
            // ⚠️ 情况2：无法获取群组信息 - Bot 还未被添加到群组中
            message.append("✅ <b>群组/频道已分享（待确认）！</b>\n\n");
            message.append("📌 <b>群组/频道信息</b>\n");
            message.append(String.format("├ ChatID: <code>%d</code>\n", sharedChatId));
            message.append(String.format("└ 请求 ID: %s\n\n", requestId));

            message.append("⚠️ <b>重要：需要手动添加机器人</b>\n");
            message.append("由于机器人还未被添加到该群组，无法读取群组详情。\n\n");

            message.append("📋 <b>后续步骤</b>\n");
            message.append("1️⃣ 打开该群组\n");
            message.append("2️⃣ 手动添加本机器人为成员\n");
            message.append("3️⃣ 给予必要的权限\n");
            message.append("4️⃣ 输入 /help 查看功能\n");

            message.append("\n💡 <b>提示</b>\n");
            message.append("群组 ID 已保存: <code>").append(sharedChatId).append("</code>\n");
        }

        messageService.sendHtml(telegramClient, chatId, message.toString());
    }


    //
    //
    //
    //
    //
    //
    //
    //
    //


    /**
     * 处理 bot 被添加到群组的事件
     * 当接收到 my_chat_member 更新时，检查是否应该激活绑定
     * <p>
     * 支持的场景：
     * 1. Bot 被添加到群组 (member)
     * 2. Bot 被提升为管理员 (administrator)
     * 3. Bot 从群组中被移除 (left/kicked)
     * 4. Bot 权限变化
     *
     * @param update         Telegram Update 对象
     * @param telegramClient Telegram 客户端（可选参数）
     */
    public void handleBotAddedToChat(Update update, TelegramClient telegramClient) {
        if (!update.hasMyChatMember()) {
            return;
        }

        try {
            ChatMemberUpdated myChatMember = update.getMyChatMember();
            Chat chat = myChatMember.getChat();
            Long chatId = chat.getId();

            ChatMember oldChatMember = update.getMyChatMember().getOldChatMember();
            String oldStatus = oldChatMember.getStatus();

            ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
            String newStatus = newChatMember.getStatus();

            log.info("Bot status changed in chat {}: {} -> {}", chatId, oldStatus, newStatus);

            // ✅ 处理 Bot 被添加的情况
            if (isBotAdded(oldStatus, newStatus)) {
                handleBotAdded(chatId, chat, newChatMember, telegramClient);
            }
            // ✅ 处理 Bot 被移除的情况
            else if (isBotRemoved(oldStatus, newStatus)) {
                handleBotRemoved(chatId, chat);
            }
            // ✅ 处理权限变化
            else if (isBotPermissionsChanged(oldStatus, newStatus)) {
                handleBotPermissionsChanged(chatId, newStatus);
            }

        } catch (Exception e) {
            log.error("Error handling bot chat member update", e);
        }
    }

    /**
     * 判断 bot 是否被添加到群组
     */
    private boolean isBotAdded(String oldStatus, String newStatus) {
        return isInactiveStatus(oldStatus) && isActiveStatus(newStatus);
    }

    /**
     * 判断 bot 是否被从群组移除
     */
    private boolean isBotRemoved(String oldStatus, String newStatus) {
        return isActiveStatus(oldStatus) && (
                "left".equals(newStatus) ||
                        "kicked".equals(newStatus)
        );
    }

    /**
     * 判断 bot 权限是否变化
     */
    private boolean isBotPermissionsChanged(String oldStatus, String newStatus) {
        return isActiveStatus(oldStatus) && isActiveStatus(newStatus) && !oldStatus.equals(newStatus);
    }

    /**
     * 判断是否为活跃状态（bot 在群组中）
     */
    private boolean isActiveStatus(String status) {
        return "member".equals(status) || "administrator".equals(status) || "restricted".equals(status);
    }

    /**
     * 判断是否为非活跃状态（bot 不在群组中）
     */
    private boolean isInactiveStatus(String status) {
        return "left".equals(status) || "kicked".equals(status) || "creator".equals(status);
    }

    /**
     * 处理 Bot 被成功添加到群组
     * <p>
     * 保存的群组信息包括：
     * - 群组基本信息（ID、名称、类型等）
     * - 群组统计信息（成员数量）
     * - 时间戳
     * - 状态标记
     */
    private void handleBotAdded(Long chatId, Chat chat, ChatMember newChatMember, TelegramClient telegramClient) {
        log.info("Bot successfully added to chat: chatId={}, chatTitle={}, status={}",
                chatId, chat.getTitle(), newChatMember.getStatus());

        try {
            // ✅ 步骤1：获取群组详细信息（包括成员数量等）
            Chat detailedChat = fetchDetailedChatInfo(telegramClient, chatId);

            // ✅ 步骤2：激活该聊天的绑定
            activateChatBinding(chatId);

            // ✅ 步骤3：保存群组完整信息
            saveChatInfo(chatId, detailedChat != null ? detailedChat : chat);

            // ✅ 步骤4：记录群组成员统计
            if (detailedChat != null) {
                logChatStatistics(chatId, detailedChat);
            }

            // ✅ 步骤5：发送欢迎消息
            if (telegramClient != null) {
                sendWelcomeMessageToChat(telegramClient, chatId, newChatMember);
            }

        } catch (Exception e) {
            log.error("Error handling bot added to chat {}", chatId, e);
        }
    }

    /**
     * 处理 Bot 被移除的情况
     */
    private void handleBotRemoved(Long chatId, Chat chat) {
        log.warn("Bot removed from chat: chatId={}, chatTitle={}", chatId, chat.getTitle());

        // ✅ 禁用该聊天的绑定
        deactivateChatBinding(chatId);

        // ✅ 清除相关数据
        clearChatData(chatId);
    }

    /**
     * 处理 Bot 权限变化
     */
    private void handleBotPermissionsChanged(Long chatId, String newStatus) {
        log.info("Bot permissions changed in chat: chatId={}, newStatus={}", chatId, newStatus);

        // ✅ 更新 bot 的权限信息
        updateBotPermissions(chatId, newStatus);
    }


    /**
     * 获取群组的详细信息（包括成员数量等）
     */
    private Chat fetchDetailedChatInfo(TelegramClient telegramClient, Long chatId) {
        if (telegramClient == null) {
            return null;
        }

        try {
            GetChat getChat = GetChat.builder()
                    .chatId(chatId)
                    .build();

            Chat detailedChat = telegramClient.execute(getChat);

            log.info("Successfully fetched detailed chat info: chatId={}, title={}, type={}, memberCount={}",
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


    /**
     * 激活聊天绑定
     */
    private void activateChatBinding(Long chatId) {
        try {
            log.info("Activating chat binding for chatId: {}", chatId);

            // TODO: 调用业务服务激活绑定
            // chatBindingService.activateBinding(chatId);

            log.info("Chat binding activated successfully for chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Failed to activate chat binding for chatId: {}", chatId, e);
        }
    }

    /**
     * 禁用聊天绑定
     */
    private void deactivateChatBinding(Long chatId) {
        try {
            log.info("Deactivating chat binding for chatId: {}", chatId);

            // TODO: 调用业务服务禁用绑定
            // chatBindingService.deactivateBinding(chatId);

            log.info("Chat binding deactivated successfully for chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Failed to deactivate chat binding for chatId: {}", chatId, e);
        }
    }

    /**
     * 保存群组信息
     */
    private void saveChatInfo(Long chatId, Chat chat) {
        try {
            log.info("Saving chat info: chatId={}, title={}, type={}",
                    chatId, chat.getTitle(), chat.getType());

            // TODO: 保存群组信息到数据库
            // chatService.saveChatInfo(chatId, chat.getTitle(), chat.getType(),
            //         chat.getUserName(), LocalDateTime.now());

            log.info("Chat info saved successfully");
        } catch (Exception e) {
            log.error("Failed to save chat info for chatId: {}", chatId, e);
        }
    }

    /**
     * 清除聊天数据
     */
    private void clearChatData(Long chatId) {
        try {
            log.info("Clearing chat data for chatId: {}", chatId);

            // TODO: 清除该群组的所有数据
            // chatService.clearChatData(chatId);

            log.info("Chat data cleared successfully");
        } catch (Exception e) {
            log.error("Failed to clear chat data for chatId: {}", chatId, e);
        }
    }

    /**
     * 更新 Bot 权限
     */
    private void updateBotPermissions(Long chatId, String newStatus) {
        try {
            log.info("Updating bot permissions for chatId: {}, status={}", chatId, newStatus);

            // TODO: 更新 bot 的权限信息
            // chatService.updateBotPermissions(chatId, newStatus);

            log.info("Bot permissions updated successfully");
        } catch (Exception e) {
            log.error("Failed to update bot permissions for chatId: {}", chatId, e);
        }
    }

    /**
     * 记录群组统计信息
     * 用于监控和分析 bot 在哪些群组中最活跃
     */
    private void logChatStatistics(Long chatId, Chat chat) {
        try {
            // Integer memberCount = chat.getUserName();
            String chatType = chat.getType();
            String title = chat.getTitle();
            String username = chat.getUserName();

            log.info("Chat statistics: chatId={}, title={}, type={}, username={}",
                    chatId, title, chatType, username);

            // TODO: 保存统计信息到数据库或缓存
            // statistics.recordChatInfo(chatId, new ChatStatistics(
            //     chatId,
            //     title,
            //     chatType,
            //     memberCount,
            //     username,
            //     LocalDateTime.now()
            // ));

        } catch (Exception e) {
            log.warn("Failed to log chat statistics for chatId: {}", chatId, e);
        }
    }


    /**
     * 发送欢迎消息到群组
     * <p>
     * 包含：
     * - 欢迎信息
     * - 群组已启用标记
     * - 可用命令列表
     * - 群组信息展示
     */
    private void sendWelcomeMessageToChat(TelegramClient telegramClient, Long chatId, ChatMember newChatMember) {
        try {
            StringBuilder message = new StringBuilder();

            message.append("👋 <b>欢迎使用本机器人！</b>\n\n");
            message.append("✅ <b>机器人已成功添加到群组</b>\n");
            message.append(String.format("📌 <b>群组 ID</b>: <code>%d</code>\n", chatId));
            message.append(String.format("📌 <b>群组 名称</b>: <code>%s</code>\n\n", newChatMember.getUser().getUserName()));

            message.append("💡 <b>可用命令</b>\n");
            message.append("• /help - 查看帮助信息\n");
            message.append("• /getid - 查看各类 ID\n");
            message.append("• /share - 分享群组信息\n\n");

            message.append("📋 <b>群组管理功能</b>\n");
            message.append("• 自动欢迎新成员\n");
            message.append("• 群组信息统计\n");
            message.append("• 成员管理工具\n\n");

            message.append("❓ <b>需要帮助？</b>\n");
            message.append("输入 /help 查看完整命令列表\n");

            messageService.sendHtml(telegramClient, chatId, message.toString());

            log.info("Welcome message sent to chat: {}", chatId);

        } catch (Exception e) {
            log.warn("Failed to send welcome message to chat {}", chatId, e);
        }
    }

}