package com.codesaid.ui.find;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.model.Feed;
import com.codesaid.ui.AbsViewModel;
import com.codesaid.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-31 01:17
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagFeedListViewModel extends AbsViewModel<Feed> {
    private String feedType;

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    private class DataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData(Integer requestedInitialKey, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        ApiResponse<List<Feed>> response = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("userId", UserManager.getInstance().getUserId())
                .addParam("pageCount", 10)
                .addParam("feedType", feedType)
                .addParam("feedId", requestedInitialKey)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType())
                .execute();

        List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
        callback.onResult(result);
    }
}
