package com.codesaid.utils;

/**
 * Created By codesaid
 * On :2020-05-06 17:06
 * Package Name: com.codesaid.utils
 * desc:
 */
public class StringConvert {

    public static String convertFeedUgc(int count) {
        if (count < 10000) {
            return String.valueOf(count);
        }
        return count / 10000 + "万";
    }
}
