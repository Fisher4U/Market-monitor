package com.qinyadan.monitor.utils;

public final class StringUtil {
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || str.length() == 0) {
            return true;
        }
        return false;
    }
}
