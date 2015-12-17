package com.example.kush.cinemax.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.kush.cinemax.BuildConfig;
import com.example.kush.cinemax.R;
import com.example.kush.cinemax.fragments.PopularFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Kush on 27-11-2016.
 */

public class PopularAdapter extends CursorAdapter
{


    public PopularAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.i_grid, parent, false);


    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView posterView = (ImageView) view.findViewById(R.id.grid_item_movie_image);
        String POSTER_S_L = "w342";

        //int moviePosterColumn = cursor.getColumnIndex(MoviesContract.PopularEntry.COLUMN_MOVIE_POSTER_PATH);
        String posterPath = cursor.getString(PopularFragment.COL_MOVIE_POSTER_PATH);

        final String POSTER_FINAL_URL = BuildConfig.MOVIEDB_API_IMAGE__URL + POSTER_S_L + posterPath;

        Picasso.with(context).load(POSTER_FINAL_URL.trim()).into(posterView);

    }
}
