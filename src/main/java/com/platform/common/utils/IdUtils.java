package com.platform.common.utils;

import cn.hutool.core.util.RandomUtil;

public class IdUtils {

    public static Long getRandomId(){
        return Long.parseLong(String.valueOf(RandomUtil.randomInt(1000000000, 2147483646)));
    }
}
