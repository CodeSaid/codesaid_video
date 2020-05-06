package com.codesaid.ui.sofa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SoFaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SoFaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is sofa fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}