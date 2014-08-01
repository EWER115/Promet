package com.example.dejan.promet;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dejan on 18.7.2014.
 */
public class dataPullService extends IntentService {

    private static final String LOG_TAG = "TrafficApp";
    private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/directions";
    private static final String OUT_JSON = "/json?";

    public dataPullService() {
        super("trafficDataPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        // traffic EVENTS

        ArrayList<Entry> data = new ArrayList<Entry>();

        try {
            URL url = new URL(Constants.DATA_URL1);
            URLConnection conn = url.openConnection();
            InputStream stream = conn.getInputStream();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(stream,null);

            // start parsing data
            int event = parser.getEventType();
            String location = null, road = null, cause = null, description = null,text = null;

            while(event != XmlPullParser.END_DOCUMENT)
            {
                String name = parser.getName();
                switch(event){
                    case XmlPullParser.START_TAG: break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equals("georss:point"))
                            location = text;
                        else if(name.equals("title")) {
                            road = text;
                            cause = text;
                        }
                        else if(name.equals("summary"))
                            description = text;
                        else if(name.equals("entry"))
                            data.add(new Entry(location, road, cause, description));
                        else {}
                        break;
                }
                event = parser.next();
            }
            stream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


        // GOOGLE DIRECTIONS

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        ArrayList<Route> routes = null;

        if(intent.hasExtra("from") && intent.hasExtra("to")) {

            try {
                StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE + OUT_JSON);
                sb.append("origin=" + URLEncoder.encode(intent.getStringExtra("from"), "utf8"));
                sb.append("&destination=" + URLEncoder.encode(intent.getStringExtra("to"), "utf8"));
                sb.append("&alternatives=true");

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error processing Directions API URL", e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to Directions API", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            if(jsonResults.length() > 0) {

                try {
                    // Create a JSON object hierarchy from the results
                    JSONObject jsonObj = new JSONObject(jsonResults.toString());

                    String status = jsonObj.getString("status");

                    if (status.equals("OK")) {

                       routes = new ArrayList<Route>();
                       JSONArray routesJsonArray = jsonObj.getJSONArray("routes");
                       JSONObject routesJsonObject;
                       JSONObject polyJsonObject;

                        Log.d("TEST","Nasel "+routesJsonArray.length()+" poti");

                       for(int i = 0; i < routesJsonArray.length(); i++)
                       {
                           routesJsonObject = routesJsonArray.getJSONObject(i);
                           polyJsonObject = routesJsonObject.getJSONObject("overview_polyline");
                           routes.add(new Route(polyJsonObject.getString("points"), "", ""));
                       }

                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Cannot process JSON results", e);
                }
            }
        }

        // TRAFFIC REPORT

        TrafficReport trafficReport = null;

        try {
            URL url = new URL(Constants.DATA_URL2);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);

            // start parsing data
            int event = parser.getEventType();
            String text = null, report = null;
            while(event != XmlPullParser.END_DOCUMENT)
            {
                String name = parser.getName();
                switch(event){
                    case XmlPullParser.START_TAG: break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equals("opis"))
                            report = text;
                        if(name.equals("datum"))
                            trafficReport = new TrafficReport(text, report);
                        break;
                }
                event = parser.next();
            }
            stream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }



        // SEND NEW DATA
        Intent dataIntent = new Intent(Constants.INTENT_ID1);
        dataIntent.putParcelableArrayListExtra(Constants.DATA_KEY1, data);
        dataIntent.putParcelableArrayListExtra(Constants.DATA_KEY2, routes);
        dataIntent.putExtra(Constants.DATA_KEY3, trafficReport);
        LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);
    }
}
