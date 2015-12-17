package com.example.kush.cinemax.network_sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.example.kush.cinemax.BuildConfig;
import com.example.kush.cinemax.R;
import com.example.kush.cinemax.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Kush on 05-11-2016.
 */

public class Movie_SyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = Movie_SyncAdapter.class.getSimpleName();

    public Movie_SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.cinemax), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {


            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void getPopularDataFromJson()
            throws JSONException {

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();
        Log.d(LOG_TAG, "Starting sync popularity");
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;
        try {
            Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_API_POPULAR_URL).buildUpon()
                    .appendQueryParameter(FinalVariables.MOVIEDB_API_KEY, BuildConfig.MOVIEDB_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Long LastModified = urlConnection.getLastModified();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesJsonStr = buffer.toString();

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(FinalVariables.MOVIEDB_RESULTS);

            // Insert the new information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());
            try {
                for (int i = 0; i < moviesArray.length(); ++i) {
                    long dateTime;
                    dateTime = dayTime.setJulianDay(julianStartDay + i);
                    JSONObject movieObject = moviesArray.getJSONObject(i);

                    ContentValues moviesValues = new ContentValues();

                    moviesValues.put(MoviesContract.PopularEntry.COLUMN_MOVIE_ID,
                            movieObject.getString(FinalVariables.MOVIEDB_ID));
                    moviesValues.put(MoviesContract.PopularEntry.COLUMN_MOVIE_POSTER_PATH,
                            movieObject.getString(FinalVariables.MOVIEDB_POSTER_PATH));
                    moviesValues.put(MoviesContract.PopularEntry.COLUMN_DATE, dateTime);

                    cVVector.add(moviesValues);
                }

                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MoviesContract.PopularEntry.CONTENT_URI, cvArray);
                    Log.d(LOG_TAG, "Bulk Inserted Data popularity, " + cvArray.length + " rows fetched");

                    // delete old data so we don't build up an endless history
                    getContext().getContentResolver().delete(MoviesContract.PopularEntry.CONTENT_URI,
                            MoviesContract.PopularEntry.COLUMN_DATE + " <= ?",
                            new String[]{Long.toString(dayTime.setJulianDay(julianStartDay - 1))});
                    Log.d(LOG_TAG, "Bulk Deleted Data, Popularity " + cvArray.length + " rows fetched");

                }
                Log.d(LOG_TAG, "Sync Complete. popularity ");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    private void getRatedDataFromJson()
            throws JSONException {

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();
        Log.d(LOG_TAG, "Starting sync Rated");
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;
        try {
            Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_API_RATED_URL).buildUpon()
                    .appendQueryParameter(FinalVariables.MOVIEDB_API_KEY, BuildConfig.MOVIEDB_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesJsonStr = buffer.toString();

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(FinalVariables.MOVIEDB_RESULTS);

            // Insert the new information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());
            try {
                for (int i = 0; i < moviesArray.length(); ++i) {
                    long dateTime;
                    dateTime = dayTime.setJulianDay(julianStartDay + i);
                    JSONObject movieObject = moviesArray.getJSONObject(i);

                    ContentValues moviesValues = new ContentValues();

                    moviesValues.put(MoviesContract.RatedEntry.COLUMN_MOVIE_ID,
                            movieObject.getString(FinalVariables.MOVIEDB_ID));
                    moviesValues.put(MoviesContract.RatedEntry.COLUMN_MOVIE_POSTER_PATH,
                            movieObject.getString(FinalVariables.MOVIEDB_POSTER_PATH));
                    moviesValues.put(MoviesContract.RatedEntry.COLUMN_DATE, dateTime);

                    cVVector.add(moviesValues);
                }

                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MoviesContract.RatedEntry.CONTENT_URI, cvArray);
                    Log.d(LOG_TAG, "Bulk Inserted Data Rated, " + cvArray.length + " rows fetched");

                    // delete old data so we don't build up an endless history
                    getContext().getContentResolver().delete(MoviesContract.RatedEntry.CONTENT_URI,
                            MoviesContract.RatedEntry.COLUMN_DATE + " <= ?",
                            new String[]{Long.toString(dayTime.setJulianDay(julianStartDay - 1))});
                    Log.d(LOG_TAG, "Bulk Deleted Data, Rated " + cvArray.length + " rows fetched");

                }
                Log.d(LOG_TAG, "Sync Complete. Rated ");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    private void getDetailsDataFromJson() {
        Log.d(LOG_TAG, "Starting sync details");
        int counter = 0;

        List<String> fileName = new ArrayList<>();
        List<String> fileName1 = new ArrayList<>();
        Cursor cursor = getContext().getContentResolver().query(
                MoviesContract.PopularEntry.CONTENT_URI,
                new String[]{MoviesContract.PopularEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {

            fileName.add(cursor.getString(cursor.getColumnIndex(MoviesContract.PopularEntry.COLUMN_MOVIE_ID)));

            while (cursor.moveToNext()) {

                fileName.add(cursor.getString(cursor.getColumnIndex(MoviesContract.PopularEntry.COLUMN_MOVIE_ID)));
            }
            cursor.close();
        }

        Cursor cursor2 = getContext().getContentResolver().query(
                MoviesContract.RatedEntry.CONTENT_URI,
                new String[]{MoviesContract.RatedEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null);

        if (cursor2 != null && cursor2.moveToFirst()) {

            fileName1.add(cursor2.getString(cursor2.getColumnIndex(MoviesContract.RatedEntry.COLUMN_MOVIE_ID)));

            while (cursor2.moveToNext()) {

                fileName1.add(cursor2.getString(cursor2.getColumnIndex(MoviesContract.RatedEntry.COLUMN_MOVIE_ID)));
            }
            cursor2.close();
        }


        fileName1.addAll(fileName);

        ArrayList<String> comboArrayList = new ArrayList<String>(fileName);
        comboArrayList.addAll(fileName1);

        Set<String> setList = new LinkedHashSet<String>(comboArrayList);
        comboArrayList.clear();
        comboArrayList.addAll(setList);

        List<String> fileName3 = new ArrayList<>();

        List list = new ArrayList(setList);

        for (Object f :list) {
            fileName3.add((String) f);
        }
        //Collections.sort(comboArrayList); for sort
        // Insert the new information into the database
        //all movies id that are ready to be inserted in details database
        for (String s : setList) {

            Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_API_RATED_URL + "/" + s).buildUpon()
                    .appendQueryParameter(FinalVariables.MOVIEDB_API_KEY, BuildConfig.MOVIEDB_API_KEY)
                    .build();

            ContentValues moviesValues = new ContentValues();
            moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID, s);
            getContext().getContentResolver().insert(MoviesContract.MoviesDetailsEntry.CONTENT_URI,moviesValues);

            Log.d(LOG_TAG, "Movie id inserting " + s + "---" + "Counter:" + counter);

        }
        counter++;
        Log.d(LOG_TAG, "sync completd details");

        if (counter != 0) {

            try {

                getDataFromJson(fileName3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getDataFromJson(List<String> setlist)
            throws JSONException {

        Log.d(LOG_TAG, "Starting sync Main Details");
        //setlist to arraylist conversion

        for (String k : setlist) {
            Log.d(LOG_TAG, "Elements are" + ":" + k);
        }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            try {
                for (String s : setlist) {
                Log.d(LOG_TAG, "Starting sync Main Details" + ":" + s);
                Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_API_DETAILS_URL + s + "?").buildUpon()
                        .appendQueryParameter(FinalVariables.MOVIEDB_API_KEY, BuildConfig.MOVIEDB_API_KEY)
                        .appendQueryParameter(FinalVariables.MOVIEDB_APPEND, FinalVariables.MOVIEDB_VIDEOS_REVIEWS)
                        .build();
                URL url = new URL(builtUri.toString());

                // Create the request to TheMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                moviesJsonStr = buffer.toString();

                try {

                    JSONObject moviesJson = new JSONObject(moviesJsonStr);

                    JSONObject reviewsObject = moviesJson.getJSONObject(FinalVariables.MOVIEDB_REVIEWS);
                    JSONObject VideosObject = moviesJson.getJSONObject(FinalVariables.MOVIEDB_VIDEOS);
                    JSONArray reviewsResultsArray = reviewsObject.getJSONArray(FinalVariables.MOVIEDB_RESULTS);
                    JSONArray videosResultsArray = VideosObject.getJSONArray(FinalVariables.MOVIEDB_RESULTS);


                    ContentValues moviesValues = new ContentValues();
                    String o_title = moviesJson.getString(FinalVariables.MOVIEDB_ORIGINAL_TITLE);
                    String v_avg = moviesJson.getString(FinalVariables.MOVIEDB_VOTE_AVERAGE);
                    String backdrop_path = moviesJson.getString(FinalVariables.MOVIEDB_BACKDROP_PATH);
                    String poster_path = moviesJson.getString(FinalVariables.MOVIEDB_POSTER_PATH);
                    String overview = moviesJson.getString(FinalVariables.MOVIEDB_OVERVIEW);
                    String release_date = moviesJson.getString(FinalVariables.MOVIEDB_RELEASE_DATE);
                    String runtime = moviesJson.getString(FinalVariables.MOVIEDB_RUNTIME);

                    String trailer1_key = "", trailer2_key = "", trailer3_key = "", trailer4_key = "";
                    String trailer1_name = "", trailer2_name = "", trailer3_name = "", trailer4_name = "";
                    String review1_content = "", review2_content = "", review3_content = "", review4_content = "";
                    String review1_name = "", review2_name = "", review3_name = "", review4_name = "";

                    for (int i = 0; i < reviewsResultsArray.length(); i++) {
                        // Get the JSON object for this review in the list
                        JSONObject review = reviewsResultsArray.getJSONObject(i);
                        String name = review.getString(FinalVariables.MOVIEDB_REVIEWS_AUTHOR);
                        String content = review.getString(FinalVariables.MOVIEDB_REVIEWS_CONTENT);

                        if (i == 0) {
                            review1_content = content;
                            review1_name = name;
                        } else if (i == 1) {
                            review2_content = content;
                            review2_name = name;
                        } else if (i == 2) {
                            review3_content = content;
                            review3_name = name;
                        } else if (i == 3) {
                            review4_content = content;
                            review4_name = name;
                        }
                    }

                    //videos

                    for (int i = 0; i < videosResultsArray.length(); i++) {
                        // Get the JSON object for this review in the list
                        JSONObject video = videosResultsArray.getJSONObject(i);
                        String name = video.getString(FinalVariables.MOVIEDB_TRAILERS_NAME);
                        String key = video.getString(FinalVariables.MOVIEDB_TRAILERS_KEY);

                        if (i == 0) {
                            trailer1_key = key;
                            trailer1_name = name;
                        } else if (i == 1) {
                            trailer2_key = key;
                            trailer2_name = name;
                        } else if (i == 2) {
                            trailer3_key = key;
                            trailer3_name = name;
                        } else if (i == 3) {
                            trailer4_key = key;
                            trailer4_name = name;
                        }
                    }
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_TITLE, o_title);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_USER_RATING, v_avg);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_BACKDROP_PATH,backdrop_path);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_POSTER_PATH,poster_path);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_OVERVIEW,overview);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_RELEASE_DATE,release_date);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_RUNTIME,runtime);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR1, review1_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR2, review2_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR3, review3_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR4, review4_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT1, review1_content);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT2, review2_content);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT3, review3_content);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT4, review4_content);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE1, trailer1_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE2, trailer2_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE3, trailer3_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE4, trailer4_name);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY1, trailer1_key);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY2, trailer2_key);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY3, trailer3_key);
                    moviesValues.put(MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY4, trailer4_key);

                    long id = Long.parseLong(s);

                    getContext().getContentResolver().update(MoviesContract.MoviesDetailsEntry.CONTENT_URI,
                            moviesValues,
                            MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID + " = " + String.valueOf(id),
                            null);

                    Log.d(LOG_TAG, "Data Enter " + s + ":" + review1_name);
                    Log.d(LOG_TAG, "Data Enter Title " + s + ":" + o_title);
                    Log.d(LOG_TAG, "Sync Complete. Main Details ");

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

            }
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return;


    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            getPopularDataFromJson();
            getRatedDataFromJson();
            getDetailsDataFromJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}