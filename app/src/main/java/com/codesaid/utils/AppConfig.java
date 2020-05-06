package com.codesaid.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.codesaid.lib_base.AppGlobals;
import com.codesaid.model.BottomBar;
import com.codesaid.model.Destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created By codesaid
 * On :2020-05-04 13:37
 * Package Name: com.codesaid.utils
 * desc:
 *
 * @author codesaid
 */
public class AppConfig {


    private static HashMap<String, Destination> sDesConfig;

    private static BottomBar sBottomBar;

    public static HashMap<String, Destination> getDesConfig() {
        if (sDesConfig == null) {
            String content = parseFile("destination.json");
            sDesConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }

        return sDesConfig;
    }

    public static BottomBar getBottomBar() {
        if (sBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }

        return sBottomBar;
    }

    /**
     * 解析 assets 文件
     *
     * @param fileName 文件名
     * @return
     */
    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();

        InputStream stream = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            stream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
