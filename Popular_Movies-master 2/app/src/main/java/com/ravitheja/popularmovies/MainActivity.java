package com.ravitheja.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ravitheja.popularmovies.Settings.SettingsActivity;
import com.ravitheja.popularmovies.adapters.ImageAdapter;
import com.ravitheja.popularmovies.data.MovieContract;
import com.ravitheja.popularmovies.databinding.ActivityMainBinding;

import static com.ravitheja.popularmovies.R.string.pref_sort_high_rated_values;
import static com.ravitheja.popularmovies.R.string.pref_sort_key;

public class MainActivity extends AppCompatActivity implements OnFetchTaskCompleted,
        SharedPreferences.OnSharedPreferenceChangeListener{
    ActivityMainBinding mBinding;
   GridView mGridView;


    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mGridView=mBinding.gridView;
        mGridView.setOnItemClickListener(movieClickListener);

        Movie mMovies;

        setUpSharedPreferences();

        if (savedInstanceState == null) {
            String sortMethod = getSortMethod();

            if ((sortMethod.equals(getResources().getString(R.string.pref_sort_most_popular_values)))
                    || (sortMethod.equals(getResources().getString(pref_sort_high_rated_values)))) {
                loadMovieData(sortMethod);
            } else {
                loadFavoriteMovies();
            }
        } else {
            Parcelable[] parcelable= savedInstanceState.
                    getParcelableArray(getString(R.string.movie_data));
            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int i = 0; i < numMovieObjects; i++) {
                    movies[i] = (Movie) parcelable[i];
                }

                mGridView.setAdapter(new ImageAdapter(this, movies));
            }
        }

    }

    private void setUpSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }


   private final GridView.OnItemClickListener movieClickListener= new GridView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Movie movie = (Movie) adapterView.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(getResources().getString(R.string.movie_data), movie);

            startActivity(intent);
        }
    };

    private void loadMovieData(String sortMethod){
        if(isNetworkAvailable()) {
            FetchMovieAsyncTask fetchMovieAsyncTask = new FetchMovieAsyncTask();
            fetchMovieAsyncTask.mListener = this;
            fetchMovieAsyncTask.execute(sortMethod);
        }else{
            Toast.makeText(this,R.string.error_need_internet,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int MovieObjectsCount = mGridView.getCount();
        if (MovieObjectsCount > 0) {

            Movie[] movies = new Movie[MovieObjectsCount];
            for (int i = 0; i < MovieObjectsCount; i++) {
                movies[i] = (Movie) mGridView.getItemAtPosition(i);
            }

            // Save Movie objects to bundle
            outState.putParcelableArray(getString(R.string.movie_data), movies);
        }

        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onTaskCompleted(Movie[] movies) {
        mGridView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
    }


    private String getSortMethod(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(pref_sort_key),
                getString(R.string.pref_sort_most_popular_values));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String value = sharedPreferences.getString(key, "");
        Log.v(LOG_TAG, value);
        if ((value.equals(getResources().getString(R.string.pref_sort_most_popular_values)))||
                (value.equals(getResources().getString(pref_sort_high_rated_values)))){
            loadMovieData(value);
        } else   {
            loadFavoriteMovies();
        }
    }

   protected void loadFavoriteMovies() {
        //SQLiteDatabase db = MovieDbHelper.getReadableDatabase();
       Cursor retCursor=null;
        try{
            retCursor = this.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        }catch (Exception e){
            Log.e(LOG_TAG, "Failed to load data");
            e.printStackTrace();
        }

       int count = retCursor.getCount();
       Movie[] movies = new Movie[count];
       for (int i = 0; i < count; i++) {
           movies[i] = new Movie();
           if (retCursor.moveToPosition(i)) {
               String movieId = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEID));
               String movieTitle = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
               String moviePosterPath = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
               String movieOverview = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
               String movieVote = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG));
               String movieReleaseDate = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));

               movies[i].setOriginalTitle(movieTitle);
               movies[i].setPosterPath("/" +moviePosterPath);
               movies[i].setOverview(movieOverview);
               movies[i].setVoteAverage(Double.parseDouble(movieVote));
               movies[i].setReleaseDate(movieReleaseDate);
               movies[i].setId(movieId);
           }
       }
       mGridView.setAdapter(new ImageAdapter(this, movies));

    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortMethod = getSortMethod();
        if(sortMethod.equals(getResources().getString(R.string.pref_sort_user_favourite_values))) {
            loadFavoriteMovies();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);
    }

}
