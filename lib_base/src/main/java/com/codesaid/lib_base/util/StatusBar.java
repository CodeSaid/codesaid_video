package com.codesaid.lib_base.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created By codesaid
 * On :2020-05-13 01:08
 * Package Name: com.codesaid.lib_base.util
 * desc: 沉浸式状态栏
 */
public class StatusBar {

    public static void fixSystemBar(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN--能够使得我们的页面布局延伸到状态栏之下，
        // 但不会隐藏状态栏。也就相当于状态栏是遮盖在布局之上的

        //View.SYSTEM_UI_FLAG_FULLSCREEN -- 能够使得我们的页面布局延伸到状态栏，但是会隐藏状态栏。
        //WindowManager.LayoutParams.FLAG_FULLSCREEN
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}
