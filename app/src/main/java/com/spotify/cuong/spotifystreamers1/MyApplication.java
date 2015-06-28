package com.spotify.cuong.spotifystreamers1;

import android.app.Application;
import android.media.MediaPlayer;

/**
 * Created by Cuong on 6/27/2015.
 */
public class MyApplication extends Application {

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }

    private MediaPlayer mMediaPlayer;
}
