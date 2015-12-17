package com.example.kush.cinemax.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MoviesDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + MoviesContract.PopularEntry.TABLE_NAME + " (" +
                MoviesContract.PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.PopularEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                MoviesContract.PopularEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.PopularEntry.COLUMN_DATE + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE "
                + " );";

        final String SQL_CREATE_RATED_TABLE = "CREATE TABLE " + MoviesContract.RatedEntry.TABLE_NAME + " (" +
                MoviesContract.RatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.RatedEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                MoviesContract.RatedEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.RatedEntry.COLUMN_DATE + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE "
                + " );";

        final String SQL_CREATE_MOVIES_DETAILS_TABLE = "CREATE TABLE " + MoviesContract.MoviesDetailsEntry.TABLE_NAME + " (" +

                MoviesContract.PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL UNIQUE ON CONFLICT IGNORE, " +

                //details
                MoviesContract.MoviesDetailsEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_USER_RATING + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_RUNTIME + " TEXT, " +

                //reviews
                MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR1 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR2 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR3 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR4 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT1 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT2 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT3 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT4 + " TEXT, " +

                //trailers
                MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE1 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE2 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE3 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE4 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY1 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY2 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY3 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY4 + " TEXT, " +
                MoviesContract.MoviesDetailsEntry.COLUMN_FAV_FLAG + " INTEGER DEFAULT 0 " +
                " );";

        db.execSQL(SQL_CREATE_POPULAR_TABLE);
        db.execSQL(SQL_CREATE_RATED_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_DETAILS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.PopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.RatedEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesDetailsEntry.TABLE_NAME);

        onCreate(db);
    }
}
