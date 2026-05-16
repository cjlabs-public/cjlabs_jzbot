package com.cjlabs.jzbot.tg.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * 按钮构建器（非单例，每次使用创建新实例）
 */
public class ButtonBuilder {

    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> currentRow = new ArrayList<>();

    /**
     * 添加回调按钮
     */
    public ButtonBuilder addCallbackButton(String text, String callbackData) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
        currentRow.add(button);
        return this;
    }

    /**
     * 添加 URL 按钮
     */
    public ButtonBuilder addUrlButton(String text, String url) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(text)
                .url(url)
                .build();
        currentRow.add(button);
        return this;
    }

    /**
     * 添加切换内联查询按钮
     */
    public ButtonBuilder addSwitchInlineButton(String text, String query) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(text)
                .switchInlineQuery(query)
                .build();
        currentRow.add(button);
        return this;
    }

    /**
     * 换行
     */
    public ButtonBuilder newRow() {
        if (!currentRow.isEmpty()) {
            keyboard.add(new ArrayList<>(currentRow));
            currentRow.clear();
        }
        return this;
    }

    /**
     * 构建键盘
     */
    public InlineKeyboardMarkup build() {
        if (!currentRow.isEmpty()) {
            keyboard.add(new ArrayList<>(currentRow));
            currentRow.clear();
        }

        // 将 List<List<InlineKeyboardButton>> 转换为 List<InlineKeyboardRow>
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        for (List<InlineKeyboardButton> row : keyboard) {
            keyboardRows.add(new InlineKeyboardRow(row));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .build();

        // 清理状态，允许重用（虽然不推荐）
        keyboard.clear();

        return markup;
    }

    /**
     * 创建单行按钮
     */
    public static InlineKeyboardMarkup createSingleRow(String... buttonTexts) {
        ButtonBuilder builder = new ButtonBuilder();
        for (int i = 0; i < buttonTexts.length; i += 2) {
            if (i + 1 < buttonTexts.length) {
                builder.addCallbackButton(buttonTexts[i], buttonTexts[i + 1]);
            }
        }
        return builder.build();
    }

    /**
     * 创建确认/取消按钮
     */
    public static InlineKeyboardMarkup createConfirmCancel(String confirmCallback, String cancelCallback) {
        return new ButtonBuilder()
                .addCallbackButton("✅ 确认", confirmCallback)
                .addCallbackButton("❌ 取消", cancelCallback)
                .build();
    }

    /**
     * 创建分页按钮
     */
    public static InlineKeyboardMarkup createPagination(int currentPage, int totalPages, String callbackPrefix) {
        ButtonBuilder builder = new ButtonBuilder();

        if (currentPage > 1) {
            builder.addCallbackButton("⬅️ 上一页", callbackPrefix + "_" + (currentPage - 1));
        }

        builder.addCallbackButton(currentPage + "/" + totalPages, "page_info");

        if (currentPage < totalPages) {
            builder.addCallbackButton("➡️ 下一页", callbackPrefix + "_" + (currentPage + 1));
        }

        return builder.build();
    }
}