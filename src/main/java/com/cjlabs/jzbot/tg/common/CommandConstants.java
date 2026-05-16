package com.cjlabs.jzbot.tg.common;

/**
 * 命令常量
 */
public class CommandConstants {
    
    // 基础命令
    public static final String CMD_START = "/start";
    public static final String CMD_HELP = "/help";
    public static final String CMD_SETTINGS = "/settings";
    
    // ID 查询命令
    public static final String CMD_GET_ID = "/getid";
    public static final String CMD_USER_ID = "/userid";
    public static final String CMD_CHAT_ID = "/chatid";
    public static final String CMD_GROUP_ID = "/groupid";
    public static final String CMD_CHANNEL_ID = "/channelid";
    public static final String CMD_BOT_ID = "/botid";
    
    // 签到命令
    public static final String CMD_CHECKIN = "/checkin";
    public static final String CMD_CHECKIN_CONFIG = "/checkin_config";
    public static final String CMD_CHECKIN_STATS = "/checkin_stats";
    
    // 抽奖命令
    public static final String CMD_LOTTERY = "/lottery";
    public static final String CMD_LOTTERY_CREATE = "/lottery_create";
    public static final String CMD_LOTTERY_JOIN = "/lottery_join";
    public static final String CMD_LOTTERY_LIST = "/lottery_list";
    
    // 活动命令
    public static final String CMD_ACTIVITY = "/activity";
    public static final String CMD_ACTIVITY_LIST = "/activity_list";
    
    // 用户命令
    public static final String CMD_MY_ASSETS = "/myassets";
    public static final String CMD_MY_PROFILE = "/myprofile";
    public static final String CMD_BIND = "/bind";
    
    // 群组管理命令
    public static final String CMD_GROUP_CONFIG = "/group_config";
    public static final String CMD_WELCOME = "/welcome";
    public static final String CMD_FILTER = "/filter";
    public static final String CMD_VERIFICATION = "/verification";
    
    private CommandConstants() {
        // 工具类，禁止实例化
    }
}