package com.platform.common.utils;

public class StringsUtils {
    /**
     * 手机号安全显示
     * @param phoneNumber
     * @return
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("\\d{11}")) {
            // 如果手机号不是11位数字，返回原字符串或抛出异常
            return phoneNumber;
        }

        // 使用正则表达式替换中间6位数字为星号
        return phoneNumber.replaceAll("(\\d{2})\\d{6}(\\d{3})", "$1******$2");
    }
}
