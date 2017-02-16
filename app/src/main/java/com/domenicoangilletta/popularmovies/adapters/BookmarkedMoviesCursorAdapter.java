package com.domenicoangilletta.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.data.MovieContract;
import com.domenicoangilletta.popularmovies.utils.DbBitmapUtility;

/**
 * Created by domangi on 13/02/17.
 */

public class BookmarkedMoviesCursorAdapter extends RecyclerView.Adapter<BookmarkedMoviesCursorAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    final private BookmarkedMovieItemClickListener mOnClickListener;

    // Click Listener Interface
    public interface BookmarkedMovieItemClickListener {
        void onBookmarkedMovieItemClick(int clickedItemIndex, String movie_id);
    }

    public BookmarkedMoviesCursorAdapter(Context context, BookmarkedMovieItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public BookmarkedMoviesCursorAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate view
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookmarkedMoviesCursorAdapter.MovieViewHolder holder, int position) {
        // set image and id
        int imageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_BLOB);
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);

        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(idIndex);
        byte[] image_blob = mCursor.getBlob(imageIndex);

        Bitmap image = DbBitmapUtility.getImage(image_blob);
        holder.poster.setImageBitmap(image);

        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.iv_movie_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onBookmarkedMovieItemClick(clickedPosition, v.getTag().toString());
        }
    }
}
