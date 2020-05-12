package com.codesaid.exoplayer;

import android.view.ViewGroup;

/**
 * Created By codesaid
 * On :2020-05-12 01:07
 * Package Name: com.codesaid.exoplayer
 * desc:
 */
public interface IPlayerListener {

    ViewGroup getOwner();

    void onActive();

    void inActive();

    boolean isPlaying();
}
