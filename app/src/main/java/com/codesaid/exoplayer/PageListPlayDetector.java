package com.codesaid.exoplayer;

import android.util.Pair;
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
        mRecyclerView = recyclerView;

        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    mPlayingListener = null;
                    mListeners.clear();
                    mRecyclerView.removeCallbacks(delayAutoPlay);
                    recyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });
        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        recyclerView.addOnScrollListener(scrollListener);

    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dx == 0 && dy == 0) {
                //时序问题。当执行了AdapterDataObserver#onItemRangeInserted  可能还没有被布局到RecyclerView上。
                //所以此时 recyclerView.getChildCount()还是等于0的。
                //等childView 被布局到RecyclerView上之后，会执行onScrolled（）方法
                //并且此时 dx,dy都等于0
                postAutoPlay();
            } else {
                //如果有正在播放的,且滑动时被划出了屏幕 则 停止他
                if (mPlayingListener != null && mPlayingListener.isPlaying() && !isTargetInBounds(mPlayingListener)) {
                    mPlayingListener.inActive();
                }
            }
        }
    };

    private void postAutoPlay() {
        mRecyclerView.post(delayAutoPlay);
    }

    Runnable delayAutoPlay = new Runnable() {
        @Override
        public void run() {
            autoPlay();
        }
    };

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            postAutoPlay();
        }
    };

    private void autoPlay() {
        if (mListeners.size() <= 0 || mRecyclerView.getChildCount() <= 0) {
            return;
        }

        if (mPlayingListener != null && mPlayingListener.isPlaying() && isTargetInBounds(mPlayingListener)) {
            return;
        }

        IPlayerListener activeTarget = null;
        for (IPlayerListener target : mListeners) {

            boolean inBounds = isTargetInBounds(target);
            if (inBounds) {
                activeTarget = target;
                break;
            }
        }

        if (activeTarget != null) {
            if (mPlayingListener != null) {
                mPlayingListener.inActive();
            }
            mPlayingListener = activeTarget;
            activeTarget.onActive();
        }
    }

    /**
     * 检测 IPlayTarget 所在的 viewGroup 是否至少还有一半的大小在屏幕内
     *
     * @param target
     * @return
     */
    private boolean isTargetInBounds(IPlayerListener target) {
        ViewGroup owner = target.getOwner();
        ensureRecyclerViewLocation();
        if (!owner.isShown() || !owner.isAttachedToWindow()) {
            return false;
        }

        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        int center = location[1] + owner.getHeight() / 2;

        //承载视频播放画面的ViewGroup它需要至少一半的大小 在RecyclerView上下范围内
        return center >= rvLocation.first && center <= rvLocation.second;
    }

    private Pair<Integer, Integer> rvLocation = null;

    private Pair<Integer, Integer> ensureRecyclerViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            mRecyclerView.getLocationOnScreen(location);

            int top = location[1];
            int bottom = top + mRecyclerView.getHeight();

            rvLocation = new Pair(top, bottom);
        }
        return rvLocation;
    }

    public void onPause() {
        if (mPlayingListener != null) {
            mPlayingListener.inActive();
        }
    }

    public void onResume() {
        if (mPlayingListener != null) {
            mPlayingListener.onActive();
        }
    }
}
