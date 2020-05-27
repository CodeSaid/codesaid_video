package com.codesaid.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.codesaid.R;
import com.codesaid.databinding.LayoutFeedDetailTypeVideoBinding;
import com.codesaid.databinding.LayoutFeedDetailTypeVideoHeaderBinding;
import com.codesaid.model.Feed;

/**
 * Created By codesaid
 * On :2020-05-17 21:18
 * Package Name: com.codesaid.ui.detail
 * desc: 视频详情页
 */
public class VideoViewHandler extends ViewHandler {

    private final LayoutFeedDetailTypeVideoBinding mBinding;

    // 是否点击的是手机的返回键
    private boolean backPressed;

    public VideoViewHandler(FragmentActivity activity) {
        super(activity);

        mBinding = DataBindingUtil.setContentView(activity, R.layout.layout_feed_detail_type_video);

        mInateractionBinding = mBinding.bottomInateraction;
        mRecyclerView = mBinding.recyclerView;

        View authorInfoView = mBinding.authorInfo.getRoot();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) authorInfoView.getLayoutParams();
        layoutParams.setBehavior(new ViewAnchorBehavior(R.id.player_view));

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mBinding.playerView.getLayoutParams();
        ViewZoomBehavior behavior = (ViewZoomBehavior) params.getBehavior();
        if (behavior != null)
            behavior.setViewZoomCallback(new ViewZoomBehavior.ViewZoomCallback() {
                @Override
                public void onDragZoom(int height) {
                    int bottom = mBinding.playerView.getBottom();
                    boolean moveUp = height < bottom;
                    boolean fullscreen = moveUp
                            ? height >= mBinding.coordinator.getBottom() - mInateractionBinding.getRoot().getHeight()
                            : height >= mBinding.coordinator.getBottom();
                    setViewAppearance(fullscreen);
                }
            });
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mBinding.setFeed(feed);

        String category = mActivity.getIntent().getStringExtra(FeedDetailActivity.KEY_CATEGORY);
        mBinding.playerView.bindData(category, mFeed.width, mFeed.height, mFeed.cover, mFeed.url);

        mBinding.playerView.post(new Runnable() {
            @Override
            public void run() {
                boolean fullScreen = mBinding.playerView.getBottom() >= mBinding.coordinator.getBottom();
                setViewAppearance(fullScreen);
            }
        });

        LayoutFeedDetailTypeVideoHeaderBinding headerBinding =
                LayoutFeedDetailTypeVideoHeaderBinding
                        .inflate(LayoutInflater.from(mActivity), mRecyclerView, false);
        headerBinding.setFeed(mFeed);
        mAdapter.addHeaderView(headerBinding.getRoot());
    }

    private void setViewAppearance(boolean fullScreen) {
        mBinding.setFullscreen(fullScreen);
        mBinding.fullscreenAuthorInfo.getRoot().setVisibility(fullScreen ? View.VISIBLE : View.GONE);

        // 底部 bottom 高度
        int bottomHeight = mInateractionBinding.getRoot().getMeasuredHeight();
        // 播放 控制器 高度
        int controllerHeight = mBinding.playerView.getPlayController().getMeasuredHeight();
        int controllerBottom = mBinding.playerView.getPlayController().getBottom();
        mBinding.playerView.getPlayController().setY(fullScreen ? controllerBottom - bottomHeight - controllerHeight
                : controllerBottom - controllerHeight);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!backPressed) {
            mBinding.playerView.inActive();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressed = false;
        mBinding.playerView.onActive();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
        //按了返回键后需要 恢复 播放控制器的位置。否则回到列表页时 可能会不正确的显示
        mBinding.playerView.getPlayController().setTranslationY(0);
    }
}
