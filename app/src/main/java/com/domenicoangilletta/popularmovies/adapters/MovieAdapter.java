package com.domenicoangilletta.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;
import java.util.LinkedList;

/**
 * Created by domenicoangilletta on 28/01/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private int mNumberItems;
    private LinkedList<Movie> movies;
    final private MovieItemClickListener mOnClickListener;

    public MovieAdapter(int numberOfItems, LinkedList<Movie> movies, MovieItemClickListener listener){
        mNumberItems = numberOfItems;
        this.movies = movies;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForMovieItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForMovieItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    // Click Listener Interface
    public interface MovieItemClickListener {
        void onMovieItemClick(int clickedItemIndex);
    }

    // Movie ViewHolder
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // reference image
        private ImageView mMovieThumb;

        public MovieViewHolder(View itemView){
            super(itemView);
            mMovieThumb = (ImageView) itemView.findViewById(R.id.iv_movie_image);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            if(index < mNumberItems) {
                Context context = mMovieThumb.getContext();
                Movie movie = movies.get(index);
                String url = movie.getThumbUrl("500");
                Picasso.with(context).load(url).into(mMovieThumb);
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieItemClick(clickedPosition);
        }
    }

}
