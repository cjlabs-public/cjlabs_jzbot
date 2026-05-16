package com.cjlabs.jzbot.tg.core;

import com.cjlabs.localbaby.tg.config.BotConfig;
import com.cjlabs.localbaby.tg.core.bottype.XianYuCryptoBot;
import com.cjlabs.localbaby.tg.core.router.UpdateRouter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bot 管理器 (TelegramBots 9.2.0 版本)
 * 负责加载、创建、注册和管理所有 Telegram 机器人
 */
@Slf4j
@Service
public class BotManager implements ApplicationListener<ApplicationReadyEvent> {
    @Getter
    private Map<String, LongPollingTelegramBot> bots = new ConcurrentHashMap<>();
    @Getter
    private TelegramBotsLongPollingApplication botsApi;
    @Getter
    private UpdateRouter updateRouter;
    @Autowired
    private BotConfig botConfig;
    /**
     * chat ID 到 bot username 的映射
     */
    private Map<Long, String> chatToBotMapping = new ConcurrentHashMap<>();

    public BotManager(TelegramBotsLongPollingApplication botsApi) {
        this.botsApi = botsApi;
    }

    /**
     * 使用 Setter 注入 UpdateRouter，避免循环依赖
     */
    @Autowired
    public void setUpdateRouter(UpdateRouter updateRouter) {
        this.updateRouter = updateRouter;
    }

    /**
     * 注册 chat 到 bot 的映射
     */
    public void putChat(Long chatId, String botUsername) {
        chatToBotMapping.put(chatId, botUsername);
    }

    /**
     * 注册 chat 到 bot 的映射
     */
    public String getBotUsernameByChat(Long chatId) {
        return chatToBotMapping.get(chatId);
    }

    /**
     * 获取 chat 对应的 bot
     */
    public LongPollingTelegramBot getBotByChat(Long chatId) {
        String botUsername = chatToBotMapping.get(chatId);
        if (botUsername != null) {
            return bots.get(botUsername);
        }
        return null;
    }

    public void registerBot(BotConfig botConfig) throws TelegramApiException {
        LongPollingTelegramBot bot = createBot(botConfig);
        botsApi.registerBot(bot.getBotToken(), bot.getUpdatesConsumer());
        bots.put(botConfig.getUsername(), bot);
        log.info("Bot registered: {} ({})", botConfig.getUsername(), botConfig.getToken());
    }

    public void unregisterBot(String username) {
        LongPollingTelegramBot bot = bots.remove(username);
        if (bot != null) {
            log.info("Bot unregistered: {}", bot.getBotUsername());
        }
    }

    private LongPollingTelegramBot createBot(BotConfig config) {
        // return switch (config.getBotType()) {
        //     case GROUP_MANAGE -> new GroupManageBot(config, updateRouter);
        //     case BRIDGE -> new BridgeBot(config, updateRouter);
        //     case UTILITY -> new UtilityBot(config, updateRouter);
        //     default -> throw new IllegalArgumentException("Unknown bot type: " + config.getBotType());
        // };

        return new XianYuCryptoBot(config, updateRouter);
    }

    public LongPollingTelegramBot getBot(Long botId) {
        return bots.get(botId);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // ✅ 整个 Spring 上下文已加载
        // ✅ 所有 Bean 都已初始化
        // ✅ 数据库连接完全就绪
        // ✅ 应用服务器完全启动
        // ✅ 只触发一次
        log.info("Initializing bots...");

        // // 从数据库加载所有启用的机器人
        // List<TgBot> enabledBots = botWrapMapper.listAllLimitService();
        //
        // for (TgBot botConfig : enabledBots) {
        try {
            registerBot(botConfig);
        } catch (Exception e) {
            log.error("Failed to initialize bot: {}", botConfig.getUsername(), e);
        }
        log.info("Bots initialized successfully, total: {}", bots.size());
    }
}