package com.workingagenda.democracydroid.tabfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workingagenda.democracydroid.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class TabFragment1 extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }
}