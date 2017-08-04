package com.ravitheja.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import  com.ravitheja.popularmovies.data.MovieContract.MovieEntry;

import static com.ravitheja.popularmovies.data.MovieContract.MovieEntry.TABLE_NAME;


public class MovieContentProvider extends ContentProvider {
    private MovieDbHelper movieDbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES,MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_MOVIES+"/#",MOVIES_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context= getContext();
        movieDbHelper = new MovieDbHelper(context);
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri,String[] projection,String selection,String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match){
            case MOVIES:
                retCursor=db.query(MovieEntry.TABLE_NAME,
                           projection,selection,selectionArgs,
                            null,null,sortOrder);
                break;

            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);

                String mSelection = "id=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor=db.query(MovieEntry.TABLE_NAME,
                                    projection,
                                    mSelection,
                                    mSelectionArgs,
                                    null,null,
                                    sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case MOVIES:
                long id = db.insert(TABLE_NAME,null,contentValues);

                if(id>0){
                    returnUri= ContentUris.withAppendedId(MovieEntry.CONTENT_URI,id);
                }
                else {
                    throw new SQLException("Failed to Insert into new row" + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri,  String selection,  String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deleteId;

        switch(match) {
            case MOVIES:
                deleteId = db.delete(MovieEntry.TABLE_NAME,
                        selection,selectionArgs);
                break;

            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "id=?";
                String[] mSelectionArgs = new String[]{id};

                deleteId = db.delete(MovieEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if(deleteId>0){
            getContext().getContentResolver().notifyChange(uri,null);

        }
            return deleteId;
    }

    @Override
    public int update(@NonNull Uri uri,  ContentValues contentValues,  String s, String[] strings) {
        return 0;
    }
}
