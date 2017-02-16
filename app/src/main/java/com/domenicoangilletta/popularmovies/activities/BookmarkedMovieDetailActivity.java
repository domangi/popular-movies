package com.domenicoangilletta.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.domenicoangilletta.popularmovies.BuildConfig;
import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.adapters.ReviewAdapter;
import com.domenicoangilletta.popularmovies.adapters.TrailerAdapter;
import com.domenicoangilletta.popularmovies.data.MovieContract;
import com.domenicoangilletta.popularmovies.models.Movie;
import com.domenicoangilletta.popularmovies.models.Review;
import com.domenicoangilletta.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.LinkedList;


public class BookmarkedMovieDetailActivity extends MovieDetailActivity implements TrailerAdapter.TrailerItemClickListener {
    /*
        Overwrite getSelectedMovie method in order to fetch data from database
     */
    public Movie getSelectedMovie(){
        Movie movie = new Movie();
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_INDEX)){
            String movie_id = intent.getStringExtra(Intent.EXTRA_INDEX);
            Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
            movieUri = movieUri.buildUpon().appendPath(""+movie_id).build();
            Cursor cursor = getContentResolver().query(movieUri,null,null,null,null);
            movie = new Movie(cursor);
        }
        return movie;
    }


    /*
        Override setSelectedMovie in order to render trailers and reviews immediately
     */
    public void setSelectedMovie() {
        super.setSelectedMovie();
        renderReviews();
        renderTrailers();
    }

}
