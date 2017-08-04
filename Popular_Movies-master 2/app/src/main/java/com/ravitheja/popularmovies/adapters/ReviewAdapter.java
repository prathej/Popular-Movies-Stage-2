package com.ravitheja.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ravitheja.popularmovies.R;
import com.ravitheja.popularmovies.Review;


public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final Review[] mReview;

    public ReviewAdapter(Context context, Review[] review){
        mContext=context;
        mReview=review;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list,parent,false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyItemHolder)holder).authorTextView.setText(mReview[position].getAuthor());
        ((MyItemHolder) holder).contentTextView.setText(mReview[position].getContent());
    }

    @Override
    public int getItemCount() {
        return mReview.length;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder{
        TextView authorTextView, contentTextView;

        public MyItemHolder(View itemView) {
            super(itemView);

            authorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            contentTextView= (TextView) itemView.findViewById(R.id.tv_review);
        }
    }
}
