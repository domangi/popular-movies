package com.domenicoangilletta.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by domangi on 13/02/17.
 */

public class MovieContentProvider extends ContentProvider {

    // DEFINE INT CONSTANTS AS PATH IDS FOR ROUTES
    public static final int MOVIES=100;
    public static final int MOVIE_WITH_ID=101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper mMovieDbHelper;

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){
            case MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[] {id};
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri for query: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri retUri;

        switch (match){
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        values);
                if(id != -1){
                    retUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri for insert: " + uri);
        }

        if(retUri!=null)
            getContext().getContentResolver().notifyChange(uri, null);

        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedRows = 0;

        switch (match){
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[] {id};
                deletedRows = db.delete(MovieContract.MovieEntry.TABLE_NAME,mSelection,mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri for delete: " + uri);
        }

        if(deletedRows>0)
            getContext().getContentResolver().notifyChange(uri, null);

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int updatedRows = 0;

        switch (match){
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[] {id};
                updatedRows = db.update(MovieContract.MovieEntry.TABLE_NAME, values, mSelection, mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri for update: " + uri);
        }
        if(updatedRows>0)
            getContext().getContentResolver().notifyChange(uri, null);

        return updatedRows;
    }
}
