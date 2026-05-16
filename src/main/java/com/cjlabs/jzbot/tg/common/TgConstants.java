package com.cjlabs.jzbot.tg.common;

/**
 * Telegram Bot 常量
 */
public class TgConstants {
    
    // 消息长度限制
    public static final int MAX_MESSAGE_LENGTH = 4096;
    public static final int MAX_CAPTION_LENGTH = 1024;
    public static final int MAX_BUTTON_TEXT_LENGTH = 64;
    public static final int MAX_CALLBACK_DATA_LENGTH = 64;
    
    // 命令前缀
    public static final String COMMAND_PREFIX = "/";
    
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
        // 工具类，禁止实例化
    }
}