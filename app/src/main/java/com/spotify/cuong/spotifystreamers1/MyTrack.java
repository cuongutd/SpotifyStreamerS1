package com.spotify.cuong.spotifystreamers1;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Cuong on 6/14/2015.
 */
public class MyTrack implements Parcelable {
    private String albumName;
    private String trackName;
    private String trackImageUrl;


    protected MyTrack(Parcel in) {
        albumName = in.readString();
        trackName = in.readString();
        trackImageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(trackName);
        dest.writeString(trackImageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyTrack> CREATOR = new Parcelable.Creator<MyTrack>() {
        @Override
        public MyTrack createFromParcel(Parcel in) {
            return new MyTrack(in);
        }

        @Override
        public MyTrack[] newArray(int size) {
            return new MyTrack[size];
        }
    };

    public MyTrack(Track spotifyTrack){
        if (spotifyTrack != null) {
            this.albumName = spotifyTrack.album.name;
            this.trackName = spotifyTrack.name;
            for (Image img : spotifyTrack.album.images)//get the last one which is smallest. if no image then no show
                this.trackImageUrl = img.url;
        }

    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackImageUrl() {
        return trackImageUrl;
    }

    public void setTrackImageUrl(String trackImageUrl) {
        this.trackImageUrl = trackImageUrl;
    }

}