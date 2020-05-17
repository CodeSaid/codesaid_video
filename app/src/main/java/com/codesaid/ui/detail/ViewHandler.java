package com.codesaid.ui.detail;

import androidx.annotation.CallSuper;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.databinding.LayoutFeedDetailBottomInateractionBinding;
import com.codesaid.model.Feed;

/**
 * Created By codesaid
 * On :2020-05-17 21:17
 * Package Name: com.codesaid.ui.detail
 * desc:
 */
public abstract class ViewHandler {

    protected FragmentActivity mActivity;
    protected Feed mFeed;
    protected RecyclerView mRecyclerView;
    protected LayoutFeedDetailBottomInateractionBinding mInateractionBinding;
    protected FeedCommentAdapter mAdapter;

    public ViewHandler(FragmentActivity activity) {

        mActivity = activity;
    }

    @CallSuper
    public void bindInitData(Feed feed) {
        mFeed = feed;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setItemAnimator(null);
        mAdapter = new FeedCommentAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
}
