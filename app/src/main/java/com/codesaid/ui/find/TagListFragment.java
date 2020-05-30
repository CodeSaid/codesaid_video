package com.codesaid.ui.find;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.model.TagList;
import com.codesaid.ui.AbsListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * Created By codesaid
 * On :2020-05-30 20:50
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagListFragment extends AbsListFragment<TagList, TagListViewModel> {

    public static final String KEY_TAG_TYPE = "tag_type";

    public static TagListFragment newInstance(String tagType) {
        Bundle args = new Bundle();
        args.putString(KEY_TAG_TYPE, tagType);
        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public PagedListAdapter<TagList, RecyclerView.ViewHolder> getAdapter() {
        return null;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}
