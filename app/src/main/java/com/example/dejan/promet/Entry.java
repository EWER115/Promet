package com.example.dejan.promet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dejan on 16.7.2014.
 */
public class Entry implements Parcelable {

    public final double x, y;
    public final String road, cause, description;
    
    public Entry(String location, String road, String cause, String description)
    {
        String parse[] = location.split(",");
        this.x = Double.parseDouble(parse[0]);
        this.y = Double.parseDouble(parse[1]);
        this.road = road.split(":")[0].trim();
        this.cause = cause.split(":")[1].trim();
        this.description = description;
    }

    public Entry(Parcel in)
    {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.road = in.readString();
        this.cause = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeString(road);
        dest.writeString(cause);
        dest.writeString(description);
    }

    public static final Creator<Entry> CREATOR
            = new Creator<Entry>() {
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
