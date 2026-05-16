package com.cjlabs.jzbot.tg.core.router;

import com.cjlabs.localbaby.tg.callback.CallbackRouter;
import com.cjlabs.localbaby.tg.core.LongPollingTelegramBot;
import com.cjlabs.localbaby.tg.handler.share.IdShareEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 更新路由器
 * 根据 Update 类型分发到不同的路由器处理
 */
@Slf4j
@Component
public class UpdateRouter {

    @Autowired
    private CommandRouter commandRouter;

    @Autowired
    private CallbackRouter callbackRouter;  // 引用 callback 包的 CallbackRouter

    @Autowired
    private MessageRouter messageRouter;

    @Autowired
    private EventRouter eventRouter;

    @Autowired
    private IdShareEventHandler idShareEventHandler;

    /**
     * 存储当前 bot 的 ThreadLocal 变量
     * 用于在路由过程中传递 bot 上下文
     */
    // private static final ThreadLocal<LongPollingTelegramBot> CURRENT_BOT = new TransmittableThreadLocal<>();

    /**
     * 路由 Update 到相应的处理器
     */
    public void route(Update update, LongPollingTelegramBot bot) {
        // CURRENT_BOT.set(bot);

        try {
            if (update.hasMessage()) {

                // ✅ 优先处理 user_shared 事件（用户分享）
                if (update.getMessage().getUserShared() != null) {
                    log.info("Handling user_shared event");
                    idShareEventHandler.handleUserShared(update, bot);
                    return;
                }

                // ✅ 优先处理 chat_shared 事件（群组/频道分享）
                if (update.getMessage().getChatShared() != null) {
                    log.info("Handling chat_shared event");
                    idShareEventHandler.handleChatShared(update, bot);
                    return;
                }

                if (update.getMessage().isCommand()) {
                    commandRouter.route(update, bot);
                } else {
                    messageRouter.route(update, bot);
                }
            } else if (update.hasCallbackQuery()) {

                callbackRouter.route(update, bot.getTelegramClient());

            } else if (update.hasMyChatMember()) {

                // ✅ 处理 Bot 被添加到群组或权限变化的事件
                log.info("Handling my_chat_member event");
                idShareEventHandler.handleBotAddedToChat(update, bot.getTelegramClient());

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
        } finally {
            // 清理 ThreadLocal，避免内存泄漏
            // CURRENT_BOT.remove();
        }
    }

    /**
     * 获取当前处理的 bot 实例
     *
     * @return 当前 bot，如果不在路由上下文中返回 null
     */
    // public static LongPollingTelegramBot getCurrentBot() {
    //     return CURRENT_BOT.get();
    // }
}
