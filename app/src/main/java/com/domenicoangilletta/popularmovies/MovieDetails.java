package com.domenicoangilletta.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetails extends AppCompatActivity {
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mOverview = (TextView) findViewById(R.id.tv_movie_overview);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mPoster = (ImageView) findViewById(R.id.iv_movie_detail_image);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            String movie_json_string = intent.getStringExtra(Intent.EXTRA_TEXT);
            setMovieData(movie_json_string);
        }
    }

    private void setMovieData(String movie_json_string){
        try {
            JSONObject movie_json = new JSONObject(movie_json_string);
            String title        = movie_json.getString("title");
            String poster_url   = movie_json.getString("poster_url");
            String overview     = movie_json.getString("overview");
            String rating       = movie_json.getString("voteAverage");
            String release_date = movie_json.getString("releaseDate");

            mTitle.setText(title);
            Picasso.with(this).load(poster_url).into(mPoster);
            mOverview.setText(overview);
            mRating.setText(getString(R.string.avg_rating_label) +": " + rating);
            mReleaseDate.setText(getString(R.string.release_date_label) + ": " + release_date);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
