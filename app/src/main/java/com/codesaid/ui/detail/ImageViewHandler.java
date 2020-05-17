package com.codesaid.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.R;
import com.codesaid.databinding.ActivityFeedDetailTypeImageBinding;
import com.codesaid.databinding.LayoutFeedDetailTypeImageHeaderBinding;
import com.codesaid.model.Feed;
import com.codesaid.view.PPImageView;

/**
 * Created By codesaid
 * On :2020-05-17 21:18
 * Package Name: com.codesaid.ui.detail
 * desc: 图文详情页
 */
public class ImageViewHandler extends ViewHandler {

    protected ActivityFeedDetailTypeImageBinding mImageBinding;
    protected LayoutFeedDetailTypeImageHeaderBinding mHeaderBinding;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);

        mImageBinding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_detail_type_image);
        mImageBinding.setFeed(mFeed);
        mRecyclerView = mImageBinding.recyclerView;
        mInateractionBinding = mImageBinding.interactionLayout;
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding
                .inflate(LayoutInflater.from(mActivity), mRecyclerView, false);
        mHeaderBinding.setFeed(mFeed);

        PPImageView headerImage = mHeaderBinding.headerImage;
        headerImage.bindData(mFeed.width, mFeed.height, mFeed.width > mFeed.height ? 0 : 16, mFeed.cover);
        mAdapter.addHeaderView(mHeaderBinding.getRoot());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean visible = mHeaderBinding.getRoot().getTop() <= -mImageBinding.titleLayout.getMeasuredHeight();
                mImageBinding.authorInfoLayout.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);
                mImageBinding.title.setVisibility(visible ? View.GONE : View.VISIBLE);
            }
        });
    }
}
