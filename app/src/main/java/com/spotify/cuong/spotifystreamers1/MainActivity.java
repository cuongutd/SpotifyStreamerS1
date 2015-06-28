package com.spotify.cuong.spotifystreamers1;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends MyActionBarActivity implements Callback, MediaControllerCallback{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private PlaybackActivityFragment mPlaybackFragment;//keep dialog fragment when its dismissed to control current played track

    private boolean mIsTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.id_fragment_album) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mIsTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                AlbumActivityFragment df = new AlbumActivityFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.id_fragment_album, df, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mIsTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        //ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        //TODO: set fragment twoPane, initiate data load


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_now_playing) {

            //

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Bundle artistInfo) {
        if (mIsTwoPane) {
            AlbumActivityFragment df = new AlbumActivityFragment();
            //add twoPane indicator to second fragment
            artistInfo.putBoolean(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, mIsTwoPane);
            df.setArguments(artistInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.id_fragment_album, df, DETAILFRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, AlbumActivity.class)
                    .putExtra(Constants.BUNDLE_TEXT_ARTIST, artistInfo.getParcelable(Constants.BUNDLE_TEXT_ARTIST))
                    .putExtra(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, mIsTwoPane);
            startActivity(intent);
        }
    }

    @Override
    public void showPlayback(MyArtist artist, ArrayList<MyTrack> tracks, int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (mPlaybackFragment == null) {
            Log.d(LOG_TAG, "dialog is null");
            mPlaybackFragment = new PlaybackActivityFragment();
        }else
            Log.d(LOG_TAG, "dialog is not null");

        if (fragmentManager.findFragmentByTag("dialog") != null) {
            Log.d(LOG_TAG, "found dialog in the stack");
            mPlaybackFragment = (PlaybackActivityFragment) fragmentManager.findFragmentByTag("dialog");
        }else
            Log.d(LOG_TAG, "not found dialog in the stack");

        //FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.BUNDLE_TEXT_TRACK_LIST, tracks);
        args.putInt(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION, position);
        args.putParcelable(Constants.BUNDLE_TEXT_ARTIST, artist);
        mPlaybackFragment.setArguments(args);
        mPlaybackFragment.show(fragmentManager, "dialog");
    }

    public PlaybackActivityFragment getPlaybackFragment() {
        return mPlaybackFragment;
    }

    public void setPlaybackFragment(PlaybackActivityFragment playbackFragment) {
        this.mPlaybackFragment = playbackFragment;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
