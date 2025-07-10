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


    /**
     * Interface for click listener
     */
    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    /**
     * Initializes list of playlists and listener
     * @param playlists A list of playlists
     * @param listener The click listener for playlist insteractions
     */
    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }


    /**
     * Creates a new ViewHolder instance for playlist items.
     * Inflates the playlist_icon layout and returns a new PlaylistViewHolder.
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (not used in this implementation)
     * @return A new PlaylistViewHolder that holds a View of the playlist_icon layout
     */
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_icon, parent, false);
        return new PlaylistViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     * Sets the playlist title, loads the cover image using Glide, and sets up click listeners.
     * @param holder The ViewHolder which should be updated to represent the contents of the item
     * @param position The position of the item within the adapter's data set
     */
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



    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of playlists in the adapter, or 0 if playlists is null
     */
    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }


    /**
     * Updates the playlist data in the adapter and notifies observers.
     * This method should be called when the playlist data changes.
     * @param playlists The new list of playlists to display
     */
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }


    /**
     * ViewHolder class for playlist items.
     * Holds references to the views within each playlist item layout.
     */
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