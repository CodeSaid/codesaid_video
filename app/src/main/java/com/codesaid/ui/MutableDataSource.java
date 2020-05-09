package com.codesaid.ui;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-09 21:35
 * Package Name: com.codesaid.ui
 * desc:
 */
public class MutableDataSource<Key, Value> extends PageKeyedDataSource<Key, Value> {

    public List<Value> mList = new ArrayList<>();

    public PagedList<Value> buildNewPageList(PagedList.Config config) {

        return new PagedList.Builder<Key, Value>(this, config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Key, Value> callback) {
        callback.onResult(mList, null, null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Key, Value> callback) {
        callback.onResult(Collections.emptyList(), null);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Key, Value> callback) {
        callback.onResult(Collections.emptyList(), null);
    }
}
