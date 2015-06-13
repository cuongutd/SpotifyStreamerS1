package com.spotify.cuong.spotifystreamers1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public static final String SPOTIFY_ID = "SPOTIFY_ID";
    public static final String ARTIST_NAME = "ARTIST_NAME";


    private View rootView;
    private ArtistListAdapter searchResultAdapter;
    private Toast msgToast;
    private AsyncTask<String, Integer, List<Artist>> task;

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Fragment.onCreate", "here");
        if (savedInstanceState != null)
            Log.d("Fragment.onCreate", "state is not null");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Fragment.onCreateView","here");

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView v = (ListView) rootView.findViewById(R.id.artistList);

        //initiate a blank result
        if (searchResultAdapter == null) {

            searchResultAdapter = new ArtistListAdapter(getActivity().getApplicationContext(), R.layout.artist_main, new ArrayList<Artist>());

            v.setAdapter(searchResultAdapter);
        }


        // check if there is saved instance then recreated previous search result
        //if (savedInstanceState != null){
        //    Log.d(LOG_TAG, "go here");
        //    if (!Utils.isEmptyString(searchText) && searchText.length()> 2)
        //        new SearchArtists().execute(searchText);
        //}





        //find the search textbox and register the listener, which will call spotify web service to get search result when txt change

        EditText artistNameSearch = (EditText)rootView.findViewById(R.id.artistNameSearch);

        artistNameSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s.length()>2) { //dont search for short text
                    if (task != null)
                        task.cancel(true); //cancel running task
                    task = new SearchArtists();
                    task.execute(s.toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //for each search result, register a listener which will open artist detail screen
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist = searchResultAdapter.getItem(position);

                //call detail activity
                Intent topTracks = new Intent(getActivity(), AlbumActivity.class).putExtra(SPOTIFY_ID, artist.id).putExtra(ARTIST_NAME, artist.name);
                //detailWeather.setData(Uri.parse(fileUrl));
                getActivity().startActivity(topTracks);

            }
        });



        return rootView;
    }

    public class SearchArtists extends AsyncTask<String, Integer, List<Artist>> {

        private final String LOG_TAG = SearchArtists.class.getSimpleName();

        @Override
        protected List<Artist> doInBackground(String... artistName){
            //set a delay time in case its being cancelled
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //calling spotify api and update searchResultAdapter
            SpotifyApi api = new SpotifyApi();

            SpotifyService spotify = api.getService();

            ArtistsPager results = spotify.searchArtists(artistName[0]);

            return results.artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            searchResultAdapter.clear();
            if (result.size()> 0) {
                if (msgToast != null)//clear msg
                    msgToast.cancel();
                searchResultAdapter.addAll(result);
            }
            else{
                msgToast = Toast.makeText(getActivity(), getString(R.string.no_result_msg), Toast.LENGTH_SHORT);
                msgToast.show();

            }
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d("Fragment.onRestore", "here");
        if (savedInstanceState != null)
            Log.d("Fragment.onRestore", "state is not null");
        super.onViewStateRestored(savedInstanceState);

    }
}
