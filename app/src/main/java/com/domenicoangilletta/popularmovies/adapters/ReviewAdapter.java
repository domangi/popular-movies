package com.domenicoangilletta.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.models.Review;

import java.util.LinkedList;

/**
 * Created by domenicoangilletta on 28/01/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private int mNumberItems;
    private LinkedList<Review> reviews;

    public ReviewAdapter(LinkedList<Review> reviews){
        mNumberItems = reviews.size();
        this.reviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForReviewItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForReviewItem, parent, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    // Movie ViewHolder
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mReviewAuthor;
        private TextView mReviewContent;

        public ReviewViewHolder(View itemView){
            super(itemView);
            mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
        }

        void bind(int index){
            if(index < reviews.size()){
                Review review = reviews.get(index);
                mReviewAuthor.setText(review.getAuthor());
                mReviewContent.setText(review.getContent());
            }
        }

    }

}
