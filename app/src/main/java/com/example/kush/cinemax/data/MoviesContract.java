package com.example.kush.cinemax.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;



public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.kush.cinemax";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES_DETAILS = "movies_details";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_RATED = "rated";

    public static final class MoviesDetailsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_DETAILS).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_DETAILS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_DETAILS;


        public static final String TABLE_NAME = "m_details";

        // Movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //details
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releasedate";
        public static final String COLUMN_RUNTIME = "runtime";

        //reviews
        public static final String COLUMN_AUTHOR1 = "author1";
        public static final String COLUMN_AUTHOR2 = "author2";
        public static final String COLUMN_AUTHOR3 = "author3";
        public static final String COLUMN_AUTHOR4 = "author4";
        public static final String COLUMN_CONTENT1 = "content1";
        public static final String COLUMN_CONTENT2 = "content2";
        public static final String COLUMN_CONTENT3 = "content3";
        public static final String COLUMN_CONTENT4 = "content4";

        //trailers
        public static final String COLUMN_TRAILER_TITLE1 = "trailer_title1";
        public static final String COLUMN_TRAILER_TITLE2 = "trailer_title2";
        public static final String COLUMN_TRAILER_TITLE3 = "trailer_title3";
        public static final String COLUMN_TRAILER_TITLE4 = "trailer_title4";
        public static final String COLUMN_YOUTUBE_KEY1 = "youtube_key1";
        public static final String COLUMN_YOUTUBE_KEY2 = "youtube_key2";
        public static final String COLUMN_YOUTUBE_KEY3 = "youtube_key3";
        public static final String COLUMN_YOUTUBE_KEY4 = "youtube_key4";

        public static final String COLUMN_FAV_FLAG = "fav_flag";


        public static Uri buildMovieDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;


        public static final String TABLE_NAME = "m_popular";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";

        //date for non-repeat
        public static final String COLUMN_DATE = "date";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class RatedEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATED;


        public static final String TABLE_NAME = "m_rated";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";

        //date for non-repeat
        public static final String COLUMN_DATE = "date";


        public static Uri buildRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
