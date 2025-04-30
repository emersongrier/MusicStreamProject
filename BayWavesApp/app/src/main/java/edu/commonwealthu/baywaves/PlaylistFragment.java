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

    private boolean isInside;

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

        // Initialize likedPlaylist with all liked songs from the repository
        initializeLikedPlaylist();

        initializeUI();

        defaultPlaylist = new Playlist(1, getString(R.string.liked_songs), getString(R.string.liked_songs_desc), R.drawable.like_default, 0, 1, likedPlaylist);

        // Load data
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

    /*
        Re-initializes the playlist_view layout
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

    /*
        Replaces current layout with new layout
     */
    private void setViewLayout(int id){
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
        //musicPlaying = view.findViewById(R.id.music_playing);
        //musicPlaying.setVisibility(View.INVISIBLE);

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
    }

    // Add these new methods to implement SongAdapter.OnSongClickListener interface
    @Override
    public void onSonglistClick(Track track) {
        // Handle song click in the playlist
        //updatePlaying(true);
        songName = songAdapter.getSongName();
        songName.setTextColor(getResources().getColor(R.color.bayWave));
        artistName = songAdapter.getArtistName();
        artistName.setTextColor(getResources().getColor(R.color.bayWave));
        musicPlaying = songAdapter.getGif();
        musicPlaying.setVisibility(View.VISIBLE);
        Glide.with(requireContext())
                .asGif()
                .load(R.drawable.music_playing)
                .into(musicPlaying);
        songAdapter.notifyDataSetChanged();
    }

    private void updatePlaying(boolean isClicked) {
        HomeFragment homeFragment = (HomeFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(PlaylistFragment.class.getSimpleName());
        if(homeFragment != null) {
            if(isClicked) {
                homeFragment.playAndPause();
            }
        }
    }

    public void addSongToDefault(Track track) {
        // First check if the track is already in the liked playlist
        boolean containsTrack = false;
        for (Track t : likedPlaylist) {
            if (t.getId() == track.getId()) {
                containsTrack = true;
                break;
            }
        }

        if (!containsTrack) {
            // Add to both collections
            likedPlaylist.add(track);
            defaultPlaylist.getSongs().add(track);

            // If we're currently viewing the default playlist, update the UI
            if (isInside && currentPlaylist != null &&
                    currentPlaylist.getId() == defaultPlaylist.getId() && songAdapter != null) {
                // Update tracks and refresh adapter
                tracks.clear();
                tracks.addAll(likedPlaylist);
                songAdapter.notifyDataSetChanged();
            }

            // Update the playlist grid view
            adapter.notifyDataSetChanged();
        }
    }

    public void removeSongToDefault(Track track) {
        // Find the track in the liked playlist
        Track trackToRemove = null;
        for (Track t : likedPlaylist) {
            if (t.getId() == track.getId()) {
                trackToRemove = t;
                break;
            }
        }

        if (trackToRemove != null) {
            // Remove from both collections
            likedPlaylist.remove(trackToRemove);
            defaultPlaylist.getSongs().remove(trackToRemove);

            // If we're currently viewing the default playlist, update the UI
            if (isInside && currentPlaylist != null &&
                    currentPlaylist.getId() == defaultPlaylist.getId() && songAdapter != null) {
                // Update tracks and refresh adapter
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