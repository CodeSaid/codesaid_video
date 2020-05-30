package com.codesaid.ui.find;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.R;
import com.codesaid.model.TagList;
import com.codesaid.ui.AbsListFragment;
import com.codesaid.ui.MutableItemKeyDataSource;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-30 20:50
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagListFragment extends AbsListFragment<TagList, TagListViewModel> {

    public static final String KEY_TAG_TYPE = "tag_type";
    private String mTagType;

    public static TagListFragment newInstance(String tagType) {
        Bundle args = new Bundle();
        args.putString(KEY_TAG_TYPE, tagType);
        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public PagedListAdapter getAdapter() {
        mTagType = getArguments().getString(KEY_TAG_TYPE);
        mViewModel.setTagType(mTagType);
        TagListAdapter tagListAdapter = new TagListAdapter(getContext());
        return tagListAdapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (TextUtils.equals(mTagType, "onlyFollow")) {
            mEmptyView.setEmptyText(getString(R.string.tag_list_no_follow));
            mEmptyView.setBtn(getString(R.string.tag_list_no_follow_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.getSwitchTabLiveData().setValue(new Object());
                }
            });
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList<TagList> currentList = getAdapter().getCurrentList();
        long tagId = currentList == null ? 0 : currentList.get(currentList.size() - 1).tagId;
        mViewModel.loadData(tagId, new ItemKeyedDataSource.LoadCallback() {
            @Override
            public void onResult(@NonNull List data) {
                MutableItemKeyDataSource<Long, TagList> mutableItemKeyDataSource =
                        new MutableItemKeyDataSource<Long, TagList>((ItemKeyedDataSource) mViewModel.getDataSource()) {
                            @NonNull
                            @Override
                            public Long getKey(@NonNull TagList item) {
                                return item.tagId;
                            }
                        };

                mutableItemKeyDataSource.data.addAll(currentList);
                mutableItemKeyDataSource.data.addAll(data);
                PagedList<TagList> tagLists = mutableItemKeyDataSource.buildNewPagedList(currentList.getConfig());
                if (data.size() > 0) {
                    submitList(tagLists);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }
}
