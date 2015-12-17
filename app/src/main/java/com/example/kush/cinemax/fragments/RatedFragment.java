package com.example.kush.cinemax.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.kush.cinemax.DetailActivity;
import com.example.kush.cinemax.R;
import com.example.kush.cinemax.adapters.RatedAdapter;
import com.example.kush.cinemax.data.MoviesContract;

/**
 * Created by Kush on 27-11-2016.
 */

public class RatedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = RatedFragment.class.getSimpleName();
    public static final int MOVIE_LOADER = 0;
    private RatedAdapter ratedAdapter;
    private static final String[] MOVIES_COLUMNS = {

            MoviesContract.RatedEntry._ID,
            MoviesContract.RatedEntry.COLUMN_MOVIE_ID,
            MoviesContract.RatedEntry.COLUMN_MOVIE_POSTER_PATH,

    };

    static final int COL_ID = 0;
    static final int COL_MOVIEID = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;

    public RatedFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_rated, container, false);
        ratedAdapter = new RatedAdapter(getActivity(), null, 0);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);

        gridView.setAdapter(ratedAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Cursor currentData = (Cursor) parent.getItemAtPosition(position);


                if (currentData != null) {

                    Intent detailsIntent = new Intent(getActivity(), DetailActivity.class);
                    String m_id= currentData.getString(COL_MOVIEID);
                    detailsIntent.putExtra("movie_id",m_id);
                    startActivity(detailsIntent);
                    Log.d(LOG_TAG, "Movie id clicked is ::- " + m_id+ "");
                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        return new CursorLoader(getActivity(),
                MoviesContract.RatedEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                null,
                null,
                null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "Cursor loaded, " + data.getCount() + " rows fetched");
        ratedAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ratedAdapter.swapCursor(null);
    }
}


