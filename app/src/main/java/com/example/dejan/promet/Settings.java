package com.example.dejan.promet;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Dejan on 20.7.2014.
 */
public class Settings extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
