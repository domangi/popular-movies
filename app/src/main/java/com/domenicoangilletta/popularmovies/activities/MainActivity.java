package com.domenicoangilletta.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.domenicoangilletta.popularmovies.BuildConfig;
import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.adapters.MovieAdapter;
import com.domenicoangilletta.popularmovies.models.Movie;
import com.domenicoangilletta.popularmovies.tasks.TheMovieDBQueryTask;
import com.domenicoangilletta.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener
{

    private static final int NUM_COLS = 2;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mMovies;
    private String sortBy;
    private final String SORT_BY_POPULAR="POPULAR"; // only for internal usage, never shown to user
    private final String SORT_BY_TOP_RATED="TOP_RATED"; // only for internal usage, never shown to user
    private final String SORT_BY_BOOKMARKED="BOOKMARKED"; // only for internal usage, never shown to user
    private TextView mSortedBy;
    private MainActivity main_activity;
    private LinkedList<Movie> currentMovies;
    private final static String THEMOBIEDB_API_KEY = BuildConfig.THEMOVIEDB_API_KEY;
    private static final String TAG = MainActivity.class.getSimpleName();


    /*
        Grid Item Click Handler
        When clicked execute an Explicit Intent that opens a Movie Detail View,
        passing movie details data as stringified json
     */
    @Override
    public void onMovieItemClick(int clickedItemIndex) {
        Context context = MainActivity.this;
        Class destinationActivity = MovieDetailActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        Movie clickedMovie = currentMovies.get(clickedItemIndex);
        intent.putExtra(Intent.EXTRA_TEXT, clickedMovie.toJson());

        startActivity(intent);
    }

    /*
        used by MovieAdapter to set currentMovies Movie List
     */
    public void setCurrentMovies(LinkedList<Movie> movies){
        currentMovies = movies;
    }

    /*
        Show menu items for ordering the list of Movies
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /*
        Handle menu item clicks
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        if(itemSelected == R.id.mi_sort_popular){
            showPopularMovies();
            return true;
        }

        if(itemSelected == R.id.mi_sort_top_rated){
            showTopRatedMovies();
            return true;
        }

        // bookmarked movies shown on distinct activity
        if(itemSelected == R.id.mi_bookmarks){
            Context context = MainActivity.this;
            Class destinationActivity = BookmarksActivity.class;
            Intent intent = new Intent(context, destinationActivity);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Sets the sorting preferences to SORT_BY_POPULAR and updates the movie list
     */
    public void showPopularMovies(){
        sortBy = SORT_BY_POPULAR;
        loadMovies();
    }

    /*
        Sets the sorting preferences to SORT_BY_TOP_RATED and updates the movie list
     */
    public void showTopRatedMovies(){
        sortBy = SORT_BY_TOP_RATED;
        loadMovies();
    }

    /*
        Initialize all instance variables
        Link variables to UI Elements
        Set a LayoutManager
        Load the movie data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_activity = this;
        mSortedBy = (TextView) findViewById(R.id.tv_sorted_by);
        sortBy = SORT_BY_POPULAR;
        mMovies = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLS);
        mMovies.setLayoutManager(layoutManager);
        mMovies.setHasFixedSize(true);
        loadMovies();
    }

    /*
        Based on the current value of sorting preferences (sortBy) updated a UI TextView
        and set the path used to fetch movie data
        Fetch Movie Data using an Async Task or from local DB
        Use different adapters
     */
    public void loadMovies(){
        String path = "";

        if(sortBy == SORT_BY_POPULAR) {
            mSortedBy.setText(R.string.search_by_popularity_label);
            path = "/popular";
        }else {
            mSortedBy.setText(R.string.search_by_top_rated_label);
            path = "/top_rated";
        }

        URL url = NetworkUtils.buildUrl(path, "1", THEMOBIEDB_API_KEY);
        new TheMovieDBQueryTask(mMovies, currentMovies, main_activity, mMovieAdapter).execute(url);

    }
}
