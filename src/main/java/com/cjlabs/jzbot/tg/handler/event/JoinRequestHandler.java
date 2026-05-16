package com.cjlabs.jzbot.tg.handler.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 入群请求事件处理器
 * 处理用户申请加入群组的事件
 */
@Slf4j
@Component
public class JoinRequestHandler {

    // TODO: 注入需要的服务
    // @Autowired
    // private JoinVerificationService verificationService;
    // @Autowired
    // private GroupConfigService groupConfigService;

    /**
     * 处理入群请求
     */
    public void handle(Update update) {
        if (!update.hasChatJoinRequest()) {
            return;
        }
        
        ChatJoinRequest joinRequest = update.getChatJoinRequest();
        Long chatId = joinRequest.getChat().getId();
        Long userId = joinRequest.getUser().getId();
        String userName = joinRequest.getUser().getFirstName();

        log.info("User {} ({}) requested to join group {}", userName, userId, chatId);

        // TODO: 获取群组配置
        // var config = groupConfigService.getGroupConfig(chatId);

        // TODO: 执行入群验证
        // if (config.getVerificationEnabled()) {
        //     verificationService.sendVerification(chatId, userId, config);
        // } else {
        //     // 自动批准
        //     verificationService.approveJoinRequest(chatId, userId);
        // }
    }
}

