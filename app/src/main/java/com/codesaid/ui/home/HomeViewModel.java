package com.codesaid.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.lib_network.request.Request;
import com.codesaid.model.Feed;
import com.codesaid.ui.AbsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean withCache = true;
    private String TAG = "HomeViewModel";

    @Override
    public DataSource createDataSource() {

        return mDataSource;
    }

    ItemKeyedDataSource<Integer, Feed> mDataSource = new ItemKeyedDataSource<Integer, Feed>() {
        /**
         * 加载初始化数据
         * @param params
         * @param callback
         */
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(0, callback);
            withCache = false;
        }

        /**
         * 加载分页数据
         * @param params
         * @param callback
         */
        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, callback);
        }

        /**
         * 向前加载数据
         * @param params
         * @param callback
         */
        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    };

    private void loadData(int key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        //feeds/queryHotFeedsList
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", null)
                .addParam("userId", 0)
                .addParam("feedId", key)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if (withCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {

                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e(TAG, "onCacheSuccess: " + response.body.size());
                    List<Feed> body = response.body;
                }
            });
        }

        try {
            Request netRequest = withCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> listData = response.body == null ? Collections.emptyList() : response.body;

            callback.onResult(listData);

            if (key > 0) {
                // 通过 liveData 发送数据 告诉 UI 是否应该主动关闭上拉加载出现的分页动画
                getBoundaryPageData().postValue(listData.size() > 0);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}