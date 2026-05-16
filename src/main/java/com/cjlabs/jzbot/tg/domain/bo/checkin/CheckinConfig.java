package com.cjlabs.jzbot.tg.domain.bo.checkin;// package com.cjlabs.tg.tg.domain.bo.checkin;
//
// // 签到配置模型
// @Data
// @Builder
// public class CheckinConfig {
//     private Long chatId;
//
//     // 基础奖励
//     private Integer baseReward;             // 基础签到奖励
//
//     // 连续签到奖励
//     private Map<Integer, Integer> continuousRewards; // 连续天数 -> 额外奖励
//     // 例如: {7: 50, 30: 200} 表示连续7天额外奖励50，连续30天额外奖励200
//
//     // 排名奖励
//     private Boolean rankRewardEnabled;      // 是否启用排名奖励
//     private Map<Integer, Integer> rankRewards; // 排名 -> 额外奖励
//
//     // 时间限制
//     private LocalTime checkinStartTime;     // 签到开始时间
//     private LocalTime checkinEndTime;       // 签到结束时间
//
//     // 自定义消息
//     private String checkinSuccessMessage;   // 签到成功消息模板
//     private String alreadyCheckedMessage;   // 已签到消息
// }
