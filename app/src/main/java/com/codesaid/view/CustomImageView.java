package com.codesaid.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codesaid.lib_base.PixUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created By codesaid
 * On :2020-05-06 16:03
 * Package Name: com.codesaid.view
 * desc:
 */
public class CustomImageView extends AppCompatImageView {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param view     view
     * @param imageUrl 图片 url
     * @param isCircle 是否圆形
     */
    @BindingAdapter(value = {"image_url", "isCircle"})
    public static void setImageUrl(CustomImageView view, String imageUrl, boolean isCircle) {
        RequestBuilder<Drawable> builder = Glide.with(view).load(imageUrl);
        if (isCircle) {
            builder.transform(new CircleCrop());
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null && params.width > 0 && params.height > 0) {
            builder.override(params.width, params.height);
        }

        builder.into(view);
    }

    public void bindData(String imageUrl, int widthPix,
                         int heightPix, int marginLeft) {
        bindData(imageUrl, widthPix, heightPix, marginLeft,
                PixUtils.getScreenWidht(), PixUtils.getScreenHeight());
    }

    /**
     * @param imageUrl   图片 url
     * @param widthPix   宽度
     * @param heightPix  高度
     * @param marginLeft 左边距
     * @param maxWidth   最大宽度
     * @param maxHeight  最大高度
     */
    public void bindData(String imageUrl, int widthPix,
                         int heightPix, int marginLeft,
                         int maxWidth, int maxHeight) {
        if (widthPix <= 0 || heightPix <= 0) {
            Glide.with(this).load(imageUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    int height = resource.getIntrinsicHeight();
                    int width = resource.getIntrinsicWidth();
                    setSize(width, height, marginLeft, maxWidth, maxHeight);

                    setImageDrawable(resource);
                }
            });
            return;
        }

        setSize(widthPix, heightPix, marginLeft, maxWidth, maxHeight);
        setImageUrl(this, imageUrl, false);
    }

    private void setSize(int width, int height, int marginLeft, int maxWidth, int maxHeight) {
        int finalWidth, finalHeight;
        if (width > height) {
            finalWidth = maxWidth;
            finalHeight = (int) (height / (width * 1.0f / finalWidth));
        } else {
            finalHeight = maxHeight;
            finalWidth = (int) (width / (height * 1.0f / finalHeight));
        }

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(finalWidth, finalHeight);
        params.leftMargin = height > width ? PixUtils.dp2pix(marginLeft) : 0;
        setLayoutParams(params);
    }

    public void setBlurImageUrl(String coverUrl, int radius) {
        Glide.with(this).load(coverUrl).override(50)
                .transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        setBackground(resource);
                    }
                });
    }
}
