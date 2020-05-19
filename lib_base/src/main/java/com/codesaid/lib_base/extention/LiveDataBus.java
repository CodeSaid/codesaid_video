package com.codesaid.lib_base.extention;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By codesaid
 * On :2020-05-20 00:59
 * Package Name: com.codesaid.lib_base.extention
 * desc:
 */
public class LiveDataBus {

    private volatile static LiveDataBus mInstance = null;

    private LiveDataBus() {

    }

    public static LiveDataBus getInstance() {
        if (mInstance == null) {
            synchronized (LiveDataBus.class) {
                if (mInstance == null) {
                    mInstance = new LiveDataBus();
                }
            }
        }
        return mInstance;
    }

    private ConcurrentHashMap<String, StickyLiveData> mHashMap = new ConcurrentHashMap<String, StickyLiveData>();

    public StickyLiveData with(String eventName) {
        StickyLiveData stickyLiveData = mHashMap.get(eventName);
        if (stickyLiveData == null) {
            stickyLiveData = new StickyLiveData(eventName);
            mHashMap.put(eventName, stickyLiveData);
        }
        return stickyLiveData;
    }

    public class StickyLiveData<T> extends LiveData<T> {

        private String mEventName;

        private T mStickyData;

        private int mVersion = 0;

        public StickyLiveData(String eventName) {
            mEventName = eventName;
        }

        @Override
        public void setValue(T value) {
            mVersion++;
            super.setValue(value);
        }

        @Override
        public void postValue(T value) {
            mVersion++;
            super.postValue(value);
        }

        public void setStickyData(T stickyData) {
            this.mStickyData = stickyData;
            setValue(stickyData);
        }

        public void postStickyData(T stickyData) {
            this.mStickyData = stickyData;
            postValue(stickyData);
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            observeSticky(owner, observer, false);
        }

        private void observeSticky(LifecycleOwner owner, Observer<? super T> observer, boolean sticky) {
            super.observe(owner, new WrapperObserver(this, observer, sticky));

            owner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        mHashMap.remove(mEventName);
                    }
                }
            });
        }

        private class WrapperObserver<T> implements Observer<T> {

            private StickyLiveData<T> mLiveData;
            private Observer<T> mObserver;
            private boolean mSticky;

            private int mLastVersion = 0;

            public WrapperObserver(StickyLiveData liveData, Observer<T> observer, boolean sticky) {

                mLiveData = liveData;
                mObserver = observer;
                mSticky = sticky;

                mLastVersion = mLiveData.mVersion;
            }

            @Override
            public void onChanged(T t) {
                if (mLastVersion > mLiveData.mVersion) {
                    if (mSticky && mLiveData.mStickyData != null) {
                        mObserver.onChanged(mLiveData.mStickyData);
                    }
                    return;
                }

                mLastVersion = mLiveData.mVersion;
                mObserver.onChanged(t);
            }
        }
    }
}
