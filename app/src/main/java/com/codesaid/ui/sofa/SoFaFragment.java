package com.codesaid.ui.sofa;

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

import com.codesaid.R;
import com.codesaid.lib_navannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/sofa",asStarter = false)
public class SoFaFragment extends Fragment {

    private SoFaViewModel mSoFaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.e("SoFaFragment", "onCreateView()");
        mSoFaViewModel =
                ViewModelProviders.of(this).get(SoFaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sofa, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        mSoFaViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}