package com.codesaid.ui;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-20 15:34
 * Package Name: com.codesaid.ui
 * desc:
 */
public abstract class MutableItemKeyDataSource<Key, Value> extends ItemKeyedDataSource<Key, Value> {

    private ItemKeyedDataSource mDataSource;

    public List<Value> data = new ArrayList<>();

    public PagedList<Value> buildNewPagedList(PagedList.Config config) {
        PagedList<Value> pagedList = new PagedList.Builder<Key, Value>(this, config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();

        return pagedList;
    }

    public MutableItemKeyDataSource(ItemKeyedDataSource dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Value> callback) {
        callback.onResult(data);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        mDataSource.loadAfter(params, callback);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        callback.onResult(Collections.emptyList());
    }

    @NonNull
    @Override
    public abstract Key getKey(@NonNull Value item);
}
