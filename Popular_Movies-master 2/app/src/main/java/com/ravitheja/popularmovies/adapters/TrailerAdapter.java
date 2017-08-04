package com.ravitheja.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ravitheja.popularmovies.R;
import com.ravitheja.popularmovies.Trailer;
import com.squareup.picasso.Picasso;


public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Trailer[] mTrailer;
    private final Context mContext;

    public TrailerAdapter(Context context, Trailer[] data){
        mContext=context;
        mTrailer=data;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyItemHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.trailerImage);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        view= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.trailer_list,parent,false
        );
        viewHolder=new MyItemHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //String id = mTrailer.get(position).getKey();   -- if we use arraylist instead of array
        String id = mTrailer[position].getKey();
     String thumbnailURL = "https://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
        Picasso.with(mContext)
                .load(thumbnailURL)
                .placeholder(R.drawable.thumbnail)
                .into(((MyItemHolder)holder)
                .imageView);
    }

    @Override
    public int getItemCount() {
        return mTrailer.length;
    }
}
