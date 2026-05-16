package com.cjlabs.jzbot.tg.handler.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

/**
 * 用户加入事件处理器
 * 处理用户加入群组的事件
 */
@Slf4j
@Component
public class UserJoinedHandler {

    // TODO: 注入需要的服务
    // @Autowired
    // private WelcomeMessageService welcomeMessageService;
    // @Autowired
    // private JoinVerificationService verificationService;
    // @Autowired
    // private GroupConfigService groupConfigService;

    /**
     * 处理用户加入事件
     */
    public void handle(Update update) {
        if (!update.hasChatMember()) {
            return;
        }
        
        ChatMemberUpdated memberUpdate = update.getChatMember();
        String newStatus = memberUpdate.getNewChatMember().getStatus();
        
        // 检查是否为新成员加入
        if (!"member".equals(newStatus)) {
            return;
        }
        
        Long chatId = memberUpdate.getChat().getId();
        Long userId = memberUpdate.getFrom().getId();
        String userName = memberUpdate.getFrom().getFirstName();

        log.info("User {} ({}) joined group {}", userName, userId, chatId);

        // TODO: 发送欢迎消息
        // var config = groupConfigService.getGroupConfig(chatId);
        // if (config.getWelcomeEnabled()) {
        //     welcomeMessageService.sendWelcome(chatId, userId, config.getWelcomeMessage());
        // }

        // TODO: 执行入群验证
        // if (config.getVerificationEnabled()) {
        //     verificationService.sendVerification(chatId, userId);
        // }
    }
}

