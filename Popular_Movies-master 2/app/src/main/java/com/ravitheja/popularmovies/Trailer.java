package com.ravitheja.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


public class Trailer implements Parcelable {
    private String mId;
    private String mKey;

    public Trailer(){

    }

    private Trailer (Parcel in){
        mId = in.readString();
        mKey = in.readString();
    }

    public void setId(String id){
        mId = id;
    }

    public void setKey(String key){
        mKey=key;
    }

    public String getId(){
    return mId;
    }

    public String getKey(){
        return mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
    }

    public static final Creator<Trailer> CREATOR= new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };


}
