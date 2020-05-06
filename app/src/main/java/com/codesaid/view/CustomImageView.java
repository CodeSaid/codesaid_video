package com.codesaid.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

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
}
