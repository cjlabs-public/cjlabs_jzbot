package com.cjlabs.jzbot.tg.handler.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

/**
 * 机器人被添加事件处理器
 * 处理机器人被添加到群组的事件
 */
@Slf4j
@Component
public class BotAddedHandler {

    // TODO: 注入需要的服务
    // @Autowired
    // private GroupConfigService groupConfigService;
    // @Autowired
    // private MessageService messageService;

    /**
     * 处理机器人被添加事件
     */
    public void handle(Update update) {
        if (!update.hasMyChatMember()) {
            return;
        }
        
        ChatMemberUpdated memberUpdate = update.getMyChatMember();
        String newStatus = memberUpdate.getNewChatMember().getStatus();
        
        // 检查机器人是否被添加为成员或管理员
        if (!"member".equals(newStatus) && !"administrator".equals(newStatus)) {
            return;
        }
        
        Long chatId = memberUpdate.getChat().getId();
        String chatTitle = memberUpdate.getChat().getTitle();

        log.info("Bot added to group {} ({}) as {}", chatTitle, chatId, newStatus);

        // TODO: 初始化群组配置
        // groupConfigService.initializeGroupConfig(chatId);

        // TODO: 发送欢迎消息
        // String welcomeMsg = """
        //     👋 感谢邀请我加入群组！
        //     
        //     我可以帮助你：
        //     • 管理群组成员
        //     • 过滤关键词
        //     • 组织活动
        //     • 签到打卡
        //     
        //     💡 输入 /help 查看所有可用命令
        //     ⚙️ 输入 /settings 配置群组功能
        //     """;
        // messageService.sendMessage(chatId, welcomeMsg);
    }
}

