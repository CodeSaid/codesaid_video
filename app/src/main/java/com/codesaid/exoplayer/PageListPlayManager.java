package com.codesaid.exoplayer;

import android.net.Uri;

import com.codesaid.lib_base.AppGlobals;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;

/**
 * Created By codesaid
 * On :2020-05-12 00:17
 * Package Name: com.codesaid.exoplayer
 * desc:
 */
public class PageListPlayManager {

    private static HashMap<String, PageListPlay> sPageListPlayHashMap = new HashMap<>();
    private static ProgressiveMediaSource.Factory sMediaSourceFactory;

    static {

        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(AppGlobals.getApplication(), AppGlobals.getApplication().getPackageName()));
        Cache cache = new SimpleCache(AppGlobals.getApplication().getCacheDir(),
                new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200));
        CacheDataSinkFactory cacheDataSinkFactory = new CacheDataSinkFactory(cache, Long.MAX_VALUE);
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(cache,
                dataSourceFactory,
                new FileDataSourceFactory(),
                cacheDataSinkFactory,
                CacheDataSource.FLAG_BLOCK_ON_CACHE,
                null);

        sMediaSourceFactory = new ProgressiveMediaSource.Factory(cacheDataSourceFactory);
    }

    public static MediaSource createMediaSource(String url) {
        return sMediaSourceFactory.createMediaSource(Uri.parse(url));
    }


    public static PageListPlay get(String pageName) {
        PageListPlay pageListPlay = sPageListPlayHashMap.get(pageName);
        if (pageListPlay == null) {
            pageListPlay = new PageListPlay();
            sPageListPlayHashMap.put(pageName, pageListPlay);
        }
        return pageListPlay;
    }

    public static void release(String pageName) {
        PageListPlay pageListPlay = sPageListPlayHashMap.get(pageName);
        if (pageListPlay != null) {
            pageListPlay.release();
        }
    }
}
