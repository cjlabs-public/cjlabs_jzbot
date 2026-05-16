package com.cjlabs.jzbot.tg.core.router;

import com.cjlabs.localbaby.tg.handler.event.BotAddedHandler;
import com.cjlabs.localbaby.tg.handler.event.JoinRequestHandler;
import com.cjlabs.localbaby.tg.handler.event.UserJoinedHandler;
import com.cjlabs.localbaby.tg.handler.event.UserLeftHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 事件路由器
 * 将事件路由到对应的事件处理器
 */
@Slf4j
@Component
public class EventRouter {

    @Autowired
    private UserJoinedHandler userJoinedHandler;

    @Autowired
    private UserLeftHandler userLeftHandler;

    @Autowired
    private BotAddedHandler botAddedHandler;

    @Autowired
    private JoinRequestHandler joinRequestHandler;

    /**
     * 路由入群请求事件
     */
    public void routeJoinRequest(Update update) {
        log.info("Routing join request event");
        joinRequestHandler.handle(update);
    }

    /**
     * 路由成员更新事件
     */
    public void routeChatMemberUpdate(Update update) {
        if (update.hasMyChatMember()) {
            // 机器人状态变化
            handleBotStatusChange(update);
        } else if (update.hasChatMember()) {
            // 成员状态变化
            handleMemberStatusChange(update);
        }
    }

    /**
     * 处理机器人状态变化
     */
    private void handleBotStatusChange(Update update) {
        String newStatus = update.getMyChatMember().getNewChatMember().getStatus();

        if ("member".equals(newStatus) || "administrator".equals(newStatus)) {
            log.info("Routing to BotAddedHandler");
            botAddedHandler.handle(update);
        } else if ("kicked".equals(newStatus) || "left".equals(newStatus)) {
            log.info("Bot removed from group {}",
                    update.getMyChatMember().getChat().getId());
            // TODO: 创建 BotRemovedHandler 处理机器人被移除的情况
        }
    }

    /**
     * 处理成员状态变化
     */
    private void handleMemberStatusChange(Update update) {
        String newStatus = update.getChatMember().getNewChatMember().getStatus();

        if ("member".equals(newStatus)) {
            log.info("Routing to UserJoinedHandler");
            userJoinedHandler.handle(update);
        } else if ("left".equals(newStatus) || "kicked".equals(newStatus)) {
            log.info("Routing to UserLeftHandler");
            userLeftHandler.handle(update);
        }
    }
}