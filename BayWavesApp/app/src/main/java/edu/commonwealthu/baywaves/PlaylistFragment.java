package edu.commonwealthu.baywaves;

import android.content.Context;
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
    public List<Track> likedPlaylist = new ArrayList<>();
    private MaterialToolbar toolbar, playlistToolbar;
    private TextView songName, artistName;
    private ImageView musicPlaying;

    public boolean isInside;

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

    /**
     * Method that initializes the the list of playlists
     */
    private void loadPlaylists() {
        loadedPlaylists = new ArrayList<>();
        loadedPlaylists.add(defaultPlaylist);

        playlists.clear();
        playlists.addAll(loadedPlaylists);
        adapter.notifyDataSetChanged();
    }


    /**
     * Handles playlist click and navigates to the playlist menu
     * @param playlist The playlist being clicked/loaded
     */
    @Override
    public void onPlaylistClick(Playlist playlist) {
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

        songAdapter = new SongAdapter(tracks, this);
        playlistView.setAdapter(songAdapter);
        //songAdapter.notifyDataSetChanged();


        HomeFragment homeFragment = (HomeFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(HomeFragment.class.getSimpleName());

        if (homeFragment != null && homeFragment.currentTrack != null) {
            updateCurrentlyPlayingSong(homeFragment.currentTrack.getId(), homeFragment.isPlaying);
        }

    }

    /**
     * Handles song click and plays the current song
     * @param track The track item being clicked
     */
    @Override
    public void onSonglistClick(Track track) {
        HomeFragment homeFragment = (HomeFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(HomeFragment.class.getSimpleName());

        if (homeFragment != null) {
            homeFragment.loadTrack(track);

            if (homeFragment.isPlaying) {
                homeFragment.playAndPause();
            }
            if (!homeFragment.isPlaying) {
                homeFragment.playAndPause();
            }

            updateCurrentlyPlayingSong(track.getId(), homeFragment.isPlaying);
        }
    }

    /**
     * Updates the current song icon in the playlist.
     * Highlights current song and resets other songs to default.
     * @param currentTrackId The Id of the current selected track
     * @param isPlaying Determines if current song is playing to show icon
     */
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

            songNameView.setTextColor(getResources().getColor(R.color.black));
            artistNameView.setTextColor(getResources().getColor(R.color.black));
            musicPlayingView.setVisibility(View.INVISIBLE);

        }

        // Highlight playing song
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId() == currentTrackId) {
                RecyclerView.ViewHolder holder = playlistView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    View itemView = holder.itemView;
                    TextView songNameView = itemView.findViewById(R.id.song_name);
                    TextView artistNameView = itemView.findViewById(R.id.artist_name);
                    ImageView musicPlayingView = itemView.findViewById(R.id.music_playing);

                    songNameView.setTextColor(getResources().getColor(R.color.bayWave));
                    artistNameView.setTextColor(getResources().getColor(R.color.bayWave));
                    if (isPlaying) {
                        musicPlayingView.setVisibility(View.VISIBLE);
                        Glide.with(requireContext())
                                .asGif()
                                .load(R.drawable.music_playing)
                                .into(musicPlayingView);
                    }
                } else {
                    playlistView.scrollToPosition(i);
                }
                break;
            }
        }
    }

    /**
     * Adds a track to the default liked playlist
     * @param track The track being added to the playlist
     */
    public void addSongToDefault(Track track) {
        for (Track t : likedPlaylist) {
            if (t.getId() == track.getId()) {
                return;
            }
        }

        likedPlaylist.add(track);

        if (isInside && currentPlaylist != null &&
                currentPlaylist.getId() == defaultPlaylist.getId() && songAdapter != null) {
            tracks.clear();
            tracks.addAll(likedPlaylist);
            songAdapter.notifyDataSetChanged();
        }

        adapter.notifyDataSetChanged();
    }


    /**
     * Removes a track from the default liked playlist
     * @param track The track being removed from the playlist
     */
    public void removeSongFromDefault(Track track) {
        Track trackToRemove = null;
        for (Track t : likedPlaylist) {
            if (t.getId() == track.getId()) {
                trackToRemove = t;
                break;
            }
        }

        if (trackToRemove != null) {
            likedPlaylist.remove(trackToRemove);
            tracks.clear();
            tracks.addAll(likedPlaylist);
            songAdapter.notifyDataSetChanged();
            updateCurrentlyPlayingSong(trackToRemove.getId(), false);

            if (isInside && currentPlaylist != null &&
                    currentPlaylist.getId() == defaultPlaylist.getId() && songAdapter != null) {
                tracks.clear();
                tracks.addAll(likedPlaylist);
                songAdapter.notifyDataSetChanged();
            }

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

                loadedPlaylists.add(newPlaylist);

                playlists.clear();
                playlists.addAll(loadedPlaylists);
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.background);
        }
    }
}