package edu.commonwealthu.baywaves;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Track> tracks;
    private OnSongClickListener listener;
    private AlbumRepository albumRepository; // You'll need to create or use an existing repository

    // Interface for click listener
    public interface OnSongClickListener {
        void onSongClick(Track track);
        void onSongLongClick(Track track, int position);
    }

    public SongAdapter(List<Track> tracks) {
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Track track = tracks.get(position);

        holder.songName.setText(track.getName());
        String artistName = getArtistName(track.getArtistId());
        holder.artistName.setText(artistName);

        // Load album cover image
        // Assuming you have a method to get album cover from album ID
        int albumCoverId = getAlbumCoverResourceId(track.getAlbumId());

        Glide.with(holder.itemView.getContext())
                .load(albumCoverId)
                .placeholder(R.drawable.default_playlist)
                .error(R.drawable.default_playlist)
                .centerCrop()
                .into(holder.songCover);

        holder.songCover.setImageResource(R.drawable.default_playlist);

        /* Set click listeners
        holder.songItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(track);
            }
        });

        holder.songItem.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onSongLongClick(track, holder.getAdapterPosition());
                return true;
            }
            return false;
        }); */
    }

    @Override
    public int getItemCount() {
        return tracks != null ? tracks.size() : 0;
    }

    // Method to update data
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    // Helper method to get artist name - implement according to your app structure
    private String getArtistName(int artistId) {
        // Placeholder - replace with actual implementation
        // This might come from an ArtistRepository or similar
        return "Artist " + artistId;
    }

    // Helper method to get album cover - implement according to your app structure
    private int getAlbumCoverResourceId(int albumId) {
        // Placeholder - replace with actual implementation
        // This might come from an AlbumRepository or similar
        return R.drawable.default_playlist;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView songCover;
        TextView songName;
        TextView artistName;

        SongViewHolder(View itemView) {
            super(itemView);
            songCover = itemView.findViewById(R.id.song_cover);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
        }
    }
}