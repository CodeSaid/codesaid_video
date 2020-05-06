package com.codesaid.lib_base;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created By codesaid
 * On :2020-05-04 13:38
 * Package Name: com.codesaid.utils
 * desc: 获取 Application
 */
public class AppGlobals {

    private static Application sApplication;


    /**
     * 获取全局 Application 对象
     *
     * @return Application
     */
    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    public static Application getApplication() {
        if (sApplication == null) {
            try {
                Method method = Class.forName("android.app.ActivityThread")
                        .getDeclaredMethod("currentApplication");
                sApplication = (Application) method.invoke(null, (Object[]) null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return sApplication;
    }
}
