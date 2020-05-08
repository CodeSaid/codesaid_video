package com.codesaid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.R;
import com.codesaid.databinding.RefreshViewLayoutBinding;
import com.codesaid.lib_base.EmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created By codesaid
 * On :2020-05-07 16:42
 * Package Name: com.codesaid.ui
 * desc:
 */
public abstract class AbsListFragment<T, M extends AbsViewModel<T>>
        extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private RefreshViewLayoutBinding mBinding;

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private EmptyView mEmptyView;

    private PagedListAdapter<T, RecyclerView.ViewHolder> mAdapter;

    protected M mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = RefreshViewLayoutBinding.inflate(inflater, container, false);
        mBinding.getRoot().setFitsSystemWindows(true);
        mRecyclerView = mBinding.recyclerView;
        mSmartRefreshLayout = mBinding.refreshLayout;
        mEmptyView = mBinding.emptyView;

        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);

        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);

        //默认给列表中的Item 一个 10dp的ItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);

        afterCreateView();

        return mBinding.getRoot();
    }

    protected abstract void afterCreateView();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if (arguments.length > 1) {
            Type argument = arguments[1];
            Class aClass = ((Class) argument).asSubclass(AbsViewModel.class);
            mViewModel = (M) ViewModelProviders.of(this).get(aClass);
            mViewModel.getLiveData().observe(this, new Observer<PagedList<T>>() {
                @Override
                public void onChanged(PagedList<T> list) {
                    mAdapter.submitList(list);
                }
            });

            mViewModel.getBoundaryPageData().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean hasData) {
                    finishRefresh(hasData);
                }
            });
        }
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

    /**
     * 因而 我们在 onCreateView的时候 创建了 PagedListAdapter
     * 所以，如果arguments 有参数需要传递到Adapter 中，那么需要在getAdapter()方法中取出参数。
     *
     * @return
     */
    public abstract PagedListAdapter<T, RecyclerView.ViewHolder> getAdapter();
}
