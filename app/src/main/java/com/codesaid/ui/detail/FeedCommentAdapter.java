package com.codesaid.ui.detail;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.databinding.LayoutFeedCommentListItemBinding;
import com.codesaid.lib_base.extention.AbsPagedListAdapter;
import com.codesaid.lib_base.util.PixUtils;
import com.codesaid.model.Comment;
import com.codesaid.ui.InteractionPresenter;
import com.codesaid.ui.MutableItemKeyDataSource;
import com.codesaid.ui.login.UserManager;
import com.codesaid.ui.publish.PreviewActivity;

/**
 * Created By codesaid
 * On :2020-05-17 21:23
 * Package Name: com.codesaid.ui.detail
 * desc:
 */
public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {


    private Context mContext;
    private LayoutInflater mInflater;

    protected FeedCommentAdapter(Context context) {
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
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected int getItemViewType2(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        LayoutFeedCommentListItemBinding binding =
                LayoutFeedCommentListItemBinding.inflate(
                        mInflater, parent, false);

        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);
        holder.mBinding.commentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractionPresenter.deleteFeedComment(mContext, item.itemId, item.commentId)
                        .observe((LifecycleOwner) mContext, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean success) {
                                if (success) {
                                    MutableItemKeyDataSource<Integer, Comment> dataSource =
                                            new MutableItemKeyDataSource<Integer, Comment>((ItemKeyedDataSource) getCurrentList().getDataSource()) {
                                                @NonNull
                                                @Override
                                                public Integer getKey(@NonNull Comment item) {
                                                    return item.id;
                                                }

                                            };
                                    PagedList<Comment> currentList = getCurrentList();
                                    for (Comment comment : currentList) {
                                        if (comment != getItem(position)) {
                                            dataSource.data.add(comment);
                                        }
                                    }
                                    PagedList<Comment> pagedList = dataSource.buildNewPagedList(getCurrentList().getConfig());
                                    submitList(pagedList);
                                }
                            }
                        });
            }
        });

        holder.mBinding.commentCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideo = item.commentType == Comment.COMMENT_TYPE_VIDEO;
                PreviewActivity.startActivity((Activity) mContext,
                        isVideo ? item.videoUrl : item.imageUrl, isVideo, null);
            }
        });
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
