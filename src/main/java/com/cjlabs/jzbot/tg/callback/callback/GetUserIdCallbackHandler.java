package com.cjlabs.jzbot.tg.callback.callback;

import com.cjlabs.jzbot.tg.callback.AbstractCallbackHandler;
import com.cjlabs.jzbot.tg.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetUserIdCallbackHandler extends AbstractCallbackHandler {

    public static final String CALLBACK_PREFIX = "cmd:getuserid";

    private final TelegramMessageService messageService;

    @Override
    public String getCallbackPrefix() {
        return CALLBACK_PREFIX;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramClient client, String data) {
        Long chatId = getChatId(callbackQuery);
        Long userId = getUserId(callbackQuery);
        String displayName = getUserDisplayName(callbackQuery);
        String username = getUsername(callbackQuery);

        if (chatId == null || userId == null) {
            sendError(client, callbackQuery, "无法识别当前用户信息");
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("🆔 你的用户信息\n\n");
        message.append("昵称: ").append(displayName != null ? displayName : "-").append("\n");
        message.append("用户ID: ").append(userId);

        if (username != null && !username.isBlank()) {
            message.append("\n用户名: @").append(username);
        }

        messageService.sendMessage(client, chatId, message.toString());
        sendSuccess(client, callbackQuery, "你的用户ID是 " + userId);

        log.info("Returned user id {} to chat {}", userId, chatId);
    }
}
