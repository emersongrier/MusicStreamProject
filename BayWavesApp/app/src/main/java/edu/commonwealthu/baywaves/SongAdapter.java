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

    /**
     * Interface for click listener
     */
    public interface OnSongClickListener {
        void onSonglistClick(Track track);
    }

    /**
     * Initializes repositories and listeners
     * @param tracks List of tracks in playlist
     * @param listener Listener interface
     */
    public SongAdapter(List<Track> tracks, OnSongClickListener listener) {
        this.tracks = tracks;
        this.albumRepository = AlbumRepository.getInstance();
        this.artistRepository = ArtistRepository.getInstance();
        this.listener = listener;
    }

    private TextView lastClickedSongName;
    private TextView lastClickedArtistName;
    private ImageView lastClickedGif;

    /**
     * Updates last clicked song item
     * @param holder View holder for the song item
     */
    private void updateLastClickedViews(SongViewHolder holder) {
        lastClickedSongName = holder.songName;
        lastClickedArtistName = holder.artistName;
        lastClickedGif = holder.musicPlayingIcon;
    }


    /**
     * Returns a holder for a song item
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A song as a song_item layout
     */
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    /**
     * Sets the view holder to the specified track
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (tracks == null || position >= tracks.size()) {
            return; // Safety check
        }

        Track track = tracks.get(position);
        if (track == null) {
            return; // Another safety check
        }

        holder.songName.setText(track.getName());
        Artist artist = artistRepository.getArtistById(track.getArtistId());
        holder.artistName.setText(artist != null ? artist.getName() : "Unknown Artist");

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
                updateLastClickedViews(holder);

            }
        });

    }

    /**
     * Gets number of tracks in playlist
     * @return The number of songs in playlist
     */
    @Override
    public int getItemCount() {
        return tracks != null ? tracks.size() : 0;
    }

    /**
     * Sets list of available tracks from search
     * @param tracks List of tracks from search
     */
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }


    /**
     * Static class that sets up song as view holder item
     */
    static class SongViewHolder extends RecyclerView.ViewHolder {
        LinearLayout songItem;
        ImageView songCover;
        TextView songName;
        TextView artistName;
        ImageView musicPlayingIcon;

        /**
         * Declares layout as song item
         * @param itemView Th layout being set as a View holder item
         */
        SongViewHolder(View itemView) {
            super(itemView);
            songItem = itemView.findViewById(R.id.song_item);
            songCover = itemView.findViewById(R.id.song_cover);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            musicPlayingIcon = itemView.findViewById(R.id.music_playing);
        }

    }

    /**
     * Gets last clicked song name
     * @return last clicked song name
     */
    public TextView getSongName() {
        return lastClickedSongName;
    }

    /**
     * Gets last clicked song artist
     * @return Last song's artist
     */
    public TextView getArtistName() {
        return lastClickedArtistName;
    }

    /**
     * Returns play icon from last clicked song
     * @return Gif of play icon
     */
    public ImageView getGif() {
        return lastClickedGif;
    }

}