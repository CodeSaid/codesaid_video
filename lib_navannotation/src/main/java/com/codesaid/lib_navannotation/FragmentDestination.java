package com.codesaid.lib_navannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created By codesaid
 * On :2020-05-04 02:01
 * Package Name: com.codesaid.lib_navannotation
 * desc:
 * @author codesaid
 */

@Target(ElementType.TYPE)
public @interface FragmentDestination {
    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
