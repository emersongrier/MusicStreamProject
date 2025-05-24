package edu.commonwealthu.baywaves;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;


    // Interface for click listener
    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_icon, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.title.setText(playlist.getName());

        // Load cover image with Glide
        Glide.with(holder.itemView.getContext())
                .load(playlist.getCover())
                .placeholder(R.drawable.like_default)
                .error(R.drawable.like_default)
                .centerCrop()
                .into(holder.cover);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistClick(playlist);

            }
        });
    }



    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }


    // Method to update data
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView cover;
        TextView title;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
        }
    }
}