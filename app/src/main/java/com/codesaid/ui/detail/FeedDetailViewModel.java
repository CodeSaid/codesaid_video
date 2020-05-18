package com.codesaid.ui.detail;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.model.Comment;
import com.codesaid.ui.AbsViewModel;
import com.codesaid.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-18 14:22
 * Package Name: com.codesaid.ui.detail
 * desc:
 */
public class FeedDetailViewModel extends AbsViewModel<Comment> {

    private long itemId;

    @Override
    public DataSource createDataSource() {
        return null;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer, Comment> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Comment> callback) {
            loadData(params.requestedInitialKey, params.requestedLoadSize, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Comment> callback) {
            loadData(params.key, params.requestedLoadSize, callback);
        }

        private void loadData(Integer key, int size,
                              LoadCallback<Comment> callback) {
            ApiResponse<List<Comment>> response = ApiService.get("/comment/queryFeedComments")
                    .addParam("id", key)
                    .addParam("itemId", itemId)
                    .addParam("userId", UserManager.getInstance().getUserId())
                    .addParam("pageCount", size)
                    .responseType(new TypeReference<ArrayList<Comment>>() {
                    }.getType())
                    .execute();

            List<Comment> list = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(list);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Comment> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Comment item) {
            return item.id;
        }
    }
}
