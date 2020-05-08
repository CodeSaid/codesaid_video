package com.codesaid.lib_base;

import android.util.DisplayMetrics;

/**
 * Created By codesaid
 * On :2020-05-07 15:20
 * Package Name: com.codesaid.lib_base
 * desc:
 */
public class PixUtils {

    public static int dp2pix(int dpValue) {
        DisplayMetrics displayMetrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dpValue + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
