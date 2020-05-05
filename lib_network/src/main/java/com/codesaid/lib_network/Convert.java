package com.codesaid.lib_network;

import java.lang.reflect.Type;

/**
 * Created By codesaid
 * On :2020-05-05 02:13
 * Package Name: com.codesaid.lib_network
 * desc:
 */
public interface Convert<T> {

    T convert(String response, Type type);

    T convert(String response, Class clazz);
}
