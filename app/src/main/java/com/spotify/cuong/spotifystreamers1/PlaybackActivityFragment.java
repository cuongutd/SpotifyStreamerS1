package com.spotify.cuong.spotifystreamers1;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


/**
 */
public class PlaybackActivityFragment extends DialogFragment implements View.OnClickListener {

    private static final String LOG_TAG = PlaybackActivityFragment.class.getSimpleName();

    private MyTrack mTrack;
    private MyArtist mArtist;
    private ArrayList<MyTrack> mTopTracks;
    private int mTrackListPosition;

    private TextView mArtistName;
    private TextView mAlbum;
    private ImageView mImg;
    private TextView mTrackName;
    private SeekBar mSeekBar;
    private TextView mBeginDuration;
    private TextView mEndDuration;
    private ImageButton mPlayButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;

    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private SeekBarController updateSeekBar = new SeekBarController();

    public class SeekBarController implements Runnable {
        @Override
        public void run() {
            if (mMediaPlayer != null)
                if (mMediaPlayer.isPlaying()) {
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                    Log.d(LOG_TAG, String.valueOf(mCurrentPosition));
                    mSeekBar.setProgress(mCurrentPosition);
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                    mBeginDuration.setText(formatDuration(mSeekBar.getProgress()));
                    mEndDuration.setText("-" + formatDuration(mSeekBar.getMax() - mSeekBar.getProgress()));
                    mHandler.postDelayed(this, 1000);
                } else {

                    Log.d(LOG_TAG, "Not playing");
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                    //mSeekBar.setProgress(mSeekBar.getMax());
                }
        }
    }

    public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mMediaPlayer != null && fromUser) {
                mMediaPlayer.seekTo(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public PlaybackActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if (((MediaControllerCallback)getActivity()).getPlaybackFragment() == null) //happens when rotation, mainActiviy lost fragment
            ((MediaControllerCallback)getActivity()).setPlaybackFragment(this);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");

        super.onStart();
    }

    @Override
    public void onResume() {
        //startUpdatingSeekBar();
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMediaPlayer = ((MyApplication)getActivity().getApplication()).getMediaPlayer();

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mTopTracks = getArguments().getParcelableArrayList(Constants.BUNDLE_TEXT_TRACK_LIST);
                mTrackListPosition = getArguments().getInt(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION);
                mTrack = mTopTracks.get(mTrackListPosition);
                mArtist = getArguments().getParcelable(Constants.BUNDLE_TEXT_ARTIST);
                if (mMediaPlayer != null ) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            } else {
                Log.d(LOG_TAG, "you are not going in empty handed");
            }
        } else {
            mTopTracks = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_TEXT_TRACK_LIST);
            mTrackListPosition = savedInstanceState.getInt(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION);
            mTrack = mTopTracks.get(mTrackListPosition);
            mArtist = savedInstanceState.getParcelable(Constants.BUNDLE_TEXT_ARTIST);
        }

        View v = inflater.inflate(R.layout.fragment_playback, container, false);

        mArtistName = (TextView) v.findViewById(R.id.player_artistname_textview);

        mAlbum = (TextView) v.findViewById(R.id.player_albumname_textview);

        mTrackName = (TextView) v.findViewById(R.id.player_trackname_textview);

        mImg = (ImageView) v.findViewById(R.id.player_albumicon_imageview);

        mPlayButton = (ImageButton) v.findViewById(R.id.player_play_imagebutton);
        mNextButton = (ImageButton) v.findViewById(R.id.player_next_imagebutton);
        mPrevButton = (ImageButton) v.findViewById(R.id.player_prev_imagebutton);

        mPlayButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPrevButton.setOnClickListener(this);

        mBeginDuration = (TextView) v.findViewById(R.id.player_begin_duration_textview);
        mEndDuration = (TextView) v.findViewById(R.id.player_end_duration_textview);

        mSeekBar = (SeekBar) v.findViewById(R.id.player_seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBarListener());

        updateSeekBar = new SeekBarController();
        mHandler = new Handler();

        setTextViews();

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            ((MyApplication)getActivity().getApplication()).setMediaPlayer(mMediaPlayer);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mMediaPlayer.setDataSource(mTrack.getTrackUri());
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        setTextViews();
                        prepareMedia();
                        playMusic();
                    }
                });
                mMediaPlayer.prepareAsync(); //using async as suggested by android doc
            } catch (IOException e) {
                Log.d(LOG_TAG, "invalid track preview URL: " + mTrack.getTrackUri());
                Log.d(LOG_TAG, e.getMessage());
                return v;
            }

            //prepareMedia();
            //playMusic();
        } else { //when rotating screen
            Log.d(LOG_TAG, "mediaPlayer is NOT null");
            mSeekBar.setMax(mMediaPlayer.getDuration());
            if (mMediaPlayer.isPlaying()) {
                Log.d(LOG_TAG, "and is playing");
                startUpdatingSeekBar();
            }

        }

        return v;


    }


    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        stopUpdatingSeekBar();
        updateSeekBar = null;
        mHandler = null;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        ((MyApplication)getActivity().getApplication()).setMediaPlayer(mMediaPlayer);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach");
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateDiaglog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(LOG_TAG, "onDismiss");
        super.onDismiss(dialog);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.BUNDLE_TEXT_TRACK_LIST, mTopTracks);
        outState.putInt(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION, mTrackListPosition);
        outState.putParcelable(Constants.BUNDLE_TEXT_ARTIST, mArtist);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView");
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(LOG_TAG, "onCancel");
        super.onCancel(dialog);
    }

    private static String formatDuration(int currentPosition) {
        //to make it simple assuming all preview musics have less than 1 hour playback
        int minute = currentPosition / 1000 / 60;
        int second = (currentPosition / 1000) % 60;

        return String.valueOf(minute) + ":" + String.valueOf(String.format("%2s", second).replace(" ", "0"));
    }

    private void setTextViews() {
        mArtistName.setText(mArtist.getArtistName());
        mAlbum.setText(mTrack.getAlbumName());
        mTrackName.setText(mTrack.getTrackName());
        Picasso.with(this.getActivity()).load(mTrack.getTrackMidImageUrl()).into(mImg);
    }

    private void prepareMedia() {//playing new track

        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setProgress(0);
        mBeginDuration.setText(formatDuration(mSeekBar.getProgress()));
        mEndDuration.setText("-" + formatDuration(mSeekBar.getMax() - mSeekBar.getProgress()));


    }

    private void playMusic() {
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        if (mSeekBar.getProgress() == mSeekBar.getMax()) {
            mSeekBar.setProgress(0);
            mBeginDuration.setText(formatDuration(mSeekBar.getProgress()));
            mEndDuration.setText("-" + formatDuration(mSeekBar.getMax() - mSeekBar.getProgress()));
        }
        mMediaPlayer.start();
        startUpdatingSeekBar();

    }

    private void pauseMusic() {
        mMediaPlayer.pause();
        mPlayButton.setImageResource(android.R.drawable.ic_media_play);
        stopUpdatingSeekBar();

    }

    private void startUpdatingSeekBar() {
        mHandler.removeCallbacks(updateSeekBar);
        mHandler.postDelayed(updateSeekBar, 1000);
    }

    private void stopUpdatingSeekBar() {
        if (mHandler != null)
            mHandler.removeCallbacks(updateSeekBar);
    }

    private void playNewTrack() {

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mTrack.getTrackUri());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d(LOG_TAG, "invalid track preview URL: " + mTrack.getTrackUri());
            Log.d(LOG_TAG, e.getMessage());
        }
        //using prepareAsync, the listerner will call these after media is prepared
        //setTextViews();
        //prepareMedia();
        //playMusic();
    }


    @Override
    public void onClick(View v) {
        if (v.equals(mPlayButton)) {
            if (mMediaPlayer != null)
                if (mMediaPlayer.isPlaying()) { //playing, pause it
                    pauseMusic();
                } else {
                    playMusic();
                }
        } else if (v.equals(mPrevButton)) {
            if (mTrackListPosition == 0)
                mTrackListPosition = mTopTracks.size() - 1;
            else
                mTrackListPosition--;

            mTrack = mTopTracks.get(mTrackListPosition);

            playNewTrack();
        } else if (v.equals(mNextButton)) {
            if (mTrackListPosition == mTopTracks.size() - 1)
                mTrackListPosition = 0;
            else
                mTrackListPosition++;

            mTrack = mTopTracks.get(mTrackListPosition);

            playNewTrack();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(LOG_TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
