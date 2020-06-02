package com.codesaid.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.codesaid.databinding.FragmentMyBinding;
import com.codesaid.lib_navannotation.FragmentDestination;

/**
 * Created By codesaid
 * On :2020-05-04 14:59
 * Package Name: com.codesaid.ui.my
 * desc:
 */

@FragmentDestination(pageUrl = "main/tabs/my", asStarter = false)
public class MyFragment extends Fragment {


    private FragmentMyBinding mBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatImageView actionLogout = mBinding.actionLogout;
        AppCompatImageView goDetail = mBinding.goDetail;

    }
}
