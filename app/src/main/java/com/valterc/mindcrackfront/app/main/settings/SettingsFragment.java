package com.valterc.mindcrackfront.app.main.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.valterc.mindcrackfront.app.R;

/**
 * Created by Valter on 23/12/2014.
 */
public class SettingsFragment extends Fragment {

    private ListView listViewSettings;

    public SettingsFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
