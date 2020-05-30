package com.codesaid.ui.find;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.codesaid.model.TagList;
import com.codesaid.ui.AbsViewModel;

/**
 * Created By codesaid
 * On :2020-05-30 20:50
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagListViewModel extends AbsViewModel<TagList> {
    @Override
    public DataSource createDataSource() {
        return null;
    }

    private class DataSource extends ItemKeyedDataSource<Long, TagList> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<TagList> callback) {

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {

        }

        @NonNull
        @Override
        public Long getKey(@NonNull TagList item) {
            return null;
        }
    }
}
