package com.codesaid.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.codesaid.R;
import com.codesaid.databinding.FragmentMyBinding;
import com.codesaid.lib_base.util.StatusBar;
import com.codesaid.lib_navannotation.FragmentDestination;
import com.codesaid.model.User;
import com.codesaid.ui.login.UserManager;

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

        User user = UserManager.getInstance().getUser();
        mBinding.setUser(user);

        UserManager.getInstance().refresh().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mBinding.setUser(user);
                }
            }
        });

        mBinding.actionLogout.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.fragment_my_logout))
                .setPositiveButton(getString(R.string.fragment_my_logout_ok), (dialog, which) -> {
                    dialog.dismiss();
                    UserManager.getInstance().logout();
                    getActivity().onBackPressed();
                }).setNegativeButton(getString(R.string.fragment_my_logout_cancel), null)
                .create().show());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.lightStatusBar(getActivity(), false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        StatusBar.lightStatusBar(getActivity(), hidden);
    }
}
