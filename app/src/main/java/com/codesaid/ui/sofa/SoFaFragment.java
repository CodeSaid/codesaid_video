package com.codesaid.ui.sofa;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.codesaid.R;
import com.codesaid.databinding.FragmentSofaBinding;
import com.codesaid.lib_navannotation.FragmentDestination;
import com.codesaid.model.SofaTab;
import com.codesaid.ui.home.HomeFragment;
import com.codesaid.utils.AppConfig;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/sofa", asStarter = false)
public class SoFaFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private FragmentSofaBinding mBinding;

    private List<SofaTab.Tabs> mTabs = new ArrayList<>();
    private SofaTab mTabConfig;

    private HashMap<Integer, Fragment> mFragmentHashMap = new HashMap<>();
    private TabLayoutMediator mMediator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentSofaBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTabLayout = mBinding.tabLayout;
        mViewPager = mBinding.viewPager;

        mTabConfig = getTabConfig();
        for (SofaTab.Tabs tab : mTabConfig.tabs) {
            if (tab.enable)
                mTabs.add(tab);
        }

        // 禁止 预加载
        mViewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        mViewPager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), this.getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = mFragmentHashMap.get(position);
                if (fragment == null) {
                    fragment = getTabFragment(position);
                    mFragmentHashMap.put(position, fragment);
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return mTabs.size();
            }
        });

        mMediator = new TabLayoutMediator(mTabLayout, mViewPager,
                false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(createTabView(position));
            }
        });

        mMediator.attach();

        mViewPager.registerOnPageChangeCallback(mPageChangeCallback);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(mTabConfig.select);
            }
        });
    }

    ViewPager2.OnPageChangeCallback mPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            int tabCount = mTabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);

                TextView customView = (TextView) tab.getCustomView();

                if (tab.getPosition() == position) {
                    customView.setTextSize(mTabConfig.activeSize);
                    customView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    customView.setTextSize(mTabConfig.normalSize);
                    customView.setTypeface(Typeface.DEFAULT);
                }
            }
        }

    };

    private View createTabView(int position) {
        TextView textView = new TextView(getContext());

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(mTabConfig.activeColor), Color.parseColor(mTabConfig.normalColor)};
        ColorStateList stateList = new ColorStateList(states, colors);
        textView.setTextColor(stateList);
        textView.setText(mTabs.get(position).title);
        textView.setTextSize(mTabConfig.normalSize);
        return textView;
    }

    private Fragment getTabFragment(int position) {
        return HomeFragment.newInstance(mTabs.get(position).tag);
    }

    private SofaTab getTabConfig() {
        return AppConfig.getSofaTab();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediator.detach();
        mViewPager.unregisterOnPageChangeCallback(mPageChangeCallback);
    }
}