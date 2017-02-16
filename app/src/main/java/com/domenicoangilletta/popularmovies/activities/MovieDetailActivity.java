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
import com.domenicoangilletta.popularmovies.tasks.FetchReviewsTask;
import com.domenicoangilletta.popularmovies.tasks.FetchTrailersTask;
import com.domenicoangilletta.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.LinkedList;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener {
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;
    private Button mBookmarkButton;

    private Movie selectedMovie;
    private MovieDetailActivity movie_details;

    private TextView mTrailersLabel;
    private RecyclerView mTrailerList;
    private LinkedList<Trailer> trailers;

    private TextView mReviewsLabel;
    private RecyclerView mReviewList;
    private LinkedList<Review> reviews;

    private String ADD_TO_BOOKMARK;
    private String REMOVE_FROM_BOOKMARKS;

    private final static String THEMOBIEDB_API_KEY = BuildConfig.THEMOVIEDB_API_KEY;

    /*
        Handle trailer item click
        Open youtube video link
     */
    @Override
    public void onTrailerItemClick(int clickedItemIndex) {
        // START INTENT
        Trailer trailer = selectedMovie.getTrailers().get(clickedItemIndex);
        String url = trailer.getVideoLink();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /*
        Link Layout elements to local variables
        Use Intent passed data to set local variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createSuper(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movie_details = this;

        ADD_TO_BOOKMARK = getString(R.string.add_to_bookmarks_label);
        REMOVE_FROM_BOOKMARKS = getString(R.string.remove_from_bookmarks_label);

        // link layout elements
        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mOverview = (TextView) findViewById(R.id.tv_movie_overview);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mPoster = (ImageView) findViewById(R.id.iv_movie_detail_image);
        mTrailerList = (RecyclerView) findViewById(R.id.rv_movie_trailers);
        mReviewList = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mBookmarkButton = (Button) findViewById(R.id.btn_add_to_bookmarks);
        mTrailersLabel = (TextView) findViewById(R.id.tv_trailers_label);
        mReviewsLabel = (TextView) findViewById(R.id.tv_reviews_label);

        // set bookmark button click listener
        mBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBookmark();
            }
        });

        // set layout manager for trailer recyler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        mTrailerList.setLayoutManager(linearLayoutManager);
        mTrailerList.setHasFixedSize(true);

        // set layout manager for reviews recyler view
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(getBaseContext());
        mReviewList.setLayoutManager(reviewsLayoutManager);
        mReviewList.setHasFixedSize(true);

        // set selectedMovie variable using Intent passed data and async task to load trailers and reviews
        setSelectedMovie();

        setMovieLayoutData(); // set layout data for selected movie
        initBookmarkButton(); // set bookmark initial state
    }

    public Movie getSelectedMovie() {
        Movie movie = new Movie();
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            String movie_json_string = intent.getStringExtra(Intent.EXTRA_TEXT);
            try{
                JSONObject movie_json   = new JSONObject(movie_json_string);
                String posterPath       = movie_json.getString("posterPath");
                int id                  = movie_json.getInt("id");
                String title            = movie_json.getString("title");
                String overview         = movie_json.getString("overview");
                String vote_average     = movie_json.getString("voteAverage");
                String releaseDate      = movie_json.getString("releaseDate");
                movie = new Movie(id, posterPath, title, overview, vote_average,
                        releaseDate, false);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return movie;
    }

    public void setSelectedMovie(){
        selectedMovie = getSelectedMovie();
        fetchReviews(); // load through Async Task
        fetchTrailers(); // load through Async Task
    }

    public void createSuper(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    /*
        Sets bookmark button stated based on presence of selected movie in bookmarks table
     */
    public void initBookmarkButton(){
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        movieUri = movieUri.buildUpon().appendPath(""+selectedMovie.id).build();
        Cursor cursor = getContentResolver().query(movieUri,null,null,null,null);

        selectedMovie.isBookmarked = cursor.getCount() > 0;
        setBookmarkButtonText();
    }

    /*
        Sets bookmark button text based on bookmark state
     */
    private void setBookmarkButtonText(){
        if(selectedMovie.isBookmarked){
            mBookmarkButton.setText(REMOVE_FROM_BOOKMARKS);
        } else{
            mBookmarkButton.setText(ADD_TO_BOOKMARK);
        }
    }

    /*
        switch movie bookmark:
        if movie is bookmarked, than remove from bookmarks and set text
        if movie is not bookmarked, than bookmark and set text
        show toast notification for action result
     */
    public void toggleBookmark(){
        String buttonText = mBookmarkButton.getText().toString();
        String notificationText = "";

        if(selectedMovie.isBookmarked){
            // REMOVE MOVIE FROM DB
            if(removeFromBookmarks()) {
                selectedMovie.isBookmarked = false;
                buttonText = ADD_TO_BOOKMARK;
                notificationText = getString(R.string.removed_bookmarks_notification);
            } else {
                notificationText = getString(R.string.removed_bookmarks_error);
            }

        } else {
            // ADD MOVIE TO DB
            if(saveToBookmark()){
                selectedMovie.isBookmarked = true;
                buttonText=REMOVE_FROM_BOOKMARKS;
                notificationText= getString(R.string.added_to_bookmarks_notification);
            } else {
                notificationText= getString(R.string.added_to_bookmarks_error);
            }

        }

        Toast.makeText(MovieDetailActivity.this, notificationText, Toast.LENGTH_LONG).show();
        mBookmarkButton.setText(buttonText);
    }

    /*
        Picasso target object used in order to have access to poster bitmap,
        to be saved for offline access if user bookmarks movie
     */
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // loading of the bitmap was a success
            mPoster.setImageBitmap(bitmap);
            selectedMovie.setPoster(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // loading of the bitmap failed
            // TODO do some action/warning/error message
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    /*
        fetch trailers from moviedb
     */
    private void fetchTrailers(){
        String path = "/" + selectedMovie.id + "/videos";
        URL url = NetworkUtils.buildUrl(path, "1", THEMOBIEDB_API_KEY);
        new FetchTrailersTask(trailers, selectedMovie, movie_details).execute(url);
    }

    /*
        fetch reviews from moviedb
     */
    private void fetchReviews(){
        String path = "/" + selectedMovie.id + "/reviews";
        URL url = NetworkUtils.buildUrl(path, "1", THEMOBIEDB_API_KEY);
        new FetchReviewsTask(reviews, selectedMovie, movie_details).execute(url);
    }

    /*
        Set layout elements with data in selected Movie
     */
    public void setMovieLayoutData(){
        mTitle.setText(selectedMovie.title);
        Picasso.with(this).load(selectedMovie.getPosterUrl()).into(target);
        mOverview.setText(selectedMovie.overview);
        mRating.setText(getString(R.string.avg_rating_label) +": " + selectedMovie.voteAverage);
        mReleaseDate.setText(getString(R.string.release_date_label) + ": " + selectedMovie.releaseDate);
    }


    /*
        Save current movie to bookmarks
     */
    public boolean saveToBookmark(){
        ContentValues movieValues = selectedMovie.toContentValues();
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        return uri!=null;
    }

    /*
        Remove current movie from bookmarks
     */
    public boolean removeFromBookmarks(){
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        movieUri = movieUri.buildUpon().appendPath(""+selectedMovie.id).build();
        int deletedRows = getContentResolver().delete(movieUri,null,null);
        return deletedRows>0;
    }

    /*
        build adapter for trailers using selected movies trailers list
    */
    public void renderTrailers(){
        if(selectedMovie.getTrailers()!=null && selectedMovie.getTrailers().size() > 0){
            TrailerAdapter adapter = new TrailerAdapter(selectedMovie.getTrailers(), movie_details );
            mTrailerList.setAdapter(adapter);
            mTrailersLabel.setVisibility(View.VISIBLE);

        }
    }

    public void renderReviews(){
        if(selectedMovie.getReviews()!=null && selectedMovie.getReviews().size() > 0){
            ReviewAdapter adapter = new ReviewAdapter(selectedMovie.getReviews());
            mReviewList.setAdapter(adapter);
            mReviewsLabel.setVisibility(View.VISIBLE);
        }
    }

    public void setReviewList(RecyclerView rv){
        mReviewList = rv;
    }

    public void setTrailerList(RecyclerView rv){
        mTrailerList = rv;
    }
}
