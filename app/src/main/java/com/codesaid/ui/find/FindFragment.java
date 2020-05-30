package com.codesaid.ui.find;

import androidx.fragment.app.Fragment;

import com.codesaid.lib_navannotation.FragmentDestination;
import com.codesaid.model.SofaTab;
import com.codesaid.ui.sofa.SoFaFragment;
import com.codesaid.utils.AppConfig;

@FragmentDestination(pageUrl = "main/tabs/find", asStarter = false)
public class FindFragment extends SoFaFragment {

    @Override
    public Fragment getTabFragment(int position) {
        return TagListFragment.newInstance(getTabConfig().tabs.get(position).tag);
    }

    @Override
    public SofaTab getTabConfig() {
        return AppConfig.getFindTab();
    }
}