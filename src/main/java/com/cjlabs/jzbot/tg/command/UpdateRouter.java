package com.cjlabs.jzbot.tg.command;

import com.cjlabs.jzbot.tg.bot.LongPollingTelegramBot;
import com.cjlabs.jzbot.tg.callback.CallbackRouter;
import com.cjlabs.jzbot.tg.callback.event.BotMembershipHandler;
import com.cjlabs.jzbot.tg.callback.message.share.IdShareMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * 更新路由器
 * 根据 Update 类型分发到不同的路由器处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateRouter {

    private final CommandRouter commandRouter;
    private final CallbackRouter callbackRouter;
    private final MessageRouter messageRouter;
    private final EventRouter eventRouter;
    private final BotMembershipHandler botMembershipHandler;
    private final IdShareMessageHandler idShareMessageHandler;

    /**
     * 路由 Update 到相应的处理器
     */
    public void route(Update update, LongPollingTelegramBot bot) {
        try {
            if (routeMessage(update, bot)) {
                return;
            }
            if (update.hasCallbackQuery()) {
                callbackRouter.route(update, bot.getTelegramClient());
            } else if (update.hasMyChatMember()) {
                botMembershipHandler.handle(update, bot.getTelegramClient());
                eventRouter.routeChatMemberUpdate(update);
            } else if (update.hasChatMember()) {
                eventRouter.routeChatMemberUpdate(update);
            } else if (update.hasChatJoinRequest()) {
                eventRouter.routeJoinRequest(update);
            } else {
                log.info("Unhandled update type: {}", update);
            }
        } catch (Exception e) {
            log.error("Error routing update", e);
        }
    }

    private boolean routeMessage(Update update, LongPollingTelegramBot bot) {
        if (!update.hasMessage()) {
            return false;
        }

        Message message = update.getMessage();
        if (message.getUserShared() != null) {
            idShareMessageHandler.handleUserShared(update, bot);
        } else if (message.getChatShared() != null) {
            idShareMessageHandler.handleChatShared(update, bot);
        } else if (message.isCommand()) {
            commandRouter.route(update, bot);
        } else {
            messageRouter.route(update, bot);
        }
        return true;
    }
}
