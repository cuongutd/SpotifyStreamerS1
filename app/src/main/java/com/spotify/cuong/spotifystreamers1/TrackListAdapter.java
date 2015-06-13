package com.spotify.cuong.spotifystreamers1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Cuong on 6/11/2015.
 */
public class TrackListAdapter extends ArrayAdapter<Track> {
    Context context;

    public TrackListAdapter(Context context, int resourceId, //resourceId=your layout
                             List<Track> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtAlbum;
        TextView txtTrack;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Track rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.track_album, null);
            holder = new ViewHolder();
            holder.txtAlbum = (TextView) convertView.findViewById(R.id.albumText);
            holder.txtTrack = (TextView) convertView.findViewById(R.id.trackName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.trackImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtAlbum.setText(rowItem.album.name);
        holder.txtTrack.setText(rowItem.name);


        String imgUrl="";
        for (Image img : rowItem.album.images)//get the last one which is smallest. if no image then no show
            imgUrl = img.url;
        if (imgUrl.length()> 0)
            Picasso.with(context).load(imgUrl).into(holder.imageView);
        else
            holder.imageView.setImageDrawable(null);



        return convertView;
    }

}
