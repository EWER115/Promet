package com.example.dejan.promet;

import android.util.SparseArray;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Dejan on 19.7.2014.
 */
public class Constants {
         public static final String INTENT_ID1 = "com.example.dejan.traffic.INTENT_ID1";
         public static final String INTENT_ID2 = "com.example.dejan.traffic.INTENT_ID2";
         public static final String INTENT_ID3 = "com.example.dejan.traffic.INTENT_ID3";
         public static final String DATA_KEY1 = "com.example.dejan.traffic.DATA_KEY1";
         public static final String DATA_KEY2 = "com.example.dejan.traffic.DATA_KEY2";
         public static final String DATA_KEY3 = "com.example.dejan.traffic.DATA_KEY3";
         public static final String DATA_URL1 = "http://kazipot1.promet.si/kazipot/services/DataExport/exportDogodki.ashx?format=GEORSS&version=1.0.0&reportType=SHORT&language=SI&sortOrder=VELJAVNOSTODDESC&icons=NONE";
         public static final String DATA_URL2 = "http://kazipot1.promet.si/kazipot/services/dataexport/exportPrometnaPorocila.ashx?format=XML&version=1.0.0&language=SI&contentType=HTML";
         public static final int SETTINGS_REQ_CODE = 0;
         public static final Dictionary<String,Integer> SIGN_IMG;
         static{
             SIGN_IMG = new Hashtable<String, Integer>();
             SIGN_IMG.put("delo", R.drawable.delo);
             SIGN_IMG.put("dogodek", R.drawable.dogodek);
             SIGN_IMG.put("izlocanje", R.drawable.izlocanje);
             SIGN_IMG.put("nesreca", R.drawable.nesreca);
             SIGN_IMG.put("poledica", R.drawable.poledica);
             SIGN_IMG.put("sneg", R.drawable.sneg);
             SIGN_IMG.put("veter", R.drawable.veter);
             SIGN_IMG.put("zaprta", R.drawable.zaprta);
             SIGN_IMG.put("zastoj", R.drawable.zastoj);
         }

        public static final String titles[] = {"Vnos potovanja", "Zemljevid", "Prometno poročilo","DARS Twitter"};

    public static SparseArray<Integer> zoomDist = new SparseArray<Integer>() {

        {
            put(1, 200000);
            put(2, 150000);
            put(3, 150000);
            put(4, 130000);
            put(5, 100000);
            put(6, 20000);
            put(7, 15000);
            put(8, 10000);
            put(9, 5000);
            put(10, 3500);
            put(11, 2000);
            put(12, 1500);
            put(13, 1000);
            put(14, 800);
            put(15, 600);
            put(16, 400);
            put(17, 200);
            put(18, 150);
            put(19, 100);
            put(20, 50);
            put(21, 10);                
        }

        ;
    };

}
