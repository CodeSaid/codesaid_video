package com.codesaid.lib_network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

/**
 * Created By codesaid
 * On :2020-05-05 02:15
 * Package Name: com.codesaid.lib_network
 * desc:
 */
public class JsonConvert implements Convert {


    @Override
    public Object convert(String response, Type type) {

        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            Object obj = data.get("data");
            return JSON.parseObject(obj.toString(), type);
        }

        return null;
    }

    @Override
    public Object convert(String response, Class clazz) {

        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            Object obj = data.get("data");
            return JSON.parseObject(obj.toString(), clazz);
        }

        return null;
    }
}
