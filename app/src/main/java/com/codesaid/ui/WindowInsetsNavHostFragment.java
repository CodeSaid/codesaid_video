package com.codesaid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.codesaid.view.WindowInsetsFrameLayout;

/**
 * Created By codesaid
 * On :2020-05-13 01:22
 * Package Name: com.codesaid.ui
 * desc:
 */
public class WindowInsetsNavHostFragment extends NavHostFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        WindowInsetsFrameLayout layout = new WindowInsetsFrameLayout(inflater.getContext());
        layout.setId(getId());
        return layout;
    }
}
