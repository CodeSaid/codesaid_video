package com.codesaid.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.lib_network.request.Request;
import com.codesaid.model.Feed;
import com.codesaid.ui.AbsViewModel;
import com.codesaid.ui.MutableDataSource;
import com.codesaid.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AbsViewModel<Feed> {

    private MutableLiveData<PagedList<Feed>> mCacheLiveData = new MutableLiveData<>();

    private volatile boolean withCache = true;
    private String TAG = "HomeViewModel";

    private AtomicBoolean loadAfter = new AtomicBoolean(false);

    @Override
    public DataSource createDataSource() {
        return mDataSource;
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return mCacheLiveData;
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

        if (key > 0) {
            loadAfter.set(true);
        }

        //feeds/queryHotFeedsList
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", null)
                .addParam("userId", UserManager.getInstance().getUserId())
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
                    MutableDataSource dataSource = new MutableDataSource<Integer, Feed>();
                    dataSource.mList.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPageList(mConfig);
                    mCacheLiveData.postValue(pagedList);
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
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {

        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, callback);
            }
        });
    }
}