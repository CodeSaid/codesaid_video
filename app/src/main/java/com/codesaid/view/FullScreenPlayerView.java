package com.codesaid.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codesaid.R;
import com.codesaid.exoplayer.PageListPlay;
import com.codesaid.exoplayer.PageListPlayManager;
import com.codesaid.lib_base.util.PixUtils;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;


/**
 * Created By codesaid
 * On :2020-05-25 16:16
 * Package Name: com.codesaid.view
 * desc:
 */
public class FullScreenPlayerView extends ListPlayerView {

    private PlayerView mExoPlayerView;

    public FullScreenPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public FullScreenPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FullScreenPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mExoPlayerView = (PlayerView) LayoutInflater.from(context)
                .inflate(R.layout.exo_player_layout, null, false);
    }

    @Override
    protected void setSize(int widthPx, int heightPx) {
        if (widthPx >= heightPx) {
            super.setSize(widthPx, heightPx);
        } else {
            int maxWidth = PixUtils.getScreenWidth();
            int maxHeight = PixUtils.getScreenHeight();

            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = maxWidth;
            params.height = maxHeight;
            setLayoutParams(params);

            FrameLayout.LayoutParams coverParams = (LayoutParams) cover.getLayoutParams();
            coverParams.width = (int) (widthPx / (heightPx * 1.0f / maxHeight));
            coverParams.height = maxHeight;
            coverParams.gravity = Gravity.CENTER;
            cover.setLayoutParams(coverParams);
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (mHeightPx > mWidthPx) {
            int layoutHeight = params.height;
            int layoutWidth = params.width;
            ViewGroup.LayoutParams coverLayoutParams = cover.getLayoutParams();
            coverLayoutParams.width = (int) (mWidthPx / (mHeightPx * 1.0f / layoutHeight));
            coverLayoutParams.height = layoutHeight;

            cover.setLayoutParams(coverLayoutParams);

            if (mExoPlayerView != null) {
                ViewGroup.LayoutParams exoLayoutParams = mExoPlayerView.getLayoutParams();

                if (exoLayoutParams != null && exoLayoutParams.width > 0 && exoLayoutParams.height > 0) {
                    float scaleX = coverLayoutParams.width * 1.0f / exoLayoutParams.width;
                    float scaleY = coverLayoutParams.height * 1.0f / exoLayoutParams.height;

                    mExoPlayerView.setScaleX(scaleX);
                    mExoPlayerView.setScaleY(scaleY);
                }

            }
        }
        super.setLayoutParams(params);
    }

    @Override
    public void onActive() {
        //视频播放,或恢复播放

        //通过该View所在页面的mCategory(比如首页列表tab_all,沙发tab的tab_video,标签帖子聚合的tag_feed) 字段，
        //取出管理该页面的Exoplayer播放器，ExoplayerView播放View,控制器对象PageListPlay
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        PlayerView playerView = mExoPlayerView;
        PlayerControlView controlView = pageListPlay.mPlayerControlView;
        SimpleExoPlayer exoPlayer = pageListPlay.mExoPlayer;
        if (playerView == null) {
            return;
        }
        pageListPlay.switchPlayerView(playerView);
        //此处我们需要主动调用一次 switchPlayerView，把播放器Exoplayer和展示视频画面的View ExoplayerView相关联
        //为什么呢？因为在列表页点击视频Item跳转到视频详情页的时候，详情页会复用列表页的播放器Exoplayer，然后和新创建的展示视频画面的View ExoplayerView相关联，达到视频无缝续播的效果
        //如果 我们再次返回列表页，则需要再次把播放器和ExoplayerView相关联
        ViewParent parent = playerView.getParent();
        if (parent != this) {

            //把展示视频画面的View添加到ItemView的容器上
            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView);
                //还应该暂停掉列表上正在播放的那个
                ((ListPlayerView) parent).inActive();
            }

            ViewGroup.LayoutParams coverParams = cover.getLayoutParams();
            this.addView(playerView, 1, coverParams);
        }

        ViewParent ctrlParent = controlView.getParent();
        if (ctrlParent != this) {
            //把视频控制器 添加到ItemView的容器上
            if (ctrlParent != null) {
                ((ViewGroup) ctrlParent).removeView(controlView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            this.addView(controlView, params);
        }

        //如果是同一个视频资源,则不需要从重新创建mediaSource。
        //但需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource mediaSource = PageListPlayManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlay.playUrl = mVideoUrl;
        }
        controlView.show();
        controlView.setVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void inActive() {
        super.inActive();
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        pageListPlay.switchPlayerView(null);
    }
}
