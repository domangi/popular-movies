package com.domenicoangilletta.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.domenicoangilletta.popularmovies.data.MovieContract;
import com.domenicoangilletta.popularmovies.utils.DbBitmapUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by domenicoangilletta on 28/01/17.
 *
 * Describes a movie as returned by themoviedb.com
 *
 */

public class Movie {
    // TODO: make variables private
    public String posterPath;
    public String title;
    public String voteAverage;
    public String overview;
    public int id;
    public String releaseDate;
    public boolean isBookmarked;
    private Bitmap poster;
    private LinkedList<Trailer> trailers;
    private LinkedList<Review> reviews;

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    public Movie(){

    }
    /*
        build a new Movie passing all necessary attributes
     */
    public Movie(int id, String posterPath, String title, String overview, String voteAverage, String releaseDate){
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.isBookmarked = false;
    }

    public Movie(int id, String posterPath, String title, String overview, String voteAverage, String releaseDate, boolean isBookmarked){
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.isBookmarked = isBookmarked;
    }

    public Movie(Cursor dbRow){
        int idIndex =  dbRow.getColumnIndex(MovieContract.MovieEntry._ID);
        int posterPathIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        int titleIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int voteAverageIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int overviewIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int posterIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_BLOB);
        int trailersIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILERS_JSON);
        int reviewsIndex = dbRow.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEWS_JSON);

        Log.d("ID INDEX", ""+idIndex);

        dbRow.moveToFirst();

        this.id = dbRow.getInt(idIndex);
        this.posterPath = dbRow.getString(posterPathIndex);
        this.title = dbRow.getString(titleIndex);
        this.voteAverage = dbRow.getString(voteAverageIndex);
        this.overview = dbRow.getString(overviewIndex);
        this.releaseDate = dbRow.getString(releaseDateIndex);
        this.poster = DbBitmapUtility.getImage(dbRow.getBlob(posterIndex));
        this.trailers = new LinkedList<Trailer>();
        try {
            JSONArray trailers_json = new JSONArray(dbRow.getString(trailersIndex));
            for(int i = 0; i < trailers_json.length(); i++){
                JSONObject trailer_json = trailers_json.getJSONObject(i);

                this.trailers.add(new Trailer(
                        trailer_json.getString("id"),
                        trailer_json.getString("name"),
                        trailer_json.getString("key"),
                        trailer_json.getString("site"),
                        trailer_json.getString("type")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.reviews = new LinkedList<Review>();
        String reviews_json_string = dbRow.getString(reviewsIndex);
        if(reviews_json_string!=null){
            try {
                JSONArray reviews_json = new JSONArray(dbRow.getString(reviewsIndex));
                for(int i = 0; i < reviews_json.length(); i++){
                    JSONObject trailer_json = reviews_json.getJSONObject(i);

                    this.reviews.add(new Review(
                            trailer_json.getString("author"),
                            trailer_json.getString("content")
                    ));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    /*
        Returns the url to the movie poster for the given format
     */
    public String getThumbUrl(String format){
        return BASE_IMAGE_URL + "w" + format + posterPath;
    }

    public String getPosterUrl(){ return getThumbUrl("780");}


    public void setTrailers(LinkedList<Trailer> trailers){
        this.trailers = trailers;
    }

    public void setReviews(LinkedList<Review> reviews){
        this.reviews = reviews;
    }

    public void setPoster(Bitmap poster){
        this.poster = poster;
    }

    public Bitmap getPoster(){
        return poster;
    }

    public byte[] getPosterBlob(){
        Bitmap poster = getPoster();
        byte[] poster_blob = DbBitmapUtility.getBytes(poster);
        return poster_blob;
    }

    public LinkedList<Trailer> getTrailers(){
        return trailers;
    }

    public LinkedList<Review> getReviews(){
        return reviews;
    }

    public String getTrailersJson(){
        JSONArray trailers_json = new JSONArray();
        for(Trailer trailer:trailers){
            try {
                JSONObject json = new JSONObject();
                json.accumulate("id", trailer.getId());
                json.accumulate("key" , trailer.getKey());
                json.accumulate("name", trailer.getName());
                json.accumulate("site", trailer.getSite());
                json.accumulate("type", trailer.getType());

                trailers_json.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return trailers_json.toString();
    }

    public String getReviewsJson(){
        JSONArray reviews_json = new JSONArray();
        for(Review review:reviews){
            try {
                JSONObject json = new JSONObject();
                json.accumulate("author", review.getAuthor());
                json.accumulate("content" , review.getContent());

                reviews_json.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return reviews_json.toString();
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put("_ID", id);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

        // TODO: SAVE POSTER, TRAILERS AND REVIEWS TO DATABASE
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_BLOB, getPosterBlob() );
        values.put(MovieContract.MovieEntry.COLUMN_TRAILERS_JSON, getTrailersJson());
        values.put(MovieContract.MovieEntry.COLUMN_REVIEWS_JSON, getReviewsJson());

        return values;
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
            movie_json.put("posterPath", posterPath);
            movie_json.put("releaseDate", releaseDate);
            movie_json.put("isBookmarked", isBookmarked);
            json_string = movie_json.toString();
        } catch (JSONException e){
            e.printStackTrace();
        }

        return json_string;
    }
}
