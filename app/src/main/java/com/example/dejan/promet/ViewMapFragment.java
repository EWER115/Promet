package com.example.dejan.promet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dejan on 22.7.2014.
 */
public class ViewMapFragment extends Fragment {

    private GoogleMap map;
    private ArrayList<Marker> markers = null;
    private ArrayList<Polyline> polyMap = null;
    private Polyline selectedPoly = null;
    private ArrayList<Entry> trafficData = null;
    private ArrayList<Route> routes = null;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(Constants.INTENT_ID2));
    }

    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        // change poly selection on click
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                for(Polyline poly : polyMap){
                    if(PolyUtil.isLocationOnPath(latLng, poly.getPoints(), false, Constants.zoomDist.get((int)map.getCameraPosition().zoom))){
                        if(poly.equals(selectedPoly))
                              return;
                        else{

                            if(markers.size() > 2){

                                for(int i = 2; i < markers.size(); i++)
                                    markers.remove(i).remove();

                            }

                            selectedPoly.setColor(Color.parseColor("#9e9e9e"));
                            selectedPoly.setZIndex(100);
                            poly.setColor(Color.parseColor("#03a9f4"));
                            poly.setZIndex(200);
                            selectedPoly = poly;

                                   for(Entry entry : trafficData){
                                       LatLng loc = new LatLng(entry.x, entry.y);
                                       if(PolyUtil.isLocationOnPath(loc, selectedPoly.getPoints(),false,20)){
                                           MarkerOptions tmp = new MarkerOptions()
                                                   .title(entry.cause)
                                                   .snippet(entry.description)
                                                   .position(loc);
                                           markers.add(map.addMarker(tmp));
                                       }
                                   }

                            return;
                        }
                    }
                }

            }
        });


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = getActivity().getLayoutInflater().inflate(R.layout.marker_content, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView content = (TextView) view.findViewById(R.id.content);
                title.setText(marker.getTitle());
                content.setText(marker.getSnippet());

                return view;
            }
        });

        map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(46.0510800,14.5051300) , 8.0f) );

        return rootView;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            trafficData = intent.getParcelableArrayListExtra(Constants.DATA_KEY1);
            routes = intent.getParcelableArrayListExtra(Constants.DATA_KEY2);
            new MarkerTask().execute(intent);
        }
    };


    class MarkerTask extends AsyncTask<Intent, Void, ArrayList<PolylineOptions> >{

        private LatLngBounds bounds = null;
        private ArrayList<MarkerOptions> mOptions = new ArrayList<MarkerOptions>();

        @Override
        protected void onPreExecute()
        {
            if(polyMap != null) {
                for(Polyline p : polyMap) p.remove();
            }
            if(markers != null)
            {
                for(Marker m : markers) m.remove();
            }

        }

        @Override
        protected ArrayList<PolylineOptions> doInBackground(Intent... params) {

            // POLYLINE PATH FOR DIRECTION
            ArrayList<Route> route = params[0].getParcelableArrayListExtra(Constants.DATA_KEY2);


            if(route == null) return null;

            ArrayList<PolylineOptions> polyOpt = new ArrayList<PolylineOptions>();
            ArrayList<LatLng> drawPoints = new ArrayList<LatLng>();

            for(Route r : route)
            {
                List<LatLng> tmpPoints = PolyUtil.decode(r.poly);
                drawPoints.addAll(tmpPoints);
                polyOpt.add(new PolylineOptions().addAll(tmpPoints).color(Color.parseColor("#9e9e9e")));

            }

            polyOpt.get(0).color(Color.parseColor("#03a9f4")); // set color of main direction

            LatLngBounds.Builder builder = LatLngBounds.builder();

            for(LatLng point : drawPoints)
                builder.include(point);
            bounds = builder.build();


            // START-CILJ marker
            IconGenerator iconGen = new IconGenerator(getActivity().getApplicationContext());
            iconGen.setStyle(IconGenerator.STYLE_GREEN);

            Bitmap iconBitMapStart = iconGen.makeIcon("START");
            Bitmap iconBitMapFinish = iconGen.makeIcon("CILJ");


            mOptions.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitMapStart))
                    .position(drawPoints.get(0)));


            mOptions.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitMapFinish))
                    .position(drawPoints.get(drawPoints.size()-1)));


            //TRAFFIC DATA
            ArrayList<Entry> trafficDataAll = params[0].getParcelableArrayListExtra(Constants.DATA_KEY1);

            if(trafficDataAll.size() == 0) return null;

            ArrayList<Entry> trafficDataRel = new ArrayList<Entry>();

            for(Entry entry : trafficDataAll){
                LatLng loc = new LatLng(entry.x, entry.y);
                if(PolyUtil.isLocationOnPath(loc, polyOpt.get(0).getPoints(), false, 20)) {
                    trafficDataRel.add(entry);

                    mOptions.add(new MarkerOptions()
                            .position(loc)
                            .title(entry.cause)
                            .snippet(entry.description));

                }
            }

            return polyOpt;
        }

        @Override
        protected void onPostExecute(ArrayList<PolylineOptions> polyOpt){
            if(polyOpt != null) {
                polyMap = new ArrayList<Polyline>();
                markers = new ArrayList<Marker>(mOptions.size());


                Polyline tmp;
                for(int i = 1; i < polyOpt.size(); i++) {
                    tmp = map.addPolyline(polyOpt.get(i));
                    tmp.setZIndex(100);
                    polyMap.add(tmp);
                }
                selectedPoly = map.addPolyline(polyOpt.get(0));
                selectedPoly.setZIndex(200);
                polyMap.add(selectedPoly);


                for (MarkerOptions mOpt : mOptions)
                    markers.add(map.addMarker(mOpt));

                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }
    }


}
