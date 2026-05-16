package com.cjlabs.jzbot.tg.util;

import com.cjlabs.domain.enums.IEnumStr;
import com.cjlabs.jzbot.tg.common.enums.ChatTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

/**
 * Telegram 辅助工具类
 */
@Slf4j
public class TelegramHelper {

    /**
     * 获取用户 ID
     *
     * @param update Telegram 更新对象
     * @return 用户 ID（正数）
     */
    public static Long getUserId(Update update) {
        if (!update.hasMessage() || update.getMessage().getFrom() == null) {
            return null;
        }
        return update.getMessage().getFrom().getId();
    }

    /**
     * 获取用户名
     * @param update Telegram 更新对象
     * @return 用户名（带 @）或 null
     */
    public static String getUsername(Update update) {
        if (!update.hasMessage() || update.getMessage().getFrom() == null) {
            return null;
        }
        String username = update.getMessage().getFrom().getUserName();
        return username != null ? "@" + username : null;
    }

    /**
     * 获取聊天用户名
     * @param update Telegram 更新对象
     * @return 聊天用户名（带 @）或 null
     */
    public static String getChatUsername(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getChat() == null) {
            return null;
        }
        String username = message.getChat().getUserName();
        return username != null ? "@" + username : null;
    }

    /**
     * 获取聊天 ID
     *
     * @param update Telegram 更新对象
     * @return 聊天 ID（群组为负数）
     */
    public static Long getChatId(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getChat() == null) {
            return null;
        }
        return message.getChat().getId();
    }

    /**
     * 获取聊天类型
     *
     * @param update Telegram 更新对象
     * @return 聊天类型（private, group, supergroup, channel）
     */
    public static ChatTypeEnum getChatType(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getChat() == null) {
            return null;
        }
        String chatType = message.getChat().getType();

        Optional<ChatTypeEnum> enumOptional = IEnumStr.getEnumByCode(chatType, ChatTypeEnum.class);

        if (enumOptional.isEmpty()) {
            log.info("AbstractBotCommand|getChatType|enumOptional is null");
            return null;
        }

        return enumOptional.get();
    }

    /**
     * 获取群组标题
     * @param update Telegram 更新对象
     * @return 群组/频道名称
     */
    public static String getGroupTitle(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getChat() == null) {
            return null;
        }
        return message.getChat().getTitle();
    }

    /**
     * 获取格式化的 ID 信息
     */
    public static String formatIdInfo(Update update) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 ID 信息：\n\n");

        Long userId = getUserId(update);
        if (userId != null) {
            sb.append("👤 用户 ID: `").append(userId).append("`\n");
        }

        String username = getUsername(update);
        if (username != null) {
            sb.append("👤 用户名: ").append(username).append("\n");
        }

        Long chatId = getChatId(update);
        if (chatId != null) {
            sb.append("💬 聊天 ID: `").append(chatId).append("`\n");
        }

        String chatTitle = getGroupTitle(update);
        if (chatTitle != null) {
            sb.append("📢 聊天标题: ").append(chatTitle).append("\n");
        }

        String chatUsername = getChatUsername(update);
        if (chatUsername != null) {
            sb.append("📢 聊天用户名: ").append(chatUsername).append("\n");
        }

        ChatTypeEnum chatType = getChatType(update);
        if (chatType != null) {
            sb.append("🔤 聊天类型: ").append(chatType.getMsg()).append("\n");
        }

        return sb.toString();
    }

    /**
     * 获取用户显示名称
     */
    public static String getUserDisplayName(User user) {
        if (user == null) {
            return "Unknown";
        }

        StringBuilder name = new StringBuilder();
        name.append(user.getFirstName());

        if (user.getLastName() != null) {
            if (name.length() > 0) {
                name.append(" ");
            }
            name.append(user.getLastName());
        }

        if (name.length() == 0 && user.getUserName() != null) {
            name.append("@").append(user.getUserName());
        }

        return name.length() > 0 ? name.toString() : "User#" + user.getId();
    }

    /**
     * 获取用户提及标签
     */
    public static String getUserMention(User user) {
        if (user == null) {
            return "Unknown";
        }

        if (user.getUserName() != null) {
            return "@" + user.getUserName();
        }

        return getUserDisplayName(user);
    }

    /**
     * 获取用户 HTML 链接
     * 格式：<a href="tg://user?id=123">Name</a>
     */
    public static String getUserHtmlLink(User user) {
        if (user == null) {
            return "Unknown";
        }

        String displayName = getUserDisplayName(user);
        return String.format("<a href=\"tg://user?id=%d\">%s</a>",
                user.getId(), escapeHtml(displayName));
    }

    /**
     * 提取命令参数
     */
    public static String[] extractCommandArgs(Message message) {
        if (message == null || message.getText() == null) {
            return new String[0];
        }

        String text = message.getText().trim();
        String[] parts = text.split("\\s+");

        if (parts.length <= 1) {
            return new String[0];
        }

        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        return args;
    }

    /**
     * 获取命令文本（不含参数）
     */
    public static String getCommandText(Message message) {
        if (message == null || message.getText() == null) {
            return "";
        }

        String text = message.getText().trim();
        int spaceIndex = text.indexOf(' ');

        if (spaceIndex > 0) {
            return text.substring(0, spaceIndex);
        }

        return text;
    }

    /**
     * 提取命令名称（去除 @botname 和参数）。
     * 例如: "/start@mybot arg" -> "/start"
     */
    public static String extractCommand(Message message) {
        String commandText = getCommandText(message);
        if (!commandText.startsWith("/")) {
            return "";
        }

        int atIndex = commandText.indexOf('@');
        if (atIndex > 0) {
            commandText = commandText.substring(0, atIndex);
        }

        return commandText.toLowerCase();
    }

    /**
     * 提取纯命令（去除 / 和 @botname）
     * 例如："/start@mybot" -> "start"
     */
    public static String extractPureCommand(String commandText) {
        if (commandText == null || commandText.isEmpty()) {
            return "";
        }

        // 去除开头的 /
        String command = commandText.startsWith("/") ?
                commandText.substring(1) : commandText;

        // 去除 @botname 部分
        int atIndex = command.indexOf('@');
        if (atIndex > 0) {
            command = command.substring(0, atIndex);
        }

        return command.toLowerCase();
    }

    /**
     * 转义 HTML 特殊字符
     */
    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /**
     * 转义 Markdown 特殊字符
     */
    public static String escapeMarkdown(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

    /**
     * 转义 MarkdownV2 特殊字符
     * 用于 ParseMode.MARKDOWNV2 模式（更严格）
     */
    public static String escapeMarkdownV2(String text) {
        if (text == null) {
            return "";
        }

        // MarkdownV2 需要转义的字符
        String[] specialChars = {
                "_", "*", "[", "]", "(", ")", "~", "`", ">", "#",
                "+", "-", "=", "|", "{", "}", ".", "!"
        };

        String result = text;
        for (String ch : specialChars) {
            result = result.replace(ch, "\\" + ch);
        }

        return result;
    }

    /**
     * 获取聊天标题或名称
     */
    public static String getChatTitle(Chat chat) {
        if (chat == null) {
            return "Unknown";
        }

        if (chat.getTitle() != null) {
            return chat.getTitle();
        }

        if (chat.getFirstName() != null) {
            StringBuilder name = new StringBuilder(chat.getFirstName());
            if (chat.getLastName() != null) {
                name.append(" ").append(chat.getLastName());
            }
            return name.toString();
        }

        if (chat.getUserName() != null) {
            return "@" + chat.getUserName();
        }

        return "Chat#" + chat.getId();
    }

    /**
     * 格式化聊天 ID（用于日志）
     */
    public static String formatChatId(Long chatId) {
        if (chatId == null) {
            return "null";
        }
        return chatId > 0 ? "User:" + chatId : "Chat:" + chatId;
    }

    // ==================== 消息验证方法 ====================

    /**
     * 检查消息是否有文本内容
     */
    public static boolean hasText(Message message) {
        return message != null &&
                message.getText() != null &&
                !message.getText().trim().isEmpty();
    }

    /**
     * 检查消息是否为命令
     */
    public static boolean isCommand(Message message) {
        return hasText(message) && message.getText().startsWith("/");
    }

    /**
     * 检查消息是否来自机器人
     */
    public static boolean isFromBot(Message message) {
        return message != null &&
                message.getFrom() != null &&
                message.getFrom().getIsBot();
    }

    /**
     * 检查用户是否为机器人
     */
    public static boolean isBot(User user) {
        return user != null && user.getIsBot();
    }
}
