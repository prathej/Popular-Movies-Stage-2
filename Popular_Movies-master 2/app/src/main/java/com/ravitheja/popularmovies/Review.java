package com.ravitheja.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 27-07-2017.
 */

public class Review implements Parcelable {
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public void setId(String id){
        mId=id;
    }

    public void setAuthor(String author){
        mAuthor=author;
    }

    public void setContent(String content){
        mContent=content;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getId(){
        return mId;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getContent(){
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }


    public Review(){}

    protected Review(Parcel in) {
        mId=in.readString();
        mAuthor=in.readString();
        mContent=in.readString();
    }

      @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


}
