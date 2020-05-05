package com.codesaid.lib_network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created By codesaid
 * On :2020-05-05 01:36
 * Package Name: com.codesaid.lib_network
 * desc: 作用于给 get 请求 拼接参数
 */
public class UrlCreator {

    public static String createUrlFromParams(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);

        if (url.indexOf("?") > 0 || url.indexOf("&") > 0) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8");
                sb.append(entry.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
