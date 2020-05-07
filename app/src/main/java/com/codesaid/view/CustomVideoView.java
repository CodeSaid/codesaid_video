package com.codesaid.view;

import android.content.ContentProvider;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codesaid.R;
import com.codesaid.lib_base.PixUtils;
import com.codesaid.utils.StringConvert;

/**
 * Created By codesaid
 * On :2020-05-07 15:29
 * Package Name: com.codesaid.view
 * desc:
 */
public class CustomVideoView extends FrameLayout {

    private ProgressBar mBufferView;
    private CustomImageView mCover;
    private CustomImageView mBlurBackground;
    private ImageView mPlayBtn;
    private String mVideoUrl;
    private String mCategory;


    public CustomVideoView(@NonNull Context context) {
        this(context, null);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.video_view_layout, this, true);

        mBufferView = findViewById(R.id.buffer_view);
        mCover = findViewById(R.id.cover);
        mBlurBackground = findViewById(R.id.blur_background);
        mPlayBtn = findViewById(R.id.play_btn);
    }

    /**
     * @param videoUrl  视频 url
     * @param coverUrl  视频封面 url
     * @param widthPix  宽度
     * @param heightPix 高度
     */
    public void bindData(String videoUrl, String coverUrl,
                         int widthPix, int heightPix,
                         String category) {

        mVideoUrl = videoUrl;
        mCategory = category;

        CustomImageView.setImageUrl(mCover, coverUrl, false);

        if (widthPix < heightPix) {
            mBlurBackground.setBlurImageUrl(coverUrl, 10);
            mBlurBackground.setVisibility(VISIBLE);
        } else {
            mBlurBackground.setVisibility(INVISIBLE);
        }
        setSize(widthPix, heightPix);
    }

    private void setSize(int widthPix, int heightPix) {
        int maxWidth = PixUtils.getScreenWidht();
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth;
        int coverHeight;

        if (widthPix > heightPix) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPix / (widthPix * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPix / (heightPix * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);

        ViewGroup.LayoutParams blurParams = mBlurBackground.getLayoutParams();
        blurParams.height = layoutHeight;
        blurParams.width = layoutWidth;
        mBlurBackground.setLayoutParams(blurParams);

        FrameLayout.LayoutParams coverParams = (LayoutParams) mCover.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        mCover.setLayoutParams(coverParams);

        FrameLayout.LayoutParams playBtnParams = (LayoutParams) mPlayBtn.getLayoutParams();
        playBtnParams.gravity = Gravity.CENTER;
        mPlayBtn.setLayoutParams(playBtnParams);

    }
}
