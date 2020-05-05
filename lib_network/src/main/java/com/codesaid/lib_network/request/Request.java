package com.codesaid.lib_network.request;

import android.util.Log;

import androidx.annotation.IntDef;

import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.Convert;
import com.codesaid.lib_network.callback.JsonCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created By codesaid
 * On :2020-05-05 00:56
 * Package Name: com.codesaid.lib_network
 * desc:
 */
@SuppressWarnings("unchecked")
public abstract class Request<T, R extends Request> {

    private static final String TAG = "Request";
    protected String mUrl;
    private String cacheKey;


    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    // 仅仅只访问本地缓存，即便本地缓存不存在，也不发起网络请求
    public static final int CACHE_ONLY = 1;
    // 先访问缓存，同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    // 仅仅只访问网络，不存储
    public static final int NET_ONLY = 3;
    // 先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;
    private Type mType;
    private Class mClazz;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    public @interface CacheStrategy {

    }

    public Request(String url) {
        this.mUrl = url;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addHeaders(HashMap<String, String> headers) {
        headers.putAll(headers);
        return (R) this;
    }

    public R addParam(String key, Object value) {

        try {
            Field field = value.getClass().getField("TYPE");
            Class clazz = (Class) field.get(null);
            if (clazz.isPrimitive()) {
                params.put(key, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (R) this;
    }

    public R cacheKey(String key) {

        this.cacheKey = key;

        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }

    public R responseType(Class clazz) {
        mClazz = clazz;
        return (R) this;
    }

    /**
     * 同步请求
     */
    public ApiResponse<T> excute() {
        try {
            Response response = getCall().execute();
            ApiResponse<T> apiResponse = parseResponse(response, null);
            return apiResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 异步请求
     *
     * @param callback
     */
    public void excute(final JsonCallback<T> callback) {
        getCall().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApiResponse<T> response = new ApiResponse<>();
                response.message = e.getMessage();
                callback.onError(response);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ApiResponse<T> apiResponse = parseResponse(response, callback);
                if (!apiResponse.success) {
                    callback.onError(apiResponse);
                } else {
                    callback.onSuccess(apiResponse);
                }
            }
        });
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> apiResponse = new ApiResponse<>();
        Convert convert = ApiService.sConvert;

        try {
            String result = response.body().string();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    apiResponse.body = (T) convert.convert(result, argument);
                } else if (mType != null) {
                    apiResponse.body = (T) convert.convert(result, mType);
                } else if (mClazz != null) {
                    apiResponse.body = (T) convert.convert(result, mClazz);
                } else {
                    Log.e(TAG, "parseResponse: 无法解析");
                }
            } else {
                message = result;
            }
        } catch (IOException e) {
            message = e.getMessage();
            success = false;
            e.printStackTrace();
        }

        apiResponse.success = success;
        apiResponse.status = status;
        apiResponse.message = message;

        return apiResponse;
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.OK_HTTP_CLIENT.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }
}
