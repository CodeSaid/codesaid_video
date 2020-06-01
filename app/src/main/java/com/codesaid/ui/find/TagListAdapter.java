package com.codesaid.ui.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.databinding.TagListItemLayoutBinding;
import com.codesaid.lib_base.extention.AbsPagedListAdapter;
import com.codesaid.model.TagList;
import com.codesaid.ui.InteractionPresenter;

/**
 * Created By codesaid
 * On :2020-05-30 21:20
 * Package Name: com.codesaid.ui.find
 * desc:
 */
public class TagListAdapter extends AbsPagedListAdapter<TagList, TagListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    protected TagListAdapter(Context context) {
        super(new DiffUtil.ItemCallback<TagList>() {
            @Override
            public boolean areItemsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.tagId == newItem.tagId;
            }

            @Override
            public boolean areContentsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected int getItemViewType2(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        TagListItemLayoutBinding binding = TagListItemLayoutBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        holder.bindData(getItem(position));
        holder.mBinding.actionFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractionPresenter.toggleTagFollow((LifecycleOwner) mContext, getItem(position));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagFeedListActivity.startActivity(mContext, getItem(position));
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TagListItemLayoutBinding mBinding;

        public ViewHolder(@NonNull View itemView, TagListItemLayoutBinding binding) {
            super(itemView);
            mBinding = binding;


        }

        public void bindData(TagList item) {
            mBinding.setTagList(item);
        }
    }
}
