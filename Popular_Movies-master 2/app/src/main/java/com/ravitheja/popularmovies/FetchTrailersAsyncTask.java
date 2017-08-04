package com.ravitheja.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import com.ravitheja.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 26-07-2017.
 */

public class FetchTrailersAsyncTask extends AsyncTask<String, Void, Trailer[] > {

    private final String LOG_TAG = FetchTrailersAsyncTask.class.getSimpleName();
    public OnTrailerTaskCompleted mListener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Trailer[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailersJsonStr = null;

        if (params.length == 0) {
            return null;
        }

        try {
            URL url = NetworkUtils.buildVideosUrl(params);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            trailersJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getTrailersDataFromJson(trailersJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Trailer[] getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ID = "id";
        final String TAG_KEY = "key";

        JSONObject trailersJSON = new JSONObject(trailersJsonStr);
        JSONArray resultsArray = trailersJSON.getJSONArray(TAG_RESULTS);

        Trailer[] trailers = new Trailer[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            trailers[i] = new Trailer();
            JSONObject trailerInfo = resultsArray.getJSONObject(i);

            trailers[i].setId(trailerInfo.getString(TAG_ID));
            trailers[i].setKey(trailerInfo.getString(TAG_KEY));
        }

        return trailers;
    }


    @Override
    protected void onPostExecute(Trailer[] trailers) {
        if(trailers != null){
            mListener.onTrailerTaskCompleted(trailers);
        }
    }
}
