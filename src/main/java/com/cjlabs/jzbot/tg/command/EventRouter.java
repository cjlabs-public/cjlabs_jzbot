package com.cjlabs.jzbot.tg.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

/**
 * Telegram 事件路由器。
 * 处理非消息类事件，例如入群申请、成员加入/离开、bot 被移除等。
 */
@Slf4j
@Component
public class EventRouter {

    public void routeJoinRequest(Update update) {
        if (!update.hasChatJoinRequest()) {
            return;
        }

        ChatJoinRequest joinRequest = update.getChatJoinRequest();
        log.info("User {} ({}) requested to join group {}",
                joinRequest.getUser().getFirstName(),
                joinRequest.getUser().getId(),
                joinRequest.getChat().getId());
    }

    public void routeChatMemberUpdate(Update update) {
        if (update.hasMyChatMember()) {
            handleBotStatusChange(update.getMyChatMember());
        } else if (update.hasChatMember()) {
            handleMemberStatusChange(update.getChatMember());
        }
    }

    private void handleBotStatusChange(ChatMemberUpdated memberUpdate) {
        String newStatus = memberUpdate.getNewChatMember().getStatus();

        if ("member".equals(newStatus) || "administrator".equals(newStatus)) {
            log.info("Bot added to group {} ({}) as {}",
                    memberUpdate.getChat().getTitle(),
                    memberUpdate.getChat().getId(),
                    newStatus);
        } else if ("kicked".equals(newStatus) || "left".equals(newStatus)) {
            log.info("Bot removed from group {}", memberUpdate.getChat().getId());
        }
    }

    private void handleMemberStatusChange(ChatMemberUpdated memberUpdate) {
        String newStatus = memberUpdate.getNewChatMember().getStatus();

        if ("member".equals(newStatus)) {
            log.info("User {} ({}) joined group {}",
                    memberUpdate.getFrom().getFirstName(),
                    memberUpdate.getFrom().getId(),
                    memberUpdate.getChat().getId());
        } else if ("left".equals(newStatus) || "kicked".equals(newStatus)) {
            log.info("User {} ({}) left group {} (status: {})",
                    memberUpdate.getFrom().getFirstName(),
                    memberUpdate.getFrom().getId(),
                    memberUpdate.getChat().getId(),
                    newStatus);
        }
    }
}
