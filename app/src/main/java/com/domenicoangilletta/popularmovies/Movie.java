package com.domenicoangilletta.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by domenicoangilletta on 28/01/17.
 *
 * Describes a movie as returned by themoviedb.com
 *
 */

class Movie {
    private String posterPath;
    private String title;
    private String voteAverage;
    private String overview;
    private String id;
    private String releaseDate;

        private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    /*
        build a new Movie passing all necessary attributes
     */
    public Movie(String id, String posterPath, String title, String overview, String voteAverage, String releaseDate){
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    /*
        Returns the url to the movie poster for the given format
     */
    public String getThumbUrl(String format){
        return BASE_IMAGE_URL + "w" + format + posterPath;
    }

    /*
        Converts the movie instance to a json object and returns it stringified
     */
    public String toJson(){
        String json_string = "";
        try {
            JSONObject movie_json = new JSONObject();
            movie_json.put("id", id);
            movie_json.put("title", title);
            movie_json.put("overview", overview);
            movie_json.put("voteAverage", voteAverage);
            movie_json.put("poster_url", getThumbUrl("780"));
            movie_json.put("releaseDate", releaseDate);
            json_string = movie_json.toString();
        } catch (JSONException e){
            e.printStackTrace();
        }

        return json_string;
    }
}
