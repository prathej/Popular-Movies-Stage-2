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

public class FetchReviewAsyncTask extends AsyncTask<String, Void, Review[]> {
    final static String LOG_TAG =FetchReviewAsyncTask.class.getSimpleName();
    public OnReviewTaskCompleted mListener;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Review[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader =null;
        String reviewsJsonStr = null;

        if(params.length==0){
            return null;
        }

        try {
            URL url = NetworkUtils.buildReviewsUrl(params);

            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();

            if(inputStream == null){
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

            reviewsJsonStr=buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getReviewsDataFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Review[] getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ID = "id";
        final String TAG_AUTHOR = "author";
        final String TAG_CONTENT="content";
        final String TAG_URL = "url";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray resultsArray = reviewsJson.getJSONArray(TAG_RESULTS);

        Review[] reviews = new Review[resultsArray.length()];

        for(int i=0; i<resultsArray.length();i++){
            reviews[i] = new Review();

            JSONObject reviewInfo = resultsArray.getJSONObject(i);
            reviews[i].setId(reviewInfo.getString(TAG_ID));
            reviews[i].setAuthor(reviewInfo.getString(TAG_AUTHOR));
            reviews[i].setContent(reviewInfo.getString(TAG_CONTENT));
            reviews[i].setUrl(reviewInfo.getString(TAG_URL));



        }
        Log.d(LOG_TAG,"Data Added successfully");
        return reviews;

    }

    @Override
    protected void onPostExecute(Review[] reviews) {

        if(reviews !=null){
            mListener.onReviewTaskCompleted(reviews);
        }
    }
}
