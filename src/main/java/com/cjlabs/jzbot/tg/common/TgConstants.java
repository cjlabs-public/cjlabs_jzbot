package com.cjlabs.jzbot.tg.common;

/**
 * Telegram Bot 常量
 */
public final class TgConstants {

    // 消息长度限制
    public static final int MAX_MESSAGE_LENGTH = 4096;
    public static final int MAX_CAPTION_LENGTH = 1024;
    public static final int MAX_BUTTON_TEXT_LENGTH = 64;
    public static final int MAX_CALLBACK_DATA_LENGTH = 64;
    
    // 命令前缀
    public static final String COMMAND_PREFIX = "/";

    // 基础命令
    public static final String CMD_START = COMMAND_PREFIX + "start";
    public static final String CMD_HELP = COMMAND_PREFIX + "help";
    public static final String CMD_SETTINGS = COMMAND_PREFIX + "settings";

    // ID 查询命令
    public static final String CMD_GET_ID = COMMAND_PREFIX + "getid";
    public static final String CMD_USER_ID = COMMAND_PREFIX + "userid";
    public static final String CMD_CHAT_ID = COMMAND_PREFIX + "chatid";
    public static final String CMD_GROUP_ID = COMMAND_PREFIX + "groupid";
    public static final String CMD_CHANNEL_ID = COMMAND_PREFIX + "channelid";
    public static final String CMD_BOT_ID = COMMAND_PREFIX + "botid";

    // 签到命令
    public static final String CMD_CHECKIN = COMMAND_PREFIX + "checkin";
    public static final String CMD_CHECKIN_CONFIG = COMMAND_PREFIX + "checkin_config";
    public static final String CMD_CHECKIN_STATS = COMMAND_PREFIX + "checkin_stats";

    // 抽奖命令
    public static final String CMD_LOTTERY = COMMAND_PREFIX + "lottery";
    public static final String CMD_LOTTERY_CREATE = COMMAND_PREFIX + "lottery_create";
    public static final String CMD_LOTTERY_JOIN = COMMAND_PREFIX + "lottery_join";
    public static final String CMD_LOTTERY_LIST = COMMAND_PREFIX + "lottery_list";

    // 活动命令
    public static final String CMD_ACTIVITY = COMMAND_PREFIX + "activity";
    public static final String CMD_ACTIVITY_LIST = COMMAND_PREFIX + "activity_list";

    // 用户命令
    public static final String CMD_MY_ASSETS = COMMAND_PREFIX + "myassets";
    public static final String CMD_MY_PROFILE = COMMAND_PREFIX + "myprofile";
    public static final String CMD_BIND = COMMAND_PREFIX + "bind";

    // 群组管理命令
    public static final String CMD_GROUP_CONFIG = COMMAND_PREFIX + "group_config";
    public static final String CMD_WELCOME = COMMAND_PREFIX + "welcome";
    public static final String CMD_FILTER = COMMAND_PREFIX + "filter";
    public static final String CMD_VERIFICATION = COMMAND_PREFIX + "verification";
    
    // 回调数据分隔符
    public static final String CALLBACK_SEPARATOR = ":";
    
    // 缓存键前缀
    public static final String CACHE_GROUP_CONFIG = "tg:group:config:";
    public static final String CACHE_USER_INFO = "tg:user:info:";
    public static final String CACHE_BOT_INFO = "tg:bot:info:";
    
    // 默认值
    public static final String DEFAULT_LANGUAGE = "zh_CN";
    public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    // 表情符号
    public static final String EMOJI_SUCCESS = "✅";
    public static final String EMOJI_ERROR = "❌";
    public static final String EMOJI_WARNING = "⚠️";
    public static final String EMOJI_INFO = "ℹ️";
    public static final String EMOJI_LOADING = "⏳";
    public static final String EMOJI_CHECK = "✔️";
    public static final String EMOJI_CROSS = "✖️";

    private TgConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
