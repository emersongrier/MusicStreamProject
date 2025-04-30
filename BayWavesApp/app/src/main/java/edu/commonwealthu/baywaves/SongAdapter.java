package edu.commonwealthu.baywaves;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Track> tracks;
    private AlbumRepository albumRepository;
    private ArtistRepository artistRepository;
    private OnSongClickListener listener;

    // Interface for click listener
    public interface OnSongClickListener {
        void onSonglistClick(Track track);
    }

    public SongAdapter(List<Track> tracks) {
        this.tracks = tracks;
        this.listener = null; // No listener provided
    }


    public SongAdapter(List<Track> tracks, OnSongClickListener listener) {
        this.tracks = tracks;
        this.albumRepository = AlbumRepository.getInstance();
        this.artistRepository = ArtistRepository.getInstance();
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (tracks == null || position >= tracks.size()) {
            return; // Safety check
        }

        Track track = tracks.get(position);
        if (track == null) {
            return; // Another safety check
        }

        // Set song name
        holder.songName.setText(track.getName());

        // Set artist name
        Artist artist = artistRepository.getArtistById(track.getArtistId());
        holder.artistName.setText(artist != null ? artist.getName() : "Unknown Artist");

        // Load album cover image
        int albumCoverId = albumRepository.getAlbumCoverResourceId(track.getAlbumId());

        Glide.with(holder.itemView.getContext())
                .load(albumCoverId)
                .placeholder(R.drawable.default_playlist)
                .error(R.drawable.default_playlist)
                .centerCrop()
                .into(holder.songCover);

        holder.songItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSonglistClick(track);

            }
        });

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

    public TextView getSongName() {
        return SongViewHolder.songName;
    }

    public TextView getArtistName() {
        return SongViewHolder.artistName;
    }

    public ImageView getGif() {
        return SongViewHolder.musicPlayingIcon;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        LinearLayout songItem;
        ImageView songCover;
        static TextView songName;
        static TextView artistName;
        static ImageView musicPlayingIcon;

        SongViewHolder(View itemView) {
            super(itemView);
            songItem = itemView.findViewById(R.id.song_item);
            songCover = itemView.findViewById(R.id.song_cover);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            musicPlayingIcon = itemView.findViewById(R.id.music_playing);
        }
    }
}