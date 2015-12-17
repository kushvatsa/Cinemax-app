package com.example.kush.cinemax.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kush.cinemax.BuildConfig;
import com.example.kush.cinemax.R;
import com.example.kush.cinemax.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public static final int DETAILS_LOADER= 0;
    private ShareActionProvider mShareActionProvider;
    int ff = 0;
    String Key1;
    String Key2;
    String Key3;
    String Key4;

    private static final String[] MOVIES_DETAILS_COLUMNS = {

            MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesDetailsEntry.COLUMN_TITLE,
            MoviesContract.MoviesDetailsEntry.COLUMN_USER_RATING,
            MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_BACKDROP_PATH,
            MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_POSTER_PATH,
            MoviesContract.MoviesDetailsEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesDetailsEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesDetailsEntry.COLUMN_RUNTIME,

            MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR1,
            MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR2,
            MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR3,
            MoviesContract.MoviesDetailsEntry.COLUMN_AUTHOR4,

            MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT1,
            MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT2,
            MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT3,
            MoviesContract.MoviesDetailsEntry.COLUMN_CONTENT4,

            MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE1,
            MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE2,
            MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE3,
            MoviesContract.MoviesDetailsEntry.COLUMN_TRAILER_TITLE4,

            MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY1,
            MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY2,
            MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY3,
            MoviesContract.MoviesDetailsEntry.COLUMN_YOUTUBE_KEY4,
            MoviesContract.MoviesDetailsEntry.COLUMN_FAV_FLAG
    };

    static final int COL_MOVIEID = 0;
    static final int COL_TITLE = 1;
    static final int COL_USER_RATING = 2;
    static final int COL_MOVIE_BACKDROP_PATH = 3;
    static final int COL_MOVIE_POSTER_PATH = 4;
    static final int COL_OVERVIEW = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_RUNTIME = 7;

    static final int COL_AUTHOR1 = 8;
    static final int COL_AUTHOR2 = 9;
    static final int COL_AUTHOR3 = 10;
    static final int COL_AUTHOR4 = 11;
    static final int COL_CONTENT1 = 12;
    static final int COL_CONTENT2 = 13;
    static final int COL_CONTENT3 = 14;
    static final int COL_CONTENT4 = 15;

    static final int COL_TRAILER_TITLE1= 16;
    static final int COL_TRAILER_TITLE2= 17;
    static final int COL_TRAILER_TITLE3= 18;
    static final int COL_TRAILER_TITLE4= 19;
    static final int COL_YOUTUBE_KEY1 = 20;
    static final int COL_YOUTUBE_KEY2 = 21;
    static final int COL_YOUTUBE_KEY3 = 22;
    static final int COL_YOUTUBE_KEY4 = 23;

    static final int COL_FAV_FLAG=24;

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView backdropImageView;
    ImageView posterImageView;
    TextView overviewTextView;
    TextView releaseDateTextView;
    TextView userRatingTextView;
    TextView runtimeView;

    CardView mReviewsCardview;
    CardView mTrailersCardview;

    ImageButton mPlay1;
    ImageButton mPlay2;
    ImageButton mPlay3;
    ImageButton mPlay4;
    TextView mTrailerName1;
    TextView mTrailerName2;
    TextView mTrailerName3;
    TextView mTrailerName4;

    TextView mAuthor1;
    TextView mAuthor2;
    TextView mAuthor3;
    TextView mAuthor4;
    TextView mContent1;
    TextView mContent2;
    TextView mContent3;
    TextView mContent4;

    private Toast mToast;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem action_favorite = menu.findItem(R.id.action_favorite);
        MenuItem action_share = menu.findItem(R.id.action_share);
        if(ff == 0) {
            action_favorite.setIcon(android.R.drawable.btn_star_big_off);
        }
        else {
            action_favorite.setIcon(android.R.drawable.btn_star_big_on);
        }

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);
        if(Key1!=null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,BuildConfig.MOVIEDB_API_YOUTUBE_TRAILER + Key1);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                Intent intent = getActivity().getIntent();
                String mId = intent.getStringExtra("movie_id");

                Log.d(LOG_TAG, "Before fav" + ff);
                if(ff == 0) {

                    ContentValues Values = new ContentValues();
                    Values.put(MoviesContract.MoviesDetailsEntry.COLUMN_FAV_FLAG, 1);
                    getContext().getContentResolver().update(MoviesContract.MoviesDetailsEntry.CONTENT_URI,
                            Values,
                            MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID + " = " + mId,
                            null);
                    Log.d(LOG_TAG, "Added Favorite");
                    item.setIcon(android.R.drawable.btn_star_big_on);
                    Log.d(LOG_TAG, "0 fav" + ff);
                    mToast = Toast.makeText(getActivity(), "Added to Favorite", Toast.LENGTH_SHORT);
                    mToast.show();
                }
                else
                {
                    ContentValues Values = new ContentValues();
                    Values.put(MoviesContract.MoviesDetailsEntry.COLUMN_FAV_FLAG, 0);
                    getContext().getContentResolver().update(MoviesContract.MoviesDetailsEntry.CONTENT_URI,
                            Values,
                            MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID + " = " + mId,
                            null);
                    Log.d(LOG_TAG, "Removed Favorite");
                    item.setIcon(android.R.drawable.btn_star_big_off);
                    Log.d(LOG_TAG, "1 fav" + ff);
                    mToast = Toast.makeText(getActivity(), "Removed from Favorite", Toast.LENGTH_SHORT);
                    mToast.show();
                }
                break;
            case android.R.id.home:
                // this takes the user 'back'
                getActivity().onBackPressed();
                return true;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.detail_collapsing_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);


        backdropImageView = (ImageView) rootView.findViewById(R.id.back_image_view);
        posterImageView = (ImageView) rootView.findViewById(R.id.detail_poster);
        overviewTextView = (TextView) rootView.findViewById(R.id.overview_text_view);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date);
        userRatingTextView = (TextView) rootView.findViewById(R.id.user_rating);
        runtimeView = (TextView) rootView.findViewById(R.id.runtime);

        mReviewsCardview = (CardView) rootView.findViewById(R.id.detail_reviews_cardview);
        mTrailersCardview = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);

        mPlay1 = (ImageButton) rootView.findViewById(R.id.movie_detail_trailer_play_button1);
        mPlay2 = (ImageButton) rootView.findViewById(R.id.movie_detail_trailer_play_button2);
        mPlay3 = (ImageButton) rootView.findViewById(R.id.movie_detail_trailer_play_button3);
        mPlay4 = (ImageButton) rootView.findViewById(R.id.movie_detail_trailer_play_button4);
        mTrailerName1 = (TextView) rootView.findViewById(R.id.movie_detail_trailer1);
        mTrailerName2 = (TextView) rootView.findViewById(R.id.movie_detail_trailer2);
        mTrailerName3 = (TextView) rootView.findViewById(R.id.movie_detail_trailer3);
        mTrailerName4 = (TextView) rootView.findViewById(R.id.movie_detail_trailer4);

        mPlay1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(BuildConfig.MOVIEDB_API_YOUTUBE_TRAILER + Key1));
                startActivity(intent);
            }
        });
        mPlay2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(BuildConfig.MOVIEDB_API_YOUTUBE_TRAILER + Key2));
                startActivity(intent);
            }
        });
        mPlay3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(BuildConfig.MOVIEDB_API_YOUTUBE_TRAILER + Key3));
                startActivity(intent);
            }
        });
        mPlay4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(BuildConfig.MOVIEDB_API_YOUTUBE_TRAILER + Key4));
                startActivity(intent);
            }
        });


        mAuthor1 = (TextView) rootView.findViewById(R.id.review_author1);
        mAuthor2 = (TextView) rootView.findViewById(R.id.review_author2);
        mAuthor3 = (TextView) rootView.findViewById(R.id.review_author3);
        mAuthor4 = (TextView) rootView.findViewById(R.id.review_author4);
        mContent1 = (TextView) rootView.findViewById(R.id.review_content1);
        mContent2 = (TextView) rootView.findViewById(R.id.review_content2);
        mContent3 = (TextView) rootView.findViewById(R.id.review_content3);
        mContent4 = (TextView) rootView.findViewById(R.id.review_content4);

        //mPlay1.setOnClickListener();

        Intent intent = getActivity().getIntent();
        String mId = intent.getStringExtra("movie_id");
        Log.d(LOG_TAG, "Movie id Detail is ::- " + mId+ "");
        Log.d(LOG_TAG, "CONTENT URI ::- " + MoviesContract.MoviesDetailsEntry.CONTENT_URI+ "");


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent();
        String movieId = intent.getStringExtra("movie_id");

        return new CursorLoader(getActivity(),
                MoviesContract.MoviesDetailsEntry.CONTENT_URI,
                MOVIES_DETAILS_COLUMNS,
                MoviesContract.MoviesDetailsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{ movieId },
                null);


    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = getContext();
        Log.d(LOG_TAG, "Cursor loaded, " + data.getCount() + " rows fetched");
        if (data != null && data.moveToFirst()) {
            Log.d(LOG_TAG, "Author Name, " + data.getString(COL_AUTHOR1) + " ");
            Log.d(LOG_TAG, "Fav Flag, " + data.getInt(COL_FAV_FLAG) + " ");
            ff = data.getInt(COL_FAV_FLAG);
            String title = data.getString(COL_TITLE);
            String UserRating = data.getString(COL_USER_RATING);
            String backdrop_path = data.getString(COL_MOVIE_BACKDROP_PATH);
            String poster_path = data.getString(COL_MOVIE_POSTER_PATH);
            String overview = data.getString(COL_OVERVIEW);
            String release_date = data.getString(COL_RELEASE_DATE);
            String runtime = data.getString(COL_RUNTIME);

            String Title1 = data.getString(COL_TRAILER_TITLE1);
            String Title2 = data.getString(COL_TRAILER_TITLE2);
            String Title3 = data.getString(COL_TRAILER_TITLE3);
            String Title4 = data.getString(COL_TRAILER_TITLE4);
            Key1 = data.getString(COL_YOUTUBE_KEY1);
            Key2 = data.getString(COL_YOUTUBE_KEY2);
            Key3 = data.getString(COL_YOUTUBE_KEY3);
            Key4 = data.getString(COL_YOUTUBE_KEY4);

            String Author1 = data.getString(COL_AUTHOR1);
            String Author2 = data.getString(COL_AUTHOR2);
            String Author3 = data.getString(COL_AUTHOR3);
            String Author4 = data.getString(COL_AUTHOR4);
            String Content1 = data.getString(COL_CONTENT1);
            String Content2 = data.getString(COL_CONTENT2);
            String Content3 = data.getString(COL_CONTENT3);
            String Content4 = data.getString(COL_CONTENT4);

            String POSTER_S_L = "w342";
            String POSTER_S_S = "w185";
            collapsingToolbarLayout.setTitle(title);

            final String POSTER_FINAL_URL = BuildConfig.MOVIEDB_API_IMAGE__URL + POSTER_S_S + poster_path;
            Picasso.with(context).load(POSTER_FINAL_URL.trim()).into(posterImageView);

            final String BACK_FINAL_URL = BuildConfig.MOVIEDB_API_IMAGE__URL + POSTER_S_L + backdrop_path;
            Picasso.with(context).load(BACK_FINAL_URL.trim()).into(backdropImageView);

            overviewTextView.setText(overview);
            releaseDateTextView.setText(release_date);
            userRatingTextView.setText(UserRating + "/10");
            runtimeView.setText(runtime + " " + "min");

            //trailers display
            if (Title1.length() != 0) {
                mTrailersCardview.setVisibility(View.VISIBLE);
                mPlay1.setVisibility(View.VISIBLE);
                mTrailerName1.setVisibility(View.VISIBLE);
                mTrailerName1.setText(Title1);
            }
            if (Title2.length() != 0) {
                mTrailersCardview.setVisibility(View.VISIBLE);
                mPlay2.setVisibility(View.VISIBLE);
                mTrailerName2.setVisibility(View.VISIBLE);
                mTrailerName2.setText(Title2);
            }
            if (Title3.length() != 0) {
                mTrailersCardview.setVisibility(View.VISIBLE);
                mPlay3.setVisibility(View.VISIBLE);
                mTrailerName3.setVisibility(View.VISIBLE);
                mTrailerName3.setText(Title3);
            }
            if (Title4.length() != 0) {
                mTrailersCardview.setVisibility(View.VISIBLE);
                mPlay4.setVisibility(View.VISIBLE);
                mTrailerName4.setVisibility(View.VISIBLE);
                mTrailerName4.setText(Title4);
            }


            //Reviews Display

            if (Author1.length() != 0) {
                mReviewsCardview.setVisibility(View.VISIBLE);
                mAuthor1.setVisibility(View.VISIBLE);
                mContent1.setVisibility(View.VISIBLE);
                mAuthor1.setText(Author1);
                mContent1.setText(Content1);
            }
            if (Author2.length() != 0) {
                mReviewsCardview.setVisibility(View.VISIBLE);
                mAuthor2.setVisibility(View.VISIBLE);
                mContent2.setVisibility(View.VISIBLE);
                mAuthor2.setText(Author2);
                mContent2.setText(Content2);
            }
            if (Author3.length() != 0) {
                mReviewsCardview.setVisibility(View.VISIBLE);
                mAuthor3.setVisibility(View.VISIBLE);
                mContent3.setVisibility(View.VISIBLE);
                mAuthor3.setText(Author3);
                mContent3.setText(Content3);
            }
            if (Author4.length() != 0) {
                mReviewsCardview.setVisibility(View.VISIBLE);
                mAuthor4.setVisibility(View.VISIBLE);
                mContent4.setVisibility(View.VISIBLE);
                mAuthor4.setText(Author4);
                mContent4.setText(Content4);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}