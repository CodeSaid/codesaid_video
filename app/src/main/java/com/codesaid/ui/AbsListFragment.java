package com.codesaid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.databinding.RefreshViewLayoutBinding;
import com.codesaid.lib_base.EmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created By codesaid
 * On :2020-05-07 16:42
 * Package Name: com.codesaid.ui
 * desc:
 */
public abstract class AbsListFragment<T> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private RefreshViewLayoutBinding mBinding;

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private EmptyView mEmptyView;

    private PagedListAdapter<T, RecyclerView.ViewHolder> mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = RefreshViewLayoutBinding.inflate(inflater, container, false);

        mRecyclerView = mBinding.recyclerView;
        mSmartRefreshLayout = mBinding.refreshLayout;
        mEmptyView = mBinding.emptyView;

        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);

        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false));

        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);

        return mBinding.getRoot();
    }

    public void submitList(PagedList<T> list) {
        if (list.size() > 0) {
            mAdapter.submitList(list);
        }
        finishRefresh(list.size() > 0);
    }

    public void finishRefresh(boolean hasData) {
        PagedList<T> currentList = mAdapter.getCurrentList();
        hasData = hasData || currentList != null && currentList.size() > 0;
        RefreshState state = mSmartRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            mSmartRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            mSmartRefreshLayout.finishRefresh();
        }

        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public abstract PagedListAdapter<T, RecyclerView.ViewHolder> getAdapter();
}
