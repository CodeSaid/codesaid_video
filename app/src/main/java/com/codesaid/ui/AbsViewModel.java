package com.codesaid.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;


/**
 * Created By codesaid
 * On :2020-05-08 00:56
 * Package Name: com.codesaid.ui
 * desc:
 */
public abstract class AbsViewModel<T> extends ViewModel {

    private DataSource mDataSource;
    private final LiveData<PagedList<T>> mLiveData;

    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();
    protected PagedList.Config mConfig;

    @SuppressWarnings("unchecked")
    public AbsViewModel() {
        mConfig = new PagedList.Config.Builder()
                .setPageSize(2)
                .setInitialLoadSizeHint(3)
                //.setEnablePlaceholders(false)
                .build();

        mLiveData = new LivePagedListBuilder(mFactory, mConfig)
                .setInitialLoadKey(0)
                .setBoundaryCallback(callback)
                .build();
    }

    public LiveData<PagedList<T>> getLiveData() {
        return mLiveData;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public MutableLiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    PagedList.BoundaryCallback<T> callback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
        }
    };


    private DataSource.Factory mFactory = new DataSource.Factory() {
        @Override
        public DataSource create() {
            if (mDataSource == null || mDataSource.isInvalid()) {
                mDataSource = createDataSource();
            }
            return mDataSource;
        }
    };

    public abstract DataSource createDataSource();
}
