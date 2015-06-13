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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Cuong on 6/11/2015.
 */
public class ArtistListAdapter extends ArrayAdapter<Artist> {

    Context context;

    public ArtistListAdapter(Context context, int resourceId, //resourceId=your layout
                                 List<Artist> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Artist rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.artist_main, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.artistName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.artistImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.name);
        String imgUrl="";
        for (Image img : rowItem.images)//get the last one which is smallest. if no image then no show
            imgUrl = img.url;
        if (imgUrl.length()> 0)
            Picasso.with(context).load(imgUrl).into(holder.imageView);
        else
            holder.imageView.setImageDrawable(null);



        return convertView;
    }

}
