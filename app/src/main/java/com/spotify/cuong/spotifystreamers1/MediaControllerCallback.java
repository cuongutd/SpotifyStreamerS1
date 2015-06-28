package com.spotify.cuong.spotifystreamers1;

import java.util.ArrayList;

/**
 * Created by Cuong on 6/27/2015.
 */
public interface MediaControllerCallback {
    public void setPlaybackFragment(PlaybackActivityFragment mPlaybackFragment);
    public PlaybackActivityFragment getPlaybackFragment();
}
