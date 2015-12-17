package com.example.kush.cinemax.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;



public class MoviesProvider extends ContentProvider {

    static final int DETAILS = 100;
    static final int POPULAR = 101;
    static final int RATED = 102;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDatabaseHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.

        matcher.addURI(authority, MoviesContract.PATH_MOVIES_DETAILS, DETAILS);
        matcher.addURI(authority, MoviesContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_RATED, RATED);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DETAILS:
                return MoviesContract.MoviesDetailsEntry.CONTENT_TYPE;
            case POPULAR:
                return MoviesContract.PopularEntry.CONTENT_TYPE;
            case RATED:
                return MoviesContract.RatedEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case DETAILS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesDetailsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.PopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RATED: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.RatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case DETAILS: {
                long id = db.insertWithOnConflict(MoviesContract.MoviesDetailsEntry.TABLE_NAME,
                        null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = MoviesContract.MoviesDetailsEntry.buildMovieDetailsUri(id);
                } else if (id == -1) {
                    returnUri = MoviesContract.MoviesDetailsEntry.buildMovieDetailsUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (match) {
            case DETAILS:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesDetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR:
                rowsDeleted = db.delete(
                        MoviesContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RATED:
                rowsDeleted = db.delete(
                        MoviesContract.RatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DETAILS:
                rowsUpdated = db.update(MoviesContract.MoviesDetailsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DETAILS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesDetailsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case POPULAR:
                db.beginTransaction();
                int returnCountp = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.PopularEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountp++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountp;
            case RATED:
                db.beginTransaction();
                int returnCountr = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.RatedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountr++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountr;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
