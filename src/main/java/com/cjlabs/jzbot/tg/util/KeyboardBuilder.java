package com.cjlabs.jzbot.tg.util;

import com.cjlabs.localbaby.tg.domain.bo.group.GroupConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class KeyboardBuilder {

    public InlineKeyboardMarkup buildSettingsKeyboard(GroupConfig config) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        // 第一行：语言和时区
        keyboard.add(new InlineKeyboardRow(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text("🌐 语言: " + config.getLanguage().getMsg())
                        .callbackData("settings:language")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("🕐 时区: " + config.getTimezone().getMsg())
                        .callbackData("settings:timezone")
                        .build()
        )));

        // 第二行：欢迎消息
        keyboard.add(new InlineKeyboardRow(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(config.getWelcomeEnabled() ? "✅ 欢迎消息" : "❌ 欢迎消息")
                        .callbackData("settings:welcome:toggle")
                        .build()
        )));

        // 第三行：关键词过滤
        keyboard.add(new InlineKeyboardRow(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(config.getFilterEnabled() ? "✅ 关键词过滤" : "❌ 关键词过滤")
                        .callbackData("settings:filter:toggle")
                        .build()
        )));

        // 第四行：入群验证
        keyboard.add(new InlineKeyboardRow(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(config.getVerificationEnabled() ? "✅ 入群验证" : "❌ 入群验证")
                        .callbackData("settings:verification:toggle")
                        .build()
        )));

        // 第五行：签到和抽奖
        keyboard.add(new InlineKeyboardRow(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(config.getCheckinEnabled() ? "✅ 签到" : "❌ 签到")
                        .callbackData("settings:checkin:toggle")
                        .build(),
                InlineKeyboardButton.builder()
                        .text(config.getLotteryEnabled() ? "✅ 抽奖" : "❌ 抽奖")
                        .callbackData("settings:lottery:toggle")
                        .build()
        )));

        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }
}