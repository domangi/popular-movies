package com.domenicoangilletta.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.models.Trailer;

import java.util.LinkedList;

/**
 * Created by domenicoangilletta on 28/01/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private int mNumberItems;
    private LinkedList<Trailer> trailers;
    final private TrailerItemClickListener mOnClickListener;

    public TrailerAdapter(LinkedList<Trailer> trailers, TrailerItemClickListener listener){
        mNumberItems = trailers.size();
        this.trailers = trailers;
        mOnClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForTrailerItem = R.layout.movie_trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForTrailerItem, parent, shouldAttachToParentImmediately);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    // Click Listener Interface
    public interface TrailerItemClickListener {
        void onTrailerItemClick(int clickedItemIndex);
    }

    // Movie ViewHolder
    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // reference image
        private TextView mTrailerTitle;
        private LinearLayout layout;
        private ImageView playButton;

        public TrailerViewHolder(View itemView){
            super(itemView);
            mTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            if(index < trailers.size() && mTrailerTitle!=null)
                mTrailerTitle.setText(trailers.get(index).getName());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onTrailerItemClick(clickedPosition);
        }
    }

}
