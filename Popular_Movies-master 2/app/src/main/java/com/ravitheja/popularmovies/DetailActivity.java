package com.ravitheja.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ravitheja.popularmovies.adapters.ReviewAdapter;
import com.ravitheja.popularmovies.adapters.TrailerAdapter;
import com.ravitheja.popularmovies.databinding.ActivityDetailBinding;
import com.ravitheja.popularmovies.utilities.DateUtilities;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class DetailActivity extends AppCompatActivity implements OnTrailerTaskCompleted, OnReviewTaskCompleted {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;
    ActivityDetailBinding mBinding;
    protected RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    Trailer[] mTrailerList;
    Review[] mReviewList;
    //ArrayList<Review> mReviewList;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    TextView notrailerview,noreviewview;
    ImageButton mFavoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        mTrailersRecyclerView= mBinding.trailersRecyclerView;
        mReviewsRecyclerView= mBinding.reviewsRecyclerView;
        notrailerview = mBinding.noTrailerView;
        noreviewview=mBinding.noReviewView;
        mFavoriteButton = mBinding.favoriteButton;


        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(getString(R.string.movie_data))) {
            mMovie = intent.getParcelableExtra(getString(R.string.movie_data));
        }

        if (mMovie.isFavorite(this)){
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        }else{
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
        }

        mBinding.textviewOriginalTitle.setText(mMovie.getOriginalTitle());

                Picasso.with(this)
                .load(mMovie.getPosterPath())
                //.resize(225,280)
                //.error(R.drawable.ic_error_outline_black_48dp)
                .placeholder(R.drawable.ic_sync_black_48dp)
                .into(mBinding.imageviewPoster);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String overView = mMovie.getOverview();
        if (overView == null) {
                mBinding.textviewOverview.setTypeface(null, Typeface.ITALIC);
                overView = getResources().getString(R.string.no_summary_found);
                }
        mBinding.textviewOverview.setText(overView);
        mBinding.textviewVoteAverage.setText(mMovie.getDetailedVoteAverage());


        String releaseDate = getFormattedReleaseData(mMovie.getReleaseDate());

        mBinding.textviewReleaseDate.setText(releaseDate);

        loadTrailers();

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mTrailersRecyclerView.setLayoutManager(trailerLayoutManager);



        mTrailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = "https://www.youtube.com/watch?v=".concat(mTrailerList[position].getKey());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }));

        loadReviews();
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        mReviewsRecyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mReviewList[position].getUrl()));
                startActivity(i);
            }
        }));
    }



    private void loadTrailers() {
        FetchTrailersAsyncTask fetchTrailersAsyncTask = (new FetchTrailersAsyncTask());
        fetchTrailersAsyncTask.mListener=this;
        fetchTrailersAsyncTask.execute(mMovie.getId());
    }

    private void loadReviews() {
        FetchReviewAsyncTask fetchReviewsAsyncTask = (new FetchReviewAsyncTask());
        fetchReviewsAsyncTask.mListener=this;
        fetchReviewsAsyncTask.execute(mMovie.getId());
    }






    @Override
    public void onTrailerTaskCompleted(Trailer[] trailers) {
        mTrailerList = trailers;
        //mTrailersRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(),trailers));
        if (mTrailerList.length==0) {
            mTrailersRecyclerView.setVisibility(View.INVISIBLE);
            notrailerview.setVisibility(View.VISIBLE);
        }else {
            trailerAdapter = new TrailerAdapter(this, mTrailerList);
            mTrailersRecyclerView.setAdapter(trailerAdapter);
        }
    }

    @Override
    public void onReviewTaskCompleted(Review[] reviews) {
        mReviewList = reviews;
        //mTrailersRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(),trailers));
       // Log.d(LOG_TAG,("Reviews" + mReviewList.toString()));
        if (mReviewList.length == 0) {
            noreviewview.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            reviewAdapter = new ReviewAdapter(this, mReviewList);
            mReviewsRecyclerView.setAdapter(reviewAdapter);
        }
    }

    public void onClickAddMovies(View view) {
        Context context = getApplicationContext();
        if (!mMovie.isFavorite(context)) {
            //Movie not a favorite
            if (mMovie.saveToFavorites(context)) {
                mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            }
        } else {
            //Movie is Favorite and if user wishes to remove fit as favorite
            if(mMovie.removeFromFavorites(context)){
                mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }
    }

    public String getFormattedReleaseData(String releaseDate){
        String formattedDate;
        formattedDate=releaseDate;

        if(formattedDate != null) {
            try {
                formattedDate = DateUtilities.getLocalizedDate(this,
                        formattedDate, mMovie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {
            mBinding.textviewReleaseDate.setTypeface(null, Typeface.ITALIC);
            formattedDate = getResources().getString(R.string.no_release_date_found);
        }
        return formattedDate;
    }
}




