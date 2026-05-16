package com.cjlabs.jzbot.tg.domain.bo.bridgeconfig;// package com.cjlabs.tg.tg.domain.bo.bridgeconfig;
//
// // 桥接配置模型
// @Data
// @Builder
// public class BridgeConfig {
//     private Long id;
//     private Long botId;
//
//     private Long sourceChatId;              // 源聊天ID
//     private Long targetChatId;              // 目标聊天ID
//
//     private Boolean bidirectional;          // 是否双向
//     private Boolean enabled;                // 是否启用
//
//     // 转发规则
//     private Boolean forwardText;            // 转发文本
//     private Boolean forwardMedia;           // 转发媒体
//     private Boolean forwardSticker;         // 转发贴纸
//
//     // 过滤规则
//     private List<String> excludeKeywords;   // 排除关键词
//     private List<Long> excludeUsers;        // 排除用户
// }