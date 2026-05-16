package com.cjlabs.jzbot.tg.util;

import java.util.regex.Pattern;

/**
 * 验证辅助工具
 */
public class ValidationHelper {
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{5,32}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证 URL 格式
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证 Telegram User ID
     */
    public static boolean isValidUserId(Long userId) {
        return userId != null && userId > 0;
    }
    
    /**
     * 验证 Telegram Chat ID
     */
    public static boolean isValidChatId(Long chatId) {
        return chatId != null && chatId != 0;
    }
    
    /**
     * 验证文本长度
     */
    public static boolean isValidLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return false;
        }
        int length = text.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 验证数字范围
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * 验证是否为空
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    /**
     * 验证是否不为空
     */
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }
}