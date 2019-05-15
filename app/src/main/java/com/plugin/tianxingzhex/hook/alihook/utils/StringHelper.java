package com.plugin.tianxingzhex.hook.alihook.utils;

import java.util.Random;

public class StringHelper {
    public static String getTextCenter(String text, String begin, String end) {
        try {
            int b = text.indexOf(begin) + begin.length();
            return text.substring(b, text.indexOf(end, b));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static String getRandomNumber(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(str.charAt(random.nextInt(9)));
        }
        return sb.toString();
    }
}
