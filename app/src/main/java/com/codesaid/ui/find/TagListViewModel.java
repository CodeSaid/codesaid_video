package com.codesaid.ui.find;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.model.TagList;
import com.codesaid.ui.AbsViewModel;
import com.codesaid.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created By codesaid
 * On :2020-05-30 20:50
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagListViewModel extends AbsViewModel<TagList> {

    private String tagType;
    private int offset;

    private AtomicBoolean loadAfter = new AtomicBoolean();

    private MutableLiveData switchTabLiveData = new MutableLiveData();

    public MutableLiveData getSwitchTabLiveData() {
        return switchTabLiveData;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    private class DataSource extends ItemKeyedDataSource<Long, TagList> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<TagList> callback) {
            loadData(0L, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {
            loadData(params.key, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {
            callback.onResult(Collections.emptyList());
        }

        @SuppressWarnings("unchecked")
        private void loadData(Long requestedInitialKey, LoadCallback<TagList> callback) {

            if (requestedInitialKey > 0) {
                loadAfter.set(true);
            }

            ApiResponse<List<TagList>> response = ApiService.get("/tag/queryTagList")
                    .addParam("userId", UserManager.getInstance().getUserId())
                    .addParam("tagId", requestedInitialKey)
                    .addParam("tagType", tagType)
                    .addParam("pageCount", 10)
                    .addParam("offset", offset)
                    .responseType(new TypeReference<ArrayList<TagList>>() {
                    }.getType())
                    .execute();

            List<TagList> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);
            if (requestedInitialKey > 0) {
                loadAfter.set(false);
                offset += result.size();
                ((MutableLiveData) getBoundaryPageData()).postValue(result);
            } else {
                offset = result.size();
            }
        }


        @NonNull
        @Override
        public Long getKey(@NonNull TagList item) {
            return item.tagId;
        }
    }

    public void loadData(long tagId, ItemKeyedDataSource.LoadCallback callback) {
        if (tagId <= 0 || loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }

        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ((TagListViewModel.DataSource) getDataSource()).loadData(tagId, callback);
            }
        });
    }
}
