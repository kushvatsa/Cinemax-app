package com.example.kush.cinemax;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kush.cinemax.fragments.FavoriteFragment;
import com.example.kush.cinemax.fragments.PopularFragment;
import com.example.kush.cinemax.fragments.RatedFragment;
import com.example.kush.cinemax.network_sync.Movie_SyncAdapter;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularFragment())
                    .commit();
        }
        Movie_SyncAdapter.initializeSyncAdapter(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:
                updateMovies();
                break;
            case R.id.menu_mostpopular:
                item.setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new PopularFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.menu_toprated:
                item.setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new RatedFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.menu_favorite:
                item.setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FavoriteFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
    Account account;
    private void updateMovies() {

        Context context = getApplicationContext();

        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(account, context.getString(R.string.cinemax), settingsBundle);
    }


}
