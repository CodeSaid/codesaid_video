package com.codesaid.lib_network.callback;

import com.codesaid.lib_network.ApiResponse;

/**
 * Created By codesaid
 * On :2020-05-05 01:26
 * Package Name: com.codesaid.lib_network.callback
 * desc:
 */
public abstract class JsonCallback<T> {

    public void onSuccess(ApiResponse<T> response) {

    }

    public void onError(ApiResponse<T> response) {

    }

    public void onCacheSuccess(ApiResponse<T> response) {

    }
}
