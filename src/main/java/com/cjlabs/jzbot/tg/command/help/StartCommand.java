package com.cjlabs.jzbot.tg.command.help;


import com.cjlabs.localbaby.tg.callback.GetUserIdCallbackHandler;
import com.cjlabs.localbaby.tg.command.AbstractBotCommand;
import com.cjlabs.localbaby.tg.enums.ChatTypeEnum;
import com.cjlabs.localbaby.tg.util.ButtonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * /start 命令 - 开始使用机器人
 */
@Slf4j
@Component
public class StartCommand extends AbstractBotCommand {

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "开始使用机器人";
    }

    @Override
    protected void doExecute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String firstName = update.getMessage().getFrom().getFirstName();

        ChatTypeEnum chatType = getChatType(update);

        // 根据聊天类型路由到不同的处理器
        switch (chatType) {
            case ChatTypeEnum.PRIVATE -> {
                log.info("Routing to PrivateMessageHandler");
                sendPrivateWelcome(chatId, firstName);
            }
            case ChatTypeEnum.GROUP, ChatTypeEnum.SUPERGROUP -> {
                log.info("Routing to GroupMessageHandler");
                sendGroupWelcome(chatId, firstName);
            }
            case ChatTypeEnum.CHANNEL -> {
                log.info("Routing to ChannelMessageHandler");
                // channelMessageHandler.handle(update);
            }
            default -> log.warn("Unknown chat type: {}", chatType);
        }

        log.info("Start command executed by user {} in {} chat {}", userId, chatType.getMsg(), chatId);
    }

    /**
     * 发送私聊欢迎消息
     */
    private void sendPrivateWelcome(Long chatId, String firstName) {
        String welcomeMessage = String.format(
                "👋 你好，%s！\n\n" +
                        "欢迎使用 Telegram 机器人！\n\n" +
                        "🤖 我可以帮助你：\n" +
                        "• 管理群组\n" +
                        "• 查询信息\n" +
                        "• 组织活动\n" +
                        "• 签到打卡\n" +
                        "• 抽奖活动\n\n" +
                        "💡 输入 /help 查看所有可用命令\n" +
                        "📱 将我添加到群组可以使用更多功能",
                firstName != null ? firstName : "朋友"
        );

        // 创建快捷按钮
        InlineKeyboardMarkup keyboard = new ButtonBuilder()
                // .addCallbackButton("📚 查看帮助", "cmd:help")
                // .newRow()
                .addCallbackButton("🆔 我的ID", GetUserIdCallbackHandler.CALLBACK_PREFIX)
                // .addCallbackButton("💰 我的资产", "cmd:myassets")
                .build();

        messageService.sendMessageWithKeyboard(telegramClient, chatId, welcomeMessage, keyboard);
    }

    /**
     * 发送群组欢迎消息
     */
    private void sendGroupWelcome(Long chatId, String firstName) {
        String welcomeMessage = String.format(
                "👋 你好，%s！\n\n" +
                        "感谢将我添加到群组！\n\n" +
                        "🤖 群组功能：\n" +
                        "• 群组管理和设置\n" +
                        "• 成员验证\n" +
                        "• 签到打卡\n" +
                        "• 抽奖活动\n" +
                        "• 活动组织\n\n" +
                        "⚙️ 管理员可使用 /settings 配置群组\n" +
                        "💡 输入 /help 查看所有命令",
                firstName != null ? firstName : "朋友"
        );

        // 群组中的快捷按钮
        InlineKeyboardMarkup keyboard = new ButtonBuilder()
                .addCallbackButton("📚 查看帮助", "cmd:help")
                .addCallbackButton("⚙️ 群组设置", "cmd:settings")
                .newRow()
                .addCallbackButton("🆔 群组ID", "cmd:getgroupid")
                .addCallbackButton("📊 签到统计", "cmd:checkinstats")
                .build();

        messageService.sendMessageWithKeyboard(telegramClient, chatId, welcomeMessage, keyboard);
    }
}
