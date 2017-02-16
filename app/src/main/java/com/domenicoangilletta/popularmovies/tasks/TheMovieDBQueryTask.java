package com.domenicoangilletta.popularmovies.tasks;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.domenicoangilletta.popularmovies.activities.MainActivity;
import com.domenicoangilletta.popularmovies.models.Movie;
import com.domenicoangilletta.popularmovies.adapters.MovieAdapter;
import com.domenicoangilletta.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by domangi on 13/02/17.
 */

// Async Task for loading Movie Data
public class TheMovieDBQueryTask extends AsyncTask<URL,Void,String> {

    private RecyclerView mMoviesRV;
    private LinkedList<Movie> movies;
    private MainActivity main_activity;
    private MovieAdapter mMovieAdapter;

    public TheMovieDBQueryTask(RecyclerView rv, LinkedList<Movie> movies, MainActivity activity, MovieAdapter adapter){
        this.mMoviesRV = rv;
        this.movies = movies;
        this.main_activity = activity;
        this.mMovieAdapter = adapter;
    }

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
            movies = getMoviesList(s);
            main_activity.setCurrentMovies(movies);
            int count = movies.size();
            mMovieAdapter = new MovieAdapter(count, movies, main_activity);
            mMoviesRV.setAdapter(mMovieAdapter);
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

                Movie movie = new Movie(Integer.parseInt(id), posterPath, title, overview, vote_average, releaseDate);
                movies.add(movie);
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        return movies;
    }
}
