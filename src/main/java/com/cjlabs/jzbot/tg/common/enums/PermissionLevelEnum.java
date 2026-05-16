package com.cjlabs.jzbot.tg.common.enums;

import com.cjlabs.domain.enums.IEnumStr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionLevelEnum implements IEnumStr {
    OWNER("OWNER", "群主", 100),
    ADMIN("ADMIN", "管理员", 80),
    MODERATOR("MODERATOR", "版主", 60),
    MEMBER("MEMBER", "成员", 40),
    RESTRICTED("RESTRICTED", "受限用户", 20),
    BANNED("BANNED", "封禁用户", 0);

    private final String code;
    private final String msg;
    private final int level;

    public String getDescription() {
        return msg;
    }

    public boolean hasAtLeast(PermissionLevelEnum requiredLevel) {
        return requiredLevel != null && level >= requiredLevel.level;
    }
}
