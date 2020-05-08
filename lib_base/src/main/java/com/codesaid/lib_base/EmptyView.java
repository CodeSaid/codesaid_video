package com.codesaid.lib_base;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

/**
 * Created By codesaid
 * On :2020-05-07 16:20
 * Package Name: com.codesaid.lib_base
 * desc:
 */
public class EmptyView extends LinearLayout {


    private ImageView mEmptyView;
    private TextView mEmptyText;
    private MaterialButton mBtnEmpty;

    public EmptyView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.empty_view_layout, this, true);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        mEmptyView = findViewById(R.id.iv_empty_view);
        mEmptyText = findViewById(R.id.empty_text);
        mBtnEmpty = findViewById(R.id.btn_empty);
    }

    public void setEmptyView(@DrawableRes int iconRes) {
        mEmptyView.setImageResource(iconRes);
    }


    public void setEmptyText(@NonNull String text) {
        if (TextUtils.isEmpty(text)) {
            mEmptyText.setVisibility(GONE);
        } else {
            mEmptyText.setVisibility(VISIBLE);
            mEmptyText.setText(text);
        }
    }

    public void setBtn(String text, View.OnClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            mBtnEmpty.setVisibility(GONE);
        } else {
            mBtnEmpty.setVisibility(VISIBLE);
            mBtnEmpty.setText(text);
            mBtnEmpty.setOnClickListener(listener);
        }
    }


}
