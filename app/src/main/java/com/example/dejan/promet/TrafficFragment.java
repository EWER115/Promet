package com.example.dejan.promet;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Dejan on 22.7.2014.
 */
public class TrafficFragment extends Fragment {

    private AutoCompleteTextView from;
    private AutoCompleteTextView to;
    private ListView listReport;
    private Button btn_get;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.traffic_fragment, container, false);

        from = (AutoCompleteTextView) rootView.findViewById(R.id.from);
        to = (AutoCompleteTextView) rootView.findViewById(R.id.to);
        btn_get = (Button) rootView.findViewById(R.id.btn_get);
        listReport = (ListView) rootView.findViewById(R.id.reportList);
        from.setAdapter(new PlacesAutoCompleteAdapter(getActivity(),R.layout.list_item));
        to.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final Intent serviceIntent = new Intent(getActivity(), dataPullService.class);
                    serviceIntent.putExtra("from", from.getText().toString());
                    serviceIntent.putExtra("to", to.getText().toString());
                    getActivity().startService(serviceIntent);

                ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
                pager.setCurrentItem(1);

                //new Directions(getActivity(), listReport).execute(from.getText().toString(), to.getText().toString());
            }
        });


        return rootView;
    }



}
