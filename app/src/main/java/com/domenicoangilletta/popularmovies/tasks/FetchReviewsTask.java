package com.domenicoangilletta.popularmovies.tasks;

import android.os.AsyncTask;

import com.domenicoangilletta.popularmovies.activities.MovieDetailActivity;
import com.domenicoangilletta.popularmovies.models.Movie;
import com.domenicoangilletta.popularmovies.models.Review;
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
public class FetchReviewsTask extends AsyncTask<URL,Void,String> {
    private LinkedList<Review> reviews;
    private MovieDetailActivity activity;
    private Movie selectedMovie;

    public FetchReviewsTask(LinkedList<Review> reviews, Movie movie, MovieDetailActivity activity){
        this.reviews = reviews;
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
            reviews = getReviewList(s);

            selectedMovie.setReviews(reviews);
            activity.renderReviews();
        }
    }


    /*
        Converts a json string to a LinkedList of Movie Objects
     */
    private LinkedList<Review> getReviewList(String s){
        LinkedList<Review> reviews = new LinkedList<Review>();
        try {
            JSONObject response = new JSONObject(s);
            JSONArray moviesJSON = response.getJSONArray("results");
            for (int i = 0; i < moviesJSON.length(); i++){
                JSONObject movieJSON    = moviesJSON.getJSONObject(i);
                String author               = movieJSON.getString("author").trim();
                String content               = movieJSON.getString("content").trim();

                Review review = new Review(author, content);
                reviews.add(review);
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        return reviews;
    }
}