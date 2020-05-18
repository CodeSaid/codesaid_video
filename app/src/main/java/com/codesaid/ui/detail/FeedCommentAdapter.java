package com.codesaid.ui.detail;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.databinding.LayoutFeedCommentListItemBinding;
import com.codesaid.lib_base.extention.AbsPagedListAdapter;
import com.codesaid.lib_base.util.PixUtils;
import com.codesaid.model.Comment;
import com.codesaid.ui.login.UserManager;

/**
 * Created By codesaid
 * On :2020-05-17 21:23
 * Package Name: com.codesaid.ui.detail
 * desc:
 */
public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {


    protected FeedCommentAdapter() {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @Override
    protected int getItemViewType2(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        LayoutFeedCommentListItemBinding binding =
                LayoutFeedCommentListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LayoutFeedCommentListItemBinding mBinding;

        public ViewHolder(View itemView, LayoutFeedCommentListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Comment item) {
            mBinding.setComment(item);
            mBinding.labelAuthor.setVisibility(UserManager.getInstance().getUserId()
                    == item.author.userId ? View.VISIBLE : View.GONE);
            mBinding.commentDelete.setVisibility(UserManager.getInstance().getUserId()
                    == item.author.userId ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(item.imageUrl)) {
                mBinding.commentCover.setVisibility(View.VISIBLE);
                mBinding.commentCover.bindData(item.width,
                        item.height,
                        0,
                        PixUtils.dp2pix(200),
                        PixUtils.dp2pix(200),
                        item.imageUrl);
                if (!TextUtils.isEmpty(item.videoUrl)) {
                    mBinding.videoIcon.setVisibility(View.VISIBLE);
                } else {
                    mBinding.videoIcon.setVisibility(View.GONE);
                }
            } else {
                mBinding.commentCover.setVisibility(View.GONE);
                mBinding.videoIcon.setVisibility(View.GONE);
            }
        }
    }
}
