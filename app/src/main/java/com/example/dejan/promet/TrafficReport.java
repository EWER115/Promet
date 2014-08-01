package com.example.dejan.promet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dejan on 24.7.2014.
 */
public class TrafficReport implements Parcelable {

    public final String time;
    public final String report;

    public TrafficReport(String time, String report){
        this.time = time;
        this.report = report;
    }

    public TrafficReport(Parcel in){
        this.time = in.readString();
        this.report = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(report);
    }

    public static final Creator<TrafficReport> CREATOR
            = new Creator<TrafficReport>() {
        public TrafficReport createFromParcel(Parcel in) {
            return new TrafficReport(in);
        }

        public TrafficReport[] newArray(int size) {
            return new TrafficReport[size];
        }
    };
}
