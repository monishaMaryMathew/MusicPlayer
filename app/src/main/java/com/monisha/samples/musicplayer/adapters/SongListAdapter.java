package com.monisha.samples.musicplayer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monisha.samples.musicplayer.Model.Song;
import com.monisha.samples.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monisha on 3/5/2018.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {
    private List<Song> songList = new ArrayList<Song>();

    public SongListAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);
        TextView titleView = (TextView) view.findViewById(R.id.song_title);
        TextView artistView = (TextView) view.findViewById(R.id.song_artist);
        SongViewHolder vh = new SongViewHolder(view, titleView, artistView);
        return vh;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.title.setText(songList.get(position).getTitle());
        holder.artist.setText(songList.get(position).getArtist());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView artist;

        public SongViewHolder(View itemView, TextView title, TextView artist) {
            super(itemView);
            this.title = title;
            this.artist = artist;
        }
    }
}
