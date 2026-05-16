package com.cjlabs.jzbot.tg.domain.bo.activity;// package com.cjlabs.tg.tg.domain.bo.activity;
//
// // 活动模型
// @Data
// @Builder
// public class Activity {
//     private Long id;
//     private Long chatId;
//     private ActivityType type;              // 活动类型
//
//     private String title;                   // 活动标题
//     private String description;             // 活动描述
//
//     private LocalDateTime startTime;        // 开始时间
//     private LocalDateTime endTime;          // 结束时间
//
//     private ActivityStatus status;          // 状态
//
//     // 奖励配置
//     private List<Reward> rewards;           // 奖励列表
//
//     // 参与条件
//     private Map<String, Object> conditions; // 参与条件(灵活配置)
// }