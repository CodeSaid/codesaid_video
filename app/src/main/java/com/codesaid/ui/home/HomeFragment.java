package com.codesaid.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.codesaid.lib_navannotation.FragmentDestination;
import com.codesaid.model.Feed;
import com.codesaid.ui.AbsListFragment;
import com.codesaid.ui.MutableDataSource;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {

    @Override
    protected void afterCreateView() {
        mViewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                mAdapter.submitList(feeds);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(), feedType);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 获取最后一个 item
        Feed feed = mAdapter.getCurrentList().get(mAdapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {

            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = mAdapter.getCurrentList().getConfig();

                if (data != null && data.size() > 0) {
                    MutableDataSource dataSource = new MutableDataSource();
                    dataSource.mList.add(data);
                    PagedList pagedList = dataSource.buildNewPageList(config);
                    submitList(pagedList);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }
}