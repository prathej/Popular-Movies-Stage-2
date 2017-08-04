package com.ravitheja.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ravitheja.popularmovies.data.MovieContract.MovieEntry;


public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG=MovieDbHelper.class.getSimpleName();
    private static final String DB_NAME ="movies" ;
    private static final int DB_VERSION = 1;

    public MovieDbHelper (Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "(" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIEID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_VOTE_AVG + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL" + ");" ;



        Log.d(LOG_TAG,"query" + SQL_CREATE_MOVIE_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
          sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
          onCreate(sqLiteDatabase);
    }
}
