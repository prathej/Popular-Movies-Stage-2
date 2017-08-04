package com.ravitheja.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.ravitheja.popularmovies.data.MovieContract;
import com.ravitheja.popularmovies.utilities.DateUtilities;

import java.text.ParseException;

import static com.ravitheja.popularmovies.FetchReviewAsyncTask.LOG_TAG;

public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Double mVoteAverage;
    private String mVotes;
    private String mReleaseDate;
    private String mId;


    public Movie() {
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }


    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }


    public void setOverview(String overview) {
        if(!overview.equals("null")) {
            mOverview = overview;
        }
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVotes = voteAverage;
    }


    public void setReleaseDate(String releaseDate) {
        if(!releaseDate.equals("null")) {
            mReleaseDate = releaseDate;
        }
    }

    public void setId(String id) {
        mId= id;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

        return TMDB_POSTER_BASE_URL + mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }


    public String getReleaseDate() {
        return mReleaseDate;
    }


    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + "/10";
    }


    public String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getId(){
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeValue(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
    }

    public Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = (Double) in.readValue(Double.class.getClassLoader());
        mReleaseDate = in.readString();
        mId = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    protected boolean isFavorite(Context context){

        Cursor retCursor=null;
        try{
            retCursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry.COLUMN_MOVIEID},
                    MovieContract.MovieEntry.COLUMN_MOVIEID + "=?",
                    new String[]{this.mId},
                    null);
        }catch (Exception e){
            Log.e(LOG_TAG, "Failed to find data");
            e.printStackTrace();
        }

        //Now return Cursor contains the movie row if its a favorite
        if(retCursor!=null){  //indicates movie is a favorite
            boolean favorite = retCursor.getCount()>0;
            retCursor.close();
            return favorite;
        }
        return false;
    }

    boolean saveToFavorites(Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIEID, mId);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mOriginalTitle);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, mPosterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mOverview);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, mVoteAverage);

        String releaseDate = getFormattedReleaseData(context,mReleaseDate);
        Log.d(LOG_TAG,"Release Date =" + releaseDate);

        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);

        Uri uri = context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(context, "Movie added to Favorites = " + uri.toString(), Toast.LENGTH_LONG).show();
            return true;
        }else {
            Toast.makeText(context, "Error adding movie to Favorites ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private String getPosterPath(String path){
        String posterPath = path;
        String retPosterPath = null;
        for(String retPath:posterPath.split("/")){
            retPosterPath= retPath;
        }
        return retPosterPath;
    }


    public boolean removeFromFavorites(Context context) {
        int deletedRows = context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIEID+ "=?",new String[]{this.mId});
        if (deletedRows>0){
            Toast.makeText(context, "Movie Deleted From Favorites",Toast.LENGTH_LONG).show();
            return true;
        }else {
            Toast.makeText(context,"Error removing movie from favorites", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public String getFormattedReleaseData(Context context, String releaseDate){
        String formattedDate;
        formattedDate=releaseDate;

        if(formattedDate != null) {
            try {
                formattedDate = DateUtilities.getLocalizedDate(context,
                        formattedDate, getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {

            formattedDate = context.getResources().getString(R.string.no_release_date_found);
        }
        return formattedDate;
    }
}
