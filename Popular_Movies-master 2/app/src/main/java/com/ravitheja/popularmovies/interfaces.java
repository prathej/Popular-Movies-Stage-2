package com.ravitheja.popularmovies;

interface OnFetchTaskCompleted {
    public void onTaskCompleted(Movie[] movies);

}

interface OnTrailerTaskCompleted{
    public void onTrailerTaskCompleted(Trailer[] trailers);
}

interface OnReviewTaskCompleted{
    public void onReviewTaskCompleted(Review[] reviews);
}
