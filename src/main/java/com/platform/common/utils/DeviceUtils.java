package com.platform.common.utils;

import javax.servlet.http.HttpServletRequest;

public class DeviceUtils {

    public static Integer detectDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.contains("android")) {
                return 1;
            } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
                return 2;
            } else {
                return 3;
            }
        }
        return 4;
    }
}
