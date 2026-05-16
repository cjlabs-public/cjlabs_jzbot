package com.cjlabs.jzbot.tg.domain.bo.group;

import com.cjlabs.domain.enums.FmkLanguageEnum;

import com.cjlabs.domain.enums.FmkTimezoneEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 群组配置模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupConfig {
    private Long chatId;

    // 基础配置
    private Boolean managementEnabled;      // 是否启用群管理
    private FmkLanguageEnum language;          // 群组语言
    private FmkTimezoneEnum timezone;                // 时区

    // 欢迎消息配置
    private Boolean welcomeEnabled;         // 是否启用欢迎消息
    private String welcomeMessage;          // 欢迎消息内容
    private Integer welcomeDeleteAfter;     // 欢迎消息删除时间(秒)

    // 关键词过滤配置
    private Boolean filterEnabled;          // 是否启用关键词过滤
    private List<String> keywords;          // 过滤关键词列表
    // private FilterAction filterAction;      // 过滤动作(删除/警告/禁言/踢出)

    // 入群验证配置
    private Boolean verificationEnabled;    // 是否启用入群验证
    // private VerificationType verificationType; // 验证类型(按钮/问答/数学题)
    private Integer verificationTimeout;    // 验证超时时间(秒)

    // 功能开关
    private Boolean checkinEnabled;         // 是否启用签到
    private Boolean lotteryEnabled;         // 是否启用抽奖
    private Boolean activityEnabled;        // 是否启用活动

    // 其他配置...
}
