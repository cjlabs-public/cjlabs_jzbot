package com.cjlabs.jzbot.tg.handler.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

/**
 * 用户离开事件处理器
 * 处理用户离开群组的事件
 */
@Slf4j
@Component
public class UserLeftHandler {

    /**
     * 处理用户离开事件
     */
    public void handle(Update update) {
        if (!update.hasChatMember()) {
            return;
        }
        
        ChatMemberUpdated memberUpdate = update.getChatMember();
        String newStatus = memberUpdate.getNewChatMember().getStatus();
        
        // 检查是否为成员离开或被踢出
        if (!"left".equals(newStatus) && !"kicked".equals(newStatus)) {
            return;
        }
        
        Long chatId = memberUpdate.getChat().getId();
        Long userId = memberUpdate.getFrom().getId();
        String userName = memberUpdate.getFrom().getFirstName();

        log.info("User {} ({}) left group {} (status: {})", userName, userId, chatId, newStatus);

        // TODO: 记录用户离开事件
        // TODO: 清理用户相关数据（如果需要）
    }
}

