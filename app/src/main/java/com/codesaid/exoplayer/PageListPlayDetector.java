package com.codesaid.exoplayer;

import android.graphics.Point;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-12 01:00
 * Package Name: com.codesaid.exoplayer
 * desc:
 */
public class PageListPlayDetector {

    private List<IPlayerListener> mListeners = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private IPlayerListener mPlayingListener;

    public void addListener(IPlayerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(IPlayerListener listener) {
        mListeners.remove(listener);
    }

    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;

        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    recyclerView.getAdapter().unregisterAdapterDataObserver(mDataObserver);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });

        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    autoPlay();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mPlayingListener != null && mPlayingListener.isPlaying() && !isListenerBounds(mPlayingListener)) {
                    mPlayingListener.inActive();
                }
            }
        });
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            autoPlay();
        }
    };

    private void autoPlay() {
        if (mListeners.size() <= 0 || mRecyclerView.getChildCount() <= 0) {
            return;
        }

        if (mPlayingListener != null && mPlayingListener.isPlaying() && isListenerBounds(mPlayingListener)) {
            return;
        }

        IPlayerListener activeListener = null;
        for (IPlayerListener listener : mListeners) {
            boolean bounds = isListenerBounds(listener);
            if (bounds) {
                activeListener = listener;
                break;
            }
        }

        if (activeListener != null) {
            if (mPlayingListener != null && mPlayingListener.isPlaying()) {
                mPlayingListener.inActive();
            }
            mPlayingListener = activeListener;
            mPlayingListener.onActive();
        }
    }

    private boolean isListenerBounds(IPlayerListener listener) {
        ViewGroup viewGroup = listener.getOwner();
        getRecyclerViewLocation();
        if (!viewGroup.isShown() || !viewGroup.isAttachedToWindow()) {
            return false;
        }
        int[] location = new int[2];
        viewGroup.getLocationOnScreen(location);

        int center = location[1] + viewGroup.getHeight() / 2;

        return center >= recyclerLocation.x && center <= recyclerLocation.y;
    }

    private Point recyclerLocation = null;

    private Point getRecyclerViewLocation() {
        if (recyclerLocation == null) {
            int[] location = new int[2];
            mRecyclerView.getLocationOnScreen(location);

            int top = location[1];
            int bottom = top + mRecyclerView.getHeight();

            recyclerLocation = new Point(top, bottom);
        }
        return recyclerLocation;
    }

    public void onResume() {
        if (mPlayingListener != null) {
            mPlayingListener.onActive();
        }
    }

    public void onPause() {
        if (mPlayingListener != null) {
            mPlayingListener.inActive();
        }
    }
}
