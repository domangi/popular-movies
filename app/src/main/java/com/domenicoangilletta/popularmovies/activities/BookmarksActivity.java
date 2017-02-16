package com.domenicoangilletta.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.domenicoangilletta.popularmovies.R;
import com.domenicoangilletta.popularmovies.adapters.BookmarkedMoviesCursorAdapter;
import com.domenicoangilletta.popularmovies.data.MovieContract;

public class BookmarksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        BookmarkedMoviesCursorAdapter.BookmarkedMovieItemClickListener  {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BookmarkedMoviesCursorAdapter mCursorAdapter;
    private final static int MOVIE_LOADER_ID=1;
    private BookmarksActivity activity;
    private RecyclerView mMovies;
    private final static int NUM_COLS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        activity = this;
        mMovies = (RecyclerView) findViewById(R.id.rv_bookmarked_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLS);
        mMovies.setLayoutManager(layoutManager);
        mMovies.setHasFixedSize(true);
        loadMovies();
    }

    private void loadMovies(){
        // LOAD MOVIES FROM DB
        mCursorAdapter = new BookmarkedMoviesCursorAdapter(activity, this);
        mMovies.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public void onBookmarkedMovieItemClick(int clickedItemIndex, String movie_id) {
        Context context = BookmarksActivity.this;
        Class destinationActivity = BookmarkedMovieDetailActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(Intent.EXTRA_INDEX, ""+movie_id);

        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }
}
