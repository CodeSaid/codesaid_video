package com.codesaid.ui.detail;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.R;
import com.codesaid.databinding.LayoutFeedDetailBottomInateractionBinding;
import com.codesaid.lib_base.view.EmptyView;
import com.codesaid.model.Comment;
import com.codesaid.model.Feed;
import com.codesaid.ui.MutableItemKeyDataSource;

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
    private final FeedDetailViewModel mViewModel;

    private EmptyView mEmptyView;
    private CommentDialog mCommentDialog;

    public ViewHandler(FragmentActivity activity) {

        mActivity = activity;
        mViewModel = ViewModelProviders.of(activity).get(FeedDetailViewModel.class);

    }

    @CallSuper
    public void bindInitData(Feed feed) {

        mInateractionBinding.setOwner(mActivity);

        mFeed = feed;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);
        mAdapter = new FeedCommentAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.setItemId(mFeed.itemId);
        mViewModel.getLiveData().observe(mActivity, new Observer<PagedList<Comment>>() {
            @Override
            public void onChanged(PagedList<Comment> comments) {
                mAdapter.submitList(comments);
                handleEmpty(comments.size() > 0);
            }
        });

        mInateractionBinding.inputView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentDialog == null) {
                    mCommentDialog = CommentDialog.newInstance(mFeed.itemId);
                }
                mCommentDialog.setCommentAddListener(new CommentDialog.CommentAddListener() {
                    @Override
                    public void onAddComment(Comment comment) {
                        MutableItemKeyDataSource<Integer, Comment> mutableItemKeyDataSource =
                                new MutableItemKeyDataSource<Integer, Comment>((ItemKeyedDataSource) mViewModel.getDataSource()) {
                                    @NonNull
                                    @Override
                                    public Integer getKey(@NonNull Comment item) {
                                        return item.id;
                                    }
                                };
                        mutableItemKeyDataSource.data.add(comment);
                        mutableItemKeyDataSource.data.addAll(mAdapter.getCurrentList());
                        PagedList<Comment> comments = mutableItemKeyDataSource.buildNewPagedList(mAdapter.getCurrentList().getConfig());
                        mAdapter.submitList(comments);
                    }
                });

                mCommentDialog.show(mActivity.getSupportFragmentManager(), "comment_dialog");
            }
        });
    }

    private void handleEmpty(boolean hasData) {
        if (hasData) {
            if (mEmptyView != null) {
                mAdapter.removeHeaderView(mEmptyView);
            }
        } else {
            if (mEmptyView == null) {
                mEmptyView = new EmptyView(mActivity);
                mEmptyView.setLayoutParams(new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mEmptyView.setEmptyText(mActivity.getString(R.string.feed_comment_empty));
                mAdapter.addHeaderView(mEmptyView);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCommentDialog != null && mCommentDialog.isAdded()) {
            mCommentDialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
