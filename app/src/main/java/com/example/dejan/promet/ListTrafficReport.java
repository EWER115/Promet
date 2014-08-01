package com.example.dejan.promet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Dejan on 22.7.2014.
 */
public class ListTrafficReport extends ArrayAdapter<Entry> {

    Context context;


    public ListTrafficReport(Context context, int resourceId, ArrayList<Entry> data) {
        super(context, resourceId, data);
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_single, null);
        ImageView img = (ImageView) rowView.findViewById(R.id.img);
        TextView text = (TextView) rowView.findViewById(R.id.report);
        Entry entry = getItem(position);

        text.setText(entry.description);
        setIcon(img, entry);

        return rowView;
    }

    private void setIcon(ImageView img, Entry entry)
    {
        String cause = entry.cause;
        if(cause.equalsIgnoreCase("zastoj")) img.setImageResource(Constants.SIGN_IMG.get("zastoj"));
        else if(cause.equalsIgnoreCase("izredni dogodek")) img.setImageResource(Constants.SIGN_IMG.get("dogodek"));
        else if(cause.equalsIgnoreCase("delo na cesti")) img.setImageResource(Constants.SIGN_IMG.get("delo"));
        else if(cause.equalsIgnoreCase("zaprta cesta")) img.setImageResource(Constants.SIGN_IMG.get("zaprta"));
        else if(cause.equalsIgnoreCase("prepoved za tovornjake")) img.setImageResource(Constants.SIGN_IMG.get("izlocanje"));
        else if(cause.equalsIgnoreCase("nesreča")) img.setImageResource(Constants.SIGN_IMG.get("nesreca"));
        else img.setImageResource(Constants.SIGN_IMG.get("dogodek"));
    }
}
