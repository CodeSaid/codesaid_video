package com.codesaid.lib_network;

/**
 * Created By codesaid
 * On :2020-05-05 01:25
 * Package Name: com.codesaid.lib_network
 * desc:
 */
public class ApiResponse<T> {

    public boolean success;
    public int status;
    public String message;
    public T body;
}
