package com.example.dejan.promet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dejan on 21.7.2014.
 */
public class Directions extends AsyncTask<String, Void, ArrayList<Entry>> {

    private static final String LOG_TAG = "TrafficApp";
    private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/directions";
    private static final String OUT_JSON = "/json?";
    private Context context;
    private ListView list;


    public Directions(Context context, ListView list)
    {
        this.context = context;
        this.list = list;
    }


    @Override
    protected ArrayList<Entry> doInBackground(String... params) {

        ArrayList<Entry> data = new ArrayList<Entry>();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE + OUT_JSON);
            sb.append("origin=" + URLEncoder.encode(params[0], "utf8"));
            sb.append("&destination=" + URLEncoder.encode(params[1], "utf8"));

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
            return data;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Directions API", e);
            return data;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray routesJsonArray = jsonObj.getJSONArray("routes");
            JSONObject routesJsonObject = routesJsonArray.getJSONObject(0);
            //JSONArray legsJsonArray = routesJsonObject.getJSONArray("legs");
            //JSONObject legsJsonObject = legsJsonArray.getJSONObject(0);
            //JSONArray stepsJsonArray = legsJsonObject.getJSONArray("steps");
            JSONObject polyJsonObject =  routesJsonObject.getJSONObject("overview_polyline");

            String poly = polyJsonObject.getString("points");
            List<LatLng> points = PolyUtil.decode(poly);

            for(Entry entry : Traffic.data){
                if(PolyUtil.isLocationOnPath(new LatLng(entry.x,entry.y),points,false,20.0)){
                    data.add(entry);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }


        return data;
    }

    protected void onPostExecute(ArrayList<Entry> data)
    {
        list.setAdapter(new ListTrafficReport(context,R.layout.list_single, data));
    }
}
