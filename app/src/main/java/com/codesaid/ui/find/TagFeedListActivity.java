package com.codesaid.ui.find;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.R;
import com.codesaid.databinding.ActivityTagFeedListLayoutBinding;
import com.codesaid.databinding.TagFeedListHeaderLayoutBinding;
import com.codesaid.exoplayer.PageListPlayDetector;
import com.codesaid.exoplayer.PageListPlayManager;
import com.codesaid.lib_base.extention.AbsPagedListAdapter;
import com.codesaid.lib_base.util.PixUtils;
import com.codesaid.lib_base.view.EmptyView;
import com.codesaid.model.Feed;
import com.codesaid.model.TagList;
import com.codesaid.ui.home.FeedAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created By codesaid
 * On :2020-05-31 00:33
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagFeedListActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    public static final String KEY_TAG_LIST = "key_list";
    public static final String KEY_FEED_TYPE = "feed_type";

    private ActivityTagFeedListLayoutBinding mBinding;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private SmartRefreshLayout mRefreshLayout;
    private TagList mTagList;

    private PageListPlayDetector mPageDetector;

    private boolean showPause = true;
    private AbsPagedListAdapter mAdapter;
    private int totalScrollY;
    private TagFeedListHeaderLayoutBinding mHeaderBinding;
    private TagFeedListViewModel mTagFeedListViewModel;


    public static void startActivity(Context context, TagList tagList) {
        Intent intent = new Intent(context, TagFeedListActivity.class);
        intent.putExtra(KEY_TAG_LIST, tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tag_feed_list_layout);
        mRecyclerView = mBinding.refreshLayout.recyclerView;
        mEmptyView = mBinding.refreshLayout.emptyView;
        mRefreshLayout = mBinding.refreshLayout.refreshLayout;

        mBinding.actionBack.setOnClickListener(this);

        mTagList = (TagList) getIntent().getSerializableExtra(KEY_TAG_LIST);
        mBinding.setTagList(mTagList);
        mBinding.setOwner(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = (AbsPagedListAdapter) getAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mPageDetector = new PageListPlayDetector(this, mRecyclerView);

        addHeaderView();

        mTagFeedListViewModel = ViewModelProviders.of(this)
                .get(TagFeedListViewModel.class);
        mTagFeedListViewModel.setFeedType(mTagList.title);
        mTagFeedListViewModel.getLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });

        mTagFeedListViewModel.getBoundaryPageData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hasData) {
                finishRefresh(hasData);
            }
        });

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void submitList(PagedList<Feed> feeds) {
        if (feeds.size() > 0) {
            mAdapter.submitList(feeds);
        } else {
            finishRefresh(feeds.size() > 0);
        }
    }

    private void finishRefresh(boolean hasData) {
        PagedList currentList = mAdapter.getCurrentList();
        hasData = currentList != null && currentList.size() > 0 || hasData;

        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        RefreshState state = mRefreshLayout.getState();
        if (state.isOpening && state.isHeader) {
            mRefreshLayout.finishRefresh();
        } else if (state.isOpening && state.isFooter) {
            mRefreshLayout.finishLoadMore();
        }
    }

    private void addHeaderView() {
        mHeaderBinding = TagFeedListHeaderLayoutBinding.inflate(LayoutInflater.from(this), mRecyclerView, false);
        mHeaderBinding.setTagList(mTagList);
        mHeaderBinding.setOwner(this);
        mAdapter.addHeaderView(mHeaderBinding.getRoot());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY += dy;
                if (totalScrollY > PixUtils.dp2pix(48)) {
                    mBinding.tagLogo.setVisibility(View.VISIBLE);
                    mBinding.tagTitle.setVisibility(View.VISIBLE);
                    mBinding.topBarFollow.setVisibility(View.VISIBLE);
                    mBinding.actionBack.setImageResource(R.drawable.icon_back_black);
                } else {
                    mBinding.tagLogo.setVisibility(View.GONE);
                    mBinding.tagTitle.setVisibility(View.GONE);
                    mBinding.topBarFollow.setVisibility(View.GONE);
                    mBinding.actionBack.setImageResource(R.drawable.icon_back_white);
                }
            }
        });
    }

    public PagedListAdapter getAdapter() {
        return new FeedAdapter(this, KEY_FEED_TYPE) {
            @Override
            public void onViewAttachedToWindow2(@NonNull ViewHolder holder) {
                if (holder.isVideoItem()) {
                    mPageDetector.addListener(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow2(@NonNull ViewHolder holder) {
                mPageDetector.removeListener(holder.getListPlayerView());
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                boolean isVideo = feed.itemType == Feed.TYPE_VIDEO;
                showPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                //这个方法是在我们每提交一次 pagelist对象到adapter 就会触发一次
                //每调用一次 adpater.submitlist
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (showPause) {
            mPageDetector.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageDetector.onResume();
    }

    @Override
    protected void onDestroy() {
        PageListPlayManager.release(KEY_FEED_TYPE);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_back) {
            finish();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mTagFeedListViewModel.getDataSource().invalidate();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList currentList = getAdapter().getCurrentList();
        finishRefresh(currentList != null && currentList.size() > 0);
    }
}
