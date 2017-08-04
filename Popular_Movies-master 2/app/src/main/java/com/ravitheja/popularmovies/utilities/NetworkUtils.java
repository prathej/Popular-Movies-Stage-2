/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ravitheja.popularmovies.utilities;

import android.net.Uri;

import com.ravitheja.popularmovies.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {


    private static final String TAG = NetworkUtils.class.getSimpleName();

    //replace * by api key
    final static String mApiKey = BuildConfig.MY_MOVIE_DB_API_KEY;

    final static String mBASE_URL = "https://api.themoviedb.org/3/movie";

    final static String mAPI_KEY_PARAM = "api_key";

    final static String mAPI_VIDEOS_PARAM="videos";
    final static String mAPI_REVIEWS_PARAM="reviews";



    public static URL buildUrl(String[] params) throws MalformedURLException{
        Uri builtUri = Uri.parse(mBASE_URL).buildUpon()
                .appendPath(params[0])
                .appendQueryParameter(mAPI_KEY_PARAM, mApiKey)
                .build();

        URL url = null;
        url = new URL(builtUri.toString());
        return url;
    }

    public static URL buildVideosUrl(String[] params) throws MalformedURLException{
        Uri builtUri = Uri.parse(mBASE_URL).buildUpon()
                .appendPath(params[0]).appendPath(mAPI_VIDEOS_PARAM)
                .appendQueryParameter(mAPI_KEY_PARAM, mApiKey)
                .build();

        URL url = null;
        url = new URL(builtUri.toString());
        return url;
    }

    public static URL buildReviewsUrl(String[] params) throws MalformedURLException{
        Uri builtUri = Uri.parse(mBASE_URL).buildUpon()
                .appendPath(params[0]).appendPath(mAPI_REVIEWS_PARAM)
                .appendQueryParameter(mAPI_KEY_PARAM, mApiKey)
                .build();

        URL url = null;
        url = new URL(builtUri.toString());
        return url;
    }

}