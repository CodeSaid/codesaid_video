package com.codesaid.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codesaid.model.Feed;

/**
 * Created By codesaid
 * On :2020-05-16 01:09
 * Package Name: com.codesaid.ui
 * desc:
 */
public class FeedDetailActivity extends AppCompatActivity {

    private static final String KEY_FEED = "key_feed";
    private static final String KEY_CATEGORY = "key_category";

    public static void startActivity(Context context, Feed item, String category) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(KEY_FEED, item);
        intent.putExtra(KEY_CATEGORY, category);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Feed feed = getIntent().getParcelableExtra(KEY_FEED);
        if (feed == null) {
            finish();
            return;
        }

        ViewHandler viewHandler = null;
        if (feed.itemType == Feed.TYPE_IMAGE) { // 图文
            viewHandler = new ImageViewHandler(this);
        } else if (feed.itemType == Feed.TYPE_VIDEO) { // 视频
            viewHandler = new VideoViewHandler(this);
        }

        viewHandler.bindInitData(feed);
    }
}
