package com.platform.modules.chat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 好友类型
 */
@Getter
public enum FriendTypeEnum {

    /**
     * 正常
     */
    NORMAL("normal", "正常"),
    /**
     * 天气机器人
     */
    WEATHER("weather", "在线客服1"),
    /**
     * 翻译机器人
     */
    TRANSLATION("translation", "在线客服2"),

    /**
     * 在线客服
     */
    SYSTEM_CUSTOMER("customer_service", "在线客服" ),

    /**
     * 自己
     */
    SELF("self", "自己"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    FriendTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
