package com.codesaid.lib_network.request;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.Convert;
import com.codesaid.lib_network.UrlCreator;
import com.codesaid.lib_network.cache.CacheManager;
import com.codesaid.lib_network.callback.JsonCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
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
public abstract class Request<T, R extends Request> implements Cloneable{

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
    private int mCacheStrategy;

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

    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
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
    public ApiResponse<T> execute() {

        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }

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
    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback<T> callback) {

        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback != null) {
                        callback.onCacheSuccess(response);
                    }
                }
            });
        }

        if (mCacheStrategy != CACHE_ONLY) {
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
    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.message = "缓存获取成功";
        result.body = (T) cache;
        result.success = true;
        return result;
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

        if (mCacheStrategy != NET_ONLY
                && apiResponse.success
                && apiResponse.body != null
                && apiResponse.body instanceof Serializable) {
            saveCache(apiResponse.body);
        }

        return apiResponse;
    }

    /**
     * 缓存数据
     *
     * @param body 数据
     */
    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        CacheManager.save(key, body);
    }

    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
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
