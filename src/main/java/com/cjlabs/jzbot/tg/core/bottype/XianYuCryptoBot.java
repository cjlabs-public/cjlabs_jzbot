package com.cjlabs.jzbot.tg.core.bottype;


import com.cjlabs.localbaby.tg.config.BotConfig;
import com.cjlabs.localbaby.tg.core.LongPollingTelegramBot;
import com.cjlabs.localbaby.tg.core.router.UpdateRouter;
import lombok.extern.slf4j.Slf4j;

/**
 * 群组管理机器人
 * 提供群组管理、成员管理、消息过滤等功能
 */
@Slf4j
public class XianYuCryptoBot extends LongPollingTelegramBot {

    public XianYuCryptoBot(BotConfig botConfig, UpdateRouter updateRouter) {
        super(botConfig, updateRouter);
        log.info("XianYuCryptoBot initialized: {}", botConfig.getBotName());
    }

}

