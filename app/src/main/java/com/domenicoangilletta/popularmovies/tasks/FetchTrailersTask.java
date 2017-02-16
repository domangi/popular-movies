package com.domenicoangilletta.popularmovies.tasks;

import android.os.AsyncTask;

import com.domenicoangilletta.popularmovies.activities.MovieDetailActivity;
import com.domenicoangilletta.popularmovies.models.Movie;
import com.domenicoangilletta.popularmovies.models.Trailer;
import com.domenicoangilletta.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by domangi on 15/02/17.
 */

// Async Task for loading Movie Data
public class FetchTrailersTask extends AsyncTask<URL,Void,String> {
    private LinkedList<Trailer> trailers;
    private Movie selectedMovie;
    private MovieDetailActivity activity;

    public FetchTrailersTask(LinkedList<Trailer> trailers, Movie movie, MovieDetailActivity activity){
        this.trailers = trailers;
        this.selectedMovie = movie;
        this.activity = activity;
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
            trailers = getTrailerList(s);
            selectedMovie.setTrailers(trailers);

            activity.renderTrailers();
        }
    }


    /*
        Converts a json string to a LinkedList of Movie Objects
     */
    private LinkedList<Trailer> getTrailerList(String s){
        LinkedList<Trailer> trailers = new LinkedList<Trailer>();
        try {
            JSONObject response = new JSONObject(s);
            JSONArray moviesJSON = response.getJSONArray("results");
            for (int i = 0; i < moviesJSON.length(); i++){
                JSONObject movieJSON    = moviesJSON.getJSONObject(i);
                String id               = movieJSON.getString("id");
                String name               = movieJSON.getString("name");
                String key               = movieJSON.getString("key");
                String type               = movieJSON.getString("type");
                String site               = movieJSON.getString("site");

                Trailer trailer = new Trailer(id, name, key, site, type);
                trailers.add(trailer);
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        return trailers;
    }
}