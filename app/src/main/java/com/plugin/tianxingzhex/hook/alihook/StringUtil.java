package com.plugin.tianxingzhex.hook.alihook;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Created by zl on 2014/12/2.
 */
public class StringUtil {
    /**
     * 判断给定字符串是否空白串。
     *
     * @param input 字符串
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        return input == null || 0 == TextUtils.getTrimmedLength(input);
    }

    /**
     * 特殊符号转译
     */
    public static String replaceSpechars(String str) {
        if (null != str) {
            str = str.replace("&", "&amp;");
            str = str.replace("<", "&lt;");
            str = str.replace(">", "&gt;");
            return str;
        } else {
            return null;
        }
    }

    /**
     * 16进制字符转byte数组 并检验合法性 含有非十六进制会返回null
     */
    public static byte[] hexToByteArray(String messagePart) {
        if (messagePart == null) return null;
        String message = messagePart.replaceAll(" ", "").toUpperCase();
        byte[] messageBytes = new byte[message.length() / 2];
        for (int i = 0; i < message.length(); i++) {
            //与'0'和'9'比较，不是0,9.
            char c = message.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F')) {
                return null;
            }
        }
        if ((message.length() & 1) == 1) return null;
        int length = message.length() >> 1;
        for (int i = 0; i < length; i++) {
            messageBytes[i] = (byte) Integer.parseInt(message.substring(i * 2, i * 2 + 2), 16);
        }
        return messageBytes;
    }

    /**
     * byte数组转16进制字符
     */
    public static String byteArrayToHex(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String bs = String.format("%02X", b);
            sb.append(bs);
        }
        return sb.toString();
    }

    /**
     * byte数组转ASCII字符
     */
    public static String byteArrayToASCII(byte[] array) {
        if (array == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte byteChar : array) {
            if (byteChar >= 32 && byteChar < 127) {
                sb.append(String.format("%c", byteChar));
            } else {
                sb.append(String.format("%d ", byteChar & 0xFF));
            }
        }
        return sb.toString();
    }

    public static String getTextCenter(String text, String begin, String end) {
        try {
            int b = text.indexOf(begin) + begin.length();
            int e = text.indexOf(end, b);
            return text.substring(b, e);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static String getUrlParam(String url, String key) {
        if (StringUtil.isEmpty(url)) return null;
        int index = url.indexOf("?");
        if (index > -1)
            url = url.substring(index);
        String params[] = url.split("&");
        for (String param : params) {
            if (param.startsWith(key)) {
                return param.substring(key.length() + 1);
            }
        }
        return null;
    }

    public static CharSequence createBiggerColorText(int color, String text) {
        if (StringUtil.isEmpty(text)) return "";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(2.0f), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static CharSequence createColorText(int color, String text) {
        if (StringUtil.isEmpty(text)) return "";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
}
