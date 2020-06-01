package com.codesaid.ui.find;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.codesaid.lib_navannotation.FragmentDestination;
import com.codesaid.model.SofaTab;
import com.codesaid.ui.sofa.SoFaFragment;
import com.codesaid.utils.AppConfig;

@FragmentDestination(pageUrl = "main/tabs/find", asStarter = false)
public class FindFragment extends SoFaFragment {

    @Override
    public Fragment getTabFragment(int position) {
        SofaTab.Tabs tab = getTabConfig().tabs.get(position);
        TagListFragment fragment = TagListFragment.newInstance(tab.tag);
        return fragment;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        String tagType = childFragment.getArguments().getString(TagListFragment.KEY_TAG_TYPE);
        if (tagType.equals("onlyFollow")) {
            ViewModelProviders
                    .of(childFragment)
                    .get(TagListViewModel.class)
                    .getSwitchTabLiveData()
                    .observe(this, new Observer() {
                        @Override
                        public void onChanged(Object o) {
                            mViewPager.setCurrentItem(1);
                        }
                    });
        }
    }

    @Override
    public SofaTab getTabConfig() {
        return AppConfig.getFindTab();
    }
}