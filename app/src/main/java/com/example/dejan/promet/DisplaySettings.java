package com.example.dejan.promet;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Dejan on 20.7.2014.
 */
public class DisplaySettings extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();
    }
}
