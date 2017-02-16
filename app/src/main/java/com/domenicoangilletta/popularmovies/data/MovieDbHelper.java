package com.domenicoangilletta.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.domenicoangilletta.popularmovies.data.MovieContract.*;

/**
 * Created by domangi on 13/02/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME ="moviesDb.db";
    private static final int VERSION = 1;

    MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE= "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                    + " INTEGER PRIMARY KEY, " + // NOT AUTOINCREMENT BECAUSE SETTED BY themovie.db
                MovieEntry.COLUMN_TITLE           + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW        + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH     + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE    + " FLOAT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE    + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_BLOB     + " BLOB, " +
                MovieEntry.COLUMN_TRAILERS_JSON   + " TEXT, " +
                MovieEntry.COLUMN_REVIEWS_JSON    + " TEXT);";

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
