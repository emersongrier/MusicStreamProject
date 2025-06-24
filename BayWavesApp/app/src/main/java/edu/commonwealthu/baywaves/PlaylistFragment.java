package edu.commonwealthu.baywaves;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener, SongAdapter.OnSongClickListener {

    private View view;

    private RecyclerView recyclerView, playlistView;
    private PlaylistAdapter adapter;
    private SongAdapter songAdapter;
    private List<Playlist> playlists = new ArrayList<>();
    private List<Playlist> loadedPlaylists;
    private List<Track> tracks = new ArrayList<>();
    private TrackRepository trackRepository;
    private List<Track> likedPlaylist = new ArrayList<>();
    private MaterialToolbar toolbar, playlistToolbar;
    private TextView songName, artistName;
    private ImageView musicPlaying;

    public boolean isInside;


    private ActivityResultLauncher<Intent> resultLauncher;

    private Playlist defaultPlaylist;
    private PlaylistAdapter.PlaylistViewHolder selectPlaylist;
    private Playlist currentPlaylist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist, container, false);
        isInside = false;

        setHasOptionsMenu(true);
        trackRepository = TrackRepository.getInstance();

        initializeLikedPlaylist();
        initializeUI();

        defaultPlaylist = new Playlist(1, getString(R.string.liked_songs), getString(R.string.liked_songs_desc), R.drawable.like_default, 0, 1, likedPlaylist);
        loadPlaylists();

        return view;
    }

    /**
     * Initialize the likedPlaylist with all currently liked songs
     */
    private void initializeLikedPlaylist() {
        likedPlaylist.clear();
        List<Track> allTracks = trackRepository.getAllTracks();

        // Add all liked tracks to the likedPlaylist
        for (Track track : allTracks) {
            if (track.isLocalLikedState() || trackRepository.isTrackLiked(track.getId())) {
                likedPlaylist.add(track);
            }
        }
    }

    /**
     * Re-initializes the playlist_view layout
     */
    private void initializeUI() {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.playlist_toolbar_text));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        recyclerView = view.findViewById(R.id.playlist_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize adapter
        adapter = new PlaylistAdapter(playlists, this);
        recyclerView.setAdapter(adapter);
    }

    public void updateSongItem() {
        HomeFragment homeFragment = (HomeFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(HomeFragment.class.getSimpleName());
        if(homeFragment.isPlaying) {
            songName.setTextColor(getResources().getColor(R.color.bayWave));
            artistName.setTextColor(getResources().getColor(R.color.bayWave));
            musicPlaying.setVisibility(View.VISIBLE);
            Glide.with(requireContext())
                    .asGif()
                    .load(R.drawable.music_playing)
                    .into(musicPlaying);
        }
        else {
            musicPlaying.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Replaces current layout with new layout
     */
    public void setViewLayout(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        rootView.addView(view);
    }

    private void loadPlaylists() {
        loadedPlaylists = new ArrayList<>();
        loadedPlaylists.add(defaultPlaylist);

        // Update the adapter
        playlists.clear();
        playlists.addAll(loadedPlaylists);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onPlaylistClick(Playlist playlist) {
        // Handle playlist click - navigate to playlist details
        setViewLayout(R.layout.inside_playlist);
        isInside = true;
        currentPlaylist = playlist;

        playlistToolbar = view.findViewById(R.id.playlistToolbar);
        if (playlistToolbar != null) {
            playlistToolbar.setTitle(playlist.getName());
            playlistToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            ((AppCompatActivity)getActivity()).setSupportActionBar(playlistToolbar);
        }
        getActivity().invalidateOptionsMenu();

        playlistView = view.findViewById(R.id.current_playlist);
        playlistView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Copy the playlist's songs to our tracks list for display
        tracks.clear();
        tracks.addAll(playlist.getSongs());

        // Create a song adapter and set it on the RecyclerView
        songAdapter = new SongAdapter(tracks, this);
        playlistView.setAdapter(songAdapter);
        //songAdapter.notifyDataSetChanged();

        try {
            HomeFragment homeFragment = (HomeFragment) getActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(HomeFragment.class.getSimpleName());

            if (homeFragment != null && homeFragment.currentTrack != null) {
                // Just use the existing method with the current track ID and playing state
                updateCurrentlyPlayingSong(homeFragment.currentTrack.getId(), homeFragment.isPlaying);
            }
        } catch (Exception e) {
            // Just silently handle any potential errors
        }
    }

    // Implement SongAdapter.OnSongClickListener interface
    @Override
    public void onSonglistClick(Track track) {
        try {
            // Get the HomeFragment instance
            HomeFragment homeFragment = (HomeFragment) getActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(HomeFragment.class.getSimpleName());

            if (homeFragment != null) {
                // Load the track that was clicked
                homeFragment.loadTrack(track);

                // Start and stop playing
                if (homeFragment.isPlaying) {
                    homeFragment.playAndPause();
                }
                if (!homeFragment.isPlaying) {
                    homeFragment.playAndPause();
                }

                updateCurrentlyPlayingSong(track.getId(), homeFragment.isPlaying);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception gracefully, maybe show a toast
        }
    }

    public void updateCurrentlyPlayingSong(int currentTrackId, boolean isPlaying) {
        if (!isInside || playlistView == null) {
            return;
        }

        // Reset all items to normal state
        for (int i = 0; i < playlistView.getChildCount(); i++) {
            View itemView = playlistView.getChildAt(i);
            TextView songNameView = itemView.findViewById(R.id.song_name);
            TextView artistNameView = itemView.findViewById(R.id.artist_name);
            ImageView musicPlayingView = itemView.findViewById(R.id.music_playing);

            if (songNameView != null) {
                songNameView.setTextColor(getResources().getColor(R.color.black));
            }
            if (artistNameView != null) {
                artistNameView.setTextColor(getResources().getColor(R.color.black));
            }
            if (musicPlayingView != null) {
                musicPlayingView.setVisibility(View.INVISIBLE);
            }
        }

        // Now highlight the playing song if it exists in this playlist
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId() == currentTrackId) {
                // Found the playing track in this playlist
                RecyclerView.ViewHolder holder = playlistView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    // View is visible, update it
                    View itemView = holder.itemView;
                    TextView songNameView = itemView.findViewById(R.id.song_name);
                    TextView artistNameView = itemView.findViewById(R.id.artist_name);
                    ImageView musicPlayingView = itemView.findViewById(R.id.music_playing);

                    if (songNameView != null) {
                        songNameView.setTextColor(getResources().getColor(R.color.bayWave));
                    }
                    if (artistNameView != null) {
                        artistNameView.setTextColor(getResources().getColor(R.color.bayWave));
                    }
                    if (musicPlayingView != null && isPlaying) {
                        musicPlayingView.setVisibility(View.VISIBLE);
                        Glide.with(requireContext())
                                .asGif()
                                .load(R.drawable.music_playing)
                                .into(musicPlayingView);
                    }
                } else {
                    // View not visible, scroll to it
                    playlistView.scrollToPosition(i);
                }
                break;  // Found our match, no need to continue
            }
        }
    }

    public void addSongToDefault(Track track) {
        // Check if the track is already in the liked playlist by ID
        for (Track t : likedPlaylist) {
            if (t.getId() == track.getId()) {
                // Already exists, no need to add
                return;
            }
        }

        // Only add to likedPlaylist - it's automatically in defaultPlaylist
        // since defaultPlaylist.getSongs() points to likedPlaylist
        likedPlaylist.add(track);

        // If we're currently viewing the default playlist, update the UI
        if (isInside && currentPlaylist != null &&
                currentPlaylist.getId() == defaultPlaylist.getId() && songAdapter != null) {
            tracks.clear();
            tracks.addAll(likedPlaylist);
            songAdapter.notifyDataSetChanged();
        }

        // Update the playlist grid view
        adapter.notifyDataSetChanged();
    }


    public void removeSongFromDefault(Track track) {
        // Find the track in the liked playlist by ID
        Track trackToRemove = null;
        for (Track t : likedPlaylist) {
            if (t.getId() == track.getId()) {
                trackToRemove = t;
                break;
            }
        }

        if (trackToRemove != null) {
            // Remove from likedPlaylist only - it's automatically removed from defaultPlaylist
            // since defaultPlaylist.getSongs() points to likedPlaylist
            likedPlaylist.remove(trackToRemove);
            tracks.clear();
            tracks.addAll(likedPlaylist);
            songAdapter.notifyDataSetChanged();
            updateCurrentlyPlayingSong(trackToRemove.getId(), false);

            // If we're currently viewing the default playlist, update the UI
            if (isInside && currentPlaylist != null &&
                    currentPlaylist.getId() == defaultPlaylist.getId() && songAdapter != null) {
                tracks.clear();
                tracks.addAll(likedPlaylist);
                songAdapter.notifyDataSetChanged();
            }

            // Update the playlist grid view
            adapter.notifyDataSetChanged();
        }
    }

    /**
     Creates menu for toolbars
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(isInside) {
            inflater.inflate(R.menu.playlist_menu, menu);
        }
        else {
            inflater.inflate(R.menu.menu_main, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     *  Gives menu items functionality
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_new_playlist) {
            showCustomDialog(R.layout.new_playlist_dialog);
        }
        if(id == R.id.playlist_back_arrow) {
            isInside = false;
            setViewLayout(R.layout.fragment_playlist);
            initializeUI();
            getActivity().invalidateOptionsMenu();
            songAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a custom dialog using a specified layout.
     * @param layoutId id of the layout
     */
    private void showCustomDialog(int layoutId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(layoutId, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton(android.R.string.cancel, null);

        EditText playlistName = dialogView.findViewById(R.id.myEditText);
        Button createButton = dialogView.findViewById(R.id.createNewPlaylist);

        AlertDialog dialog = builder.create();
        dialog.show();
        createButton.setOnClickListener(v -> {
            String name = playlistName.getText().toString().trim();
            if (!name.isEmpty()) {
                Playlist newPlaylist = new Playlist(
                        loadedPlaylists.size() + 1,
                        name,
                        getString(R.string.liked_songs_desc),
                        R.drawable.dafault_album_cover,
                        0,
                        1,
                        new ArrayList<Track>()
                );

                // Add the new playlist to the list
                loadedPlaylists.add(newPlaylist);

                // Update the adapter data and refresh the view
                playlists.clear();
                playlists.addAll(loadedPlaylists);
                adapter.notifyDataSetChanged();

                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.background);
        }
    }
}