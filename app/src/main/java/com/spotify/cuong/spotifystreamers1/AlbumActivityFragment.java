package com.spotify.cuong.spotifystreamers1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class AlbumActivityFragment extends Fragment {

    public static final String BUNDLE_TEXT_TRACK_LIST = "BUNDLE_TEXT_TRACK_LIST";

    private TrackListAdapter trackListAdapter;
    private View rootView;
    private ArrayList<MyTrack> myTracks;

    public AlbumActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_album, container, false) ;

        ListView v = (ListView) rootView.findViewById(R.id.trackListView);

        if (savedInstanceState != null)
            myTracks = savedInstanceState.getParcelableArrayList(BUNDLE_TEXT_TRACK_LIST);
        else
            //initiate a blank result
            myTracks = new ArrayList<MyTrack>();

        trackListAdapter = new TrackListAdapter(getActivity().getApplicationContext(), R.layout.track_album, myTracks);
        v.setAdapter(trackListAdapter);

        if (savedInstanceState == null){
            //get data from the intent and call spotify api to get top tracks
            Intent intent = getActivity().getIntent();

            String spotifyId = intent.getStringExtra(MainActivityFragment.SPOTIFY_ID);
            String artistName = intent.getStringExtra(MainActivityFragment.ARTIST_NAME);

            new SearchToptracks().execute(spotifyId);
        }
        return rootView;
    }

    public class SearchToptracks extends AsyncTask<String, Integer, List<MyTrack>> {

        private final String LOG_TAG = SearchToptracks.class.getSimpleName();

        @Override
        protected List<MyTrack> doInBackground(String... spotifyId){
            myTracks = new ArrayList<MyTrack>();

            //calling spotify api and update searchResultAdapter
            SpotifyApi api = new SpotifyApi();

            SpotifyService spotify = api.getService();

            Map<String, Object> options = new HashMap<>();

            options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());

            Tracks sporityResults = spotify.getArtistTopTrack(spotifyId[0], options);

            for (Track track : sporityResults.tracks)
                myTracks.add(new MyTrack(track));
            return myTracks;
        }

        @Override
        protected void onPostExecute(List<MyTrack> result) {
            trackListAdapter.clear();
            if (result.size()> 0)
                trackListAdapter.addAll(result);
            else {
                Utils.showMsg(getString(R.string.no_result_msg), getActivity());
            }

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(BUNDLE_TEXT_TRACK_LIST, myTracks);

        super.onSaveInstanceState(outState);
    }
}
