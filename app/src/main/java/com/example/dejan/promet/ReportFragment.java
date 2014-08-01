package com.example.dejan.promet;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Dejan on 24.7.2014.
 */
public class ReportFragment extends Fragment {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(Constants.INTENT_ID3));
    }

    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.report_fragment, container, false);


        return view;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TrafficReport trafficReport = intent.getParcelableExtra(Constants.DATA_KEY3);
            TextView report = (TextView) getActivity().findViewById(R.id.report);
            TextView timestamp = (TextView) getActivity().findViewById(R.id.timestamp);

            report.setText(Html.fromHtml(trafficReport.report));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = simpleDateFormat.parse(trafficReport.time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.setTimeZone(TimeZone.getTimeZone("Europe/Ljubljana"));
                //calendar.add(Calendar.HOUR,2);

                timestamp.setText(Html.fromHtml("<b>Posodobljeno:</b> "+SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL, new Locale("sl_SI")).format(date)+", <b>"+String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d",calendar.get(Calendar.MINUTE))+"</b>"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

}
