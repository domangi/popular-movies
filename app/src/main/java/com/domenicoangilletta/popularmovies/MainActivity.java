package com.domenicoangilletta.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener {

    private static final int NUM_MOVIE_ITEMS = 20;
    private static final int NUM_COLS = 2;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mMovies;
    private String sortBy;
    private final String SORT_BY_POPULAR="POPULAR"; // only for internal usage, never shown to user
    private final String SORT_BY_TOP_RATED="TOP_RATED"; // only for internal usage, never shown to user
    private TextView mSortedBy;
    private MainActivity main_activity;
    private LinkedList<Movie> currentMovies;
    private String THEMOBIEDB_API_KEY;

    /*
        Grid Item Click Handler
        When clicked execute an Explicit Intent that opens a Movie Detail View,
        passing movie details data as stringified json
     */
    @Override
    public void onMovieItemClick(int clickedItemIndex) {
        Context context = MainActivity.this;
        Class destinationActivity = MovieDetails.class;
        Intent intent = new Intent(context, destinationActivity);
        Movie clickedMovie = currentMovies.get(clickedItemIndex);
        intent.putExtra(Intent.EXTRA_TEXT, clickedMovie.toJson());

        startActivity(intent);
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
        THEMOBIEDB_API_KEY = getString(R.string.THEMOVIEDB_API_KEY);

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
        Fetch Movie Data using an Async Task
     */
    public void loadMovies(){
        String path = "";
        if (sortBy == SORT_BY_POPULAR) {
            mSortedBy.setText(R.string.search_by_popularity_label);
            path = "/popular";
        }
        else {
            mSortedBy.setText(R.string.search_by_top_rated_label);
            path = "/top_rated";
        }

        URL url = NetworkUtils.buildUrl(path, "1", THEMOBIEDB_API_KEY);
        new TheMovieDBQueryTask().execute(url);
    }

    // Async Task for loading Movie Data
    public class TheMovieDBQueryTask extends AsyncTask<URL,Void,String> {

        /*
            Get a movie list json from themoviedb.com
         */
        @Override
        protected String doInBackground(URL... params) {
            URL queryUrl = params[0];
            String theMovieDBResults = null;
            try{
                theMovieDBResults = NetworkUtils.getResponseFromHttpUrl(queryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return theMovieDBResults;
        }

        /*
            After the list of movies is created, build a new MovieAdapter and set it as the
              RecyclerView Adapter
         */
        @Override
        protected void onPostExecute(String s) {
            if(s!=null && !s.equals("")){
                currentMovies = getMoviesList(s);
                mMovieAdapter = new MovieAdapter(NUM_MOVIE_ITEMS, currentMovies, main_activity);
                mMovies.setAdapter(mMovieAdapter);
            }
        }


        /*
            Converts a json string to a LinkedList of Movie Objects
         */
        private LinkedList<Movie> getMoviesList(String s){
            LinkedList<Movie> movies = new LinkedList<Movie>();
            try {
                JSONObject response = new JSONObject(s);
                JSONArray moviesJSON = response.getJSONArray("results");
                for (int i = 0; i < moviesJSON.length(); i++){
                    JSONObject movieJSON    = moviesJSON.getJSONObject(i);
                    String posterPath       = movieJSON.getString("poster_path");
                    String id               = movieJSON.getString("id");
                    String title            = movieJSON.getString("title");
                    String overview         = movieJSON.getString("overview");
                    String vote_average     = movieJSON.getString("vote_average");
                    String releaseDate      = movieJSON.getString("release_date");

                    Movie movie = new Movie(id, posterPath, title, overview, vote_average, releaseDate);
                    movies.add(movie);
                }

            } catch(JSONException e){
                e.printStackTrace();
            }

            return movies;
        }
    }
}
