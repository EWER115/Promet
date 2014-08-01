package com.example.dejan.promet;


import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.support.v4.content.LocalBroadcastManager;


public class Traffic extends FragmentActivity implements NoInternetDialog.NoInternetDialogListener {

    private ScheduledExecutorService dataScheduler;
    private int updateTime = 10; // default update time

    public static ArrayList<Entry> data = null;
    public static ArrayList<Route> routes = null;

    private ViewPager pager;
    private PagerTabStrip pagerTabStrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));

        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setBackgroundColor(Color.parseColor("#03a9f4"));
        pagerTabStrip.setTextColor(Color.WHITE);
        pagerTabStrip.setTabIndicatorColor(Color.parseColor("#81d4fa"));

        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter(Constants.INTENT_ID1));

        if(!internetConnected())
            new NoInternetDialog().show(getFragmentManager(),"noInternetDialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.SETTINGS_REQ_CODE:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                updateTime = Integer.parseInt(sharedPreferences.getString("updateTime",""+updateTime));

                // restart service with new scheduler
                if(!internetConnected())
                    new NoInternetDialog().show(getFragmentManager(),"noInternetDialog");
                else {
                    if(dataScheduler != null)
                        dataScheduler.shutdown();
                    //getData();
                }

                break;
        }

    }

    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    protected void onResume(){
        super.onResume();
        registerReceiver(netReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void onPause(){
        super.onPause();
        unregisterReceiver(netReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.traffic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, DisplaySettings.class);
            startActivityForResult(settingsIntent,Constants.SETTINGS_REQ_CODE);
        }
        if(id == R.id.updateData){
            if(!internetConnected())
                new NoInternetDialog().show(getFragmentManager(),"noInternetDialog");
            else {
                final Intent serviceIntent = new Intent(this, dataPullService.class);
                this.startService(serviceIntent);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /*private void getData()
    {
        final Intent serviceIntent = new Intent(this,dataPullService.class);
        dataScheduler = Executors.newSingleThreadScheduledExecutor();
        dataScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getApplicationContext().startService(serviceIntent);
            }
        },0,updateTime, TimeUnit.MINUTES);
        this.startService(serviceIntent);
    }*/

    // RECEIVE NEW TRAFFIC DATA
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            data =  intent.getParcelableArrayListExtra(Constants.DATA_KEY1);
            routes = intent.getParcelableArrayListExtra(Constants.DATA_KEY2);

            intent.setAction(Constants.INTENT_ID2);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            Intent reportIntent = new Intent()
                            .putExtra(Constants.DATA_KEY3, intent.getParcelableExtra(Constants.DATA_KEY3))
                            .setAction(Constants.INTENT_ID3);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(reportIntent);

        }
    };

    // RECEIVE INTERNET STATUS
    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(internetConnected())
                Toast.makeText(context,"Internet connected",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,"Internet not connected",Toast.LENGTH_SHORT).show();

        }
    };


    // NoInternetDialog response methods

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //finish();
    }

    private boolean internetConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



    private class TabsPagerAdapter extends FragmentStatePagerAdapter {

        // number of tabs
        private static final int PAGE_COUNT = 3;

        public TabsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            switch(i){
                case 0: return new TrafficFragment();
                case 1: return new ViewMapFragment();
                case 2: return new ReportFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return Constants.titles[position];
        }
    }
}


