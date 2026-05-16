package com.cjlabs.jzbot.tg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionLevelEnum {
    OWNER("OWNER", "群主", 100),
    ADMIN("ADMIN", "管理员", 80),
    MODERATOR("MODERATOR", "版主", 60),
    MEMBER("MEMBER", "成员", 40),
    RESTRICTED("RESTRICTED", "受限用户", 20),
    BANNED("BANNED", "封禁用户", 0);

    private final String code;
    private final String description;
    private final int level;
}