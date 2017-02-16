package com.domenicoangilletta.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by domangi on 13/02/17.
 */

public class MovieContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.domenicoangilletta.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME           = "movies";

        public static final String COLUMN_POSTER_PATH   = "poster_path";
        public static final String COLUMN_TITLE         = "title";
        public static final String COLUMN_VOTE_AVERAGE  = "vote_average";
        public static final String COLUMN_OVERVIEW      = "overview";
        public static final String COLUMN_RELEASE_DATE  = "release_date";
        public static final String COLUMN_POSTER_BLOB   = "poster_blob";
        public static final String COLUMN_TRAILERS_JSON = "trailers_json";
        public static final String COLUMN_REVIEWS_JSON  = "reviews_json";

    }
}
