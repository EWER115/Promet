package com.example.dejan.promet;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Dejan on 22.7.2014.
 */
public class Route implements Parcelable {

    public final String poly;
    public final String distance;
    public final String duration;

    public Route(String poly, String distance, String duration)
    {
        this.poly = poly;
        this.distance =distance;
        this.duration = duration;
    }

    public Route(Parcel in)
    {
        this.poly = in.readString();
        this.distance = in.readString();
        this.duration = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poly);
        dest.writeString(distance);
        dest.writeString(duration);
    }

    public static final Creator<Route> CREATOR
            = new Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
