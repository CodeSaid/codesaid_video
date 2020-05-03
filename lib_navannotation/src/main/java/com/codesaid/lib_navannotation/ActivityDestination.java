package com.codesaid.lib_navannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created By codesaid
 * On :2020-05-04 02:03
 * Package Name: com.codesaid.lib_navannotation
 * desc:
 *
 * @author codesaid
 */

@Target(ElementType.TYPE)
public @interface ActivityDestination {

    /**
     * 页面 url
     */
    String pageUrl();

    /**
     * 是否需要登录
     */
    boolean needLogin() default false;

    /**
     * 是否作为启动页面
     */
    boolean asStarter() default false;
}
