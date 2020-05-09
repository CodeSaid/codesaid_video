package com.codesaid.ui.home;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.codesaid.lib_navannotation.FragmentDestination;
import com.codesaid.model.Feed;
import com.codesaid.ui.AbsListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {
    @Override
    protected void afterCreateView() {

    }

    @Override
    public PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(), feedType);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }
}