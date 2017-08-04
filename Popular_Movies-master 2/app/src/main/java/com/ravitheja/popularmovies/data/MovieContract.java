package com.ravitheja.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    public MovieContract(){

    }

    public static final String AUTHORITY = "com.ravitheja.popularmovies";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES)
                                                .build();

        // movies table and column names
        public static final String TABLE_NAME = "movies";

        //columns
        public static final String COLUMN_MOVIEID="movieId";
        public static final String COLUMN_MOVIE_TITLE="MovieTitle";
        public static final String COLUMN_MOVIE_POSTER="MoviePoster";
        public static final String COLUMN_MOVIE_OVERVIEW="MovieOverview";
        public static final String COLUMN_MOVIE_VOTE_AVG="MovieVoteAvg";
        public static final String COLUMN_MOVIE_RELEASE_DATE="MovieReleaseDate";


    }
}
