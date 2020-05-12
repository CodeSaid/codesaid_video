package com.codesaid.exoplayer;

import android.app.Application;
import android.view.LayoutInflater;

import com.codesaid.R;
import com.codesaid.lib_base.AppGlobals;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * Created By codesaid
 * On :2020-05-12 00:18
 * Package Name: com.codesaid.exoplayer
 * desc:
 */
public class PageListPlay {

    public SimpleExoPlayer mExoPlayer;
    public PlayerView mPlayerView;
    public PlayerControlView mPlayerControlView;

    public String playUrl;

    public PageListPlay() {
        Application application = AppGlobals.getApplication();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                application,
                new DefaultRenderersFactory(application),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mPlayerView = (PlayerView) LayoutInflater
                .from(application).inflate(R.layout.exo_player_layout, null, false);

        mPlayerControlView = (PlayerControlView) LayoutInflater
                .from(application).inflate(R.layout.exo_player_controller_layout, null, false);

        mPlayerView.setPlayer(mExoPlayer);
        mPlayerControlView.setPlayer(mExoPlayer);
    }

    /**
     * 销毁
     */
    public void release() {

        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.stop(true);
            mExoPlayer.release();
            mExoPlayer = null;
        }

        if (mPlayerView != null) {
            mPlayerView.setPlayer(null);
            mPlayerView = null;
        }

        if (mPlayerControlView != null) {
            mPlayerControlView.setPlayer(null);
            mPlayerControlView.setVisibilityListener(null);
            mPlayerControlView = null;
        }
    }
}
