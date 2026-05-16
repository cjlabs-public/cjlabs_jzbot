package com.cjlabs.jzbot.tg.util;// package com.cjlabs.tg.tg.util;
//
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * 消息格式化工具
//  */
// public class MessageFormatter {
//
//     /**
//      * 格式化用户信息
//      */
//     public static String formatUserInfo(Long userId, String username, String firstName, String lastName, boolean isBot) {
//         StringBuilder sb = new StringBuilder();
//         sb.append("<b>👤 用户信息</b>\n\n");
//         sb.append("User ID: <code>").append(userId).append("</code>\n");
//         sb.append("Username: ").append(username != null ? "@" + username : "无").append("\n");
//         sb.append("First Name: ").append(TelegramHelper.escapeHtml(firstName)).append("\n");
//         if (lastName != null) {
//             sb.append("Last Name: ").append(TelegramHelper.escapeHtml(lastName)).append("\n");
//         }
//         sb.append("Is Bot: ").append(isBot ? "是" : "否");
//         return sb.toString();
//     }
//
//     /**
//      * 格式化聊天信息
//      */
//     public static String formatChatInfo(Long chatId, String title, String username, String type) {
//         StringBuilder sb = new StringBuilder();
//         sb.append("<b>💬 聊天信息</b>\n\n");
//         sb.append("Chat ID: <code>").append(chatId).append("</code>\n");
//         if (title != null) {
//             sb.append("Title: ").append(TelegramHelper.escapeHtml(title)).append("\n");
//         }
//         if (username != null) {
//             sb.append("Username: @").append(username).append("\n");
//         }
//         sb.append("Type: ").append(TelegramHelper.getChatTypeDescription(type));
//         return sb.toString();
//     }
//
//     /**
//      * 格式化完整 ID 信息
//      */
//     public static String formatCompleteIdInfo(IdInfoDTO idInfo) {
//         StringBuilder sb = new StringBuilder();
//         sb.append("📋 <b>ID 信息</b>\n\n");
//
//         // 用户信息
//         if (idInfo.getUserInfo() != null) {
//             UserInfoDTO user = idInfo.getUserInfo();
//             sb.append(formatUserInfo(
//                 user.getUserId(),
//                 user.getUsername(),
//                 user.getFirstName(),
//                 user.getLastName(),
//                 user.isBot()
//             )).append("\n\n");
//         }
//
//         // 聊天信息
//         if (idInfo.getChatInfo() != null) {
//             ChatInfoDTO chat = idInfo.getChatInfo();
//             sb.append(formatChatInfo(
//                 chat.getChatId(),
//                 chat.getTitle(),
//                 chat.getUsername(),
//                 chat.getType()
//             )).append("\n\n");
//         }
//
//         // 消息信息
//         if (idInfo.getMessageInfo() != null) {
//             MessageInfoDTO msg = idInfo.getMessageInfo();
//             sb.append("<b>📨 消息信息</b>\n");
//             sb.append("Message ID: <code>").append(msg.getMessageId()).append("</code>");
//         }
//
//         return sb.toString();
//     }
//
//     /**
//      * 格式化错误消息
//      */
//     public static String formatError(String errorMessage) {
//         return "❌ <b>错误</b>\n\n" + TelegramHelper.escapeHtml(errorMessage);
//     }
//
//     /**
//      * 格式化成功消息
//      */
//     public static String formatSuccess(String successMessage) {
//         return "✅ <b>成功</b>\n\n" + TelegramHelper.escapeHtml(successMessage);
//     }
//
//     /**
//      * 格式化警告消息
//      */
//     public static String formatWarning(String warningMessage) {
//         return "⚠️ <b>警告</b>\n\n" + TelegramHelper.escapeHtml(warningMessage);
//     }
//
//     /**
//      * 格式化信息消息
//      */
//     public static String formatInfo(String infoMessage) {
//         return "ℹ️ <b>信息</b>\n\n" + TelegramHelper.escapeHtml(infoMessage);
//     }
//
//     /**
//      * 使用模板格式化消息
//      */
//     public static String formatTemplate(String template, Map<String, String> params) {
//         String result = template;
//         for (Map.Entry<String, String> entry : params.entrySet()) {
//             result = result.replace("{" + entry.getKey() + "}", entry.getValue());
//         }
//         return result;
//     }
//
//     /**
//      * 格式化列表
//      */
//     public static String formatList(String title, String... items) {
//         StringBuilder sb = new StringBuilder();
//         sb.append("<b>").append(title).append("</b>\n\n");
//         for (int i = 0; i < items.length; i++) {
//             sb.append((i + 1)).append(". ").append(items[i]).append("\n");
//         }
//         return sb.toString();
//     }
//
//     /**
//      * 格式化键值对
//      */
//     public static String formatKeyValue(String title, Map<String, String> data) {
//         StringBuilder sb = new StringBuilder();
//         sb.append("<b>").append(title).append("</b>\n\n");
//         for (Map.Entry<String, String> entry : data.entrySet()) {
//             sb.append("• ").append(entry.getKey()).append(": ")
//               .append(entry.getValue()).append("\n");
//         }
//         return sb.toString();
//     }
// }