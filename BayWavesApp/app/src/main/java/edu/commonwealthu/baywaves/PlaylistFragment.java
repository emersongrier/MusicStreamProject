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
import android.widget.Toolbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener {

    private View view;

    private RecyclerView recyclerView, playlistView;
    private PlaylistAdapter adapter;
    private SongAdapter songAdapter;
    private List<Playlist> playlists = new ArrayList<>();
    private List<Playlist> loadedPlaylists;
    private TrackRepository trackRepository;
    private List<Track> likedPlaylist;
    private Toolbar toolbar;


    private ActivityResultLauncher<Intent> resultLauncher;

    private Playlist defaultPlaylist;
    private PlaylistAdapter.PlaylistViewHolder selectPlaylist;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist, container, false);

        setHasOptionsMenu(true);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        toolbar.setTitle(getString(R.string.playlist_toolbar_text));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        trackRepository = TrackRepository.getInstance();

        likedPlaylist = new ArrayList<Track>();

      /*  for(int i = 1; i<trackRepository.getAllTracks().size(); i++) {
            if(trackRepository.getTrackById(i).isLocalLikedState()) {
                likedPlaylist.add(trackRepository.getTrackById(i));
            }
        }*/

        defaultPlaylist = new Playlist(1, getString(R.string.liked_songs), getString(R.string.liked_songs_desc), R.drawable.like_default, 0, 1, likedPlaylist);



        recyclerView = view.findViewById(R.id.playlist_view);
        // Use GridLayoutManager for a grid of playlists (2 columns)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize adapter
        adapter = new PlaylistAdapter(playlists, this);
        recyclerView.setAdapter(adapter);

        /*for inside of playlist
        playlistView = view.findViewById(R.id.current_playlist);
        songAdapter = new SongAdapter(likedPlaylist);
        playlistView.setAdapter(songAdapter);*/
        //playlistView = view.findViewById(R.id.current_playlist);
        //playlistView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data (replace with your actual data loading logic)
        loadPlaylists();

        return view;
    }


    /*
        Replaces current layout with new layout
     */
    private void setViewLayout(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(view);
    }

    private void loadPlaylists() {
        // Sample data - replace with your actual data source
        // Example: fetch from API or database
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
        //Toast.makeText(getContext(), "Clicked: " + playlist.getName(), Toast.LENGTH_SHORT).show();
        setViewLayout(R.layout.inside_playlist);
        playlistView = view.findViewById(R.id.current_playlist);
        playlistView.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(playlist.getSongs());
        playlistView.setAdapter(songAdapter);
        int width = playlistView.getWidth();

        MaterialToolbar playlistToolbar = view.findViewById(R.id.playlistToolbar);
        if (playlistToolbar != null) {
            playlistToolbar.setTitle(playlist.getName());
            //playlistToolbar.setMinimumWidth(width);
        }

        // Example: Navigate to playlist detail
        // Bundle bundle = new Bundle();
        // bundle.putInt("playlistId", playlist.getId());
        // Navigation.findNavController(requireView()).navigate(R.id.action_to_playlistDetail, bundle);\

    }

    public void addSongToDefault(Track track) {
        if (!likedPlaylist.contains(track)) {
            likedPlaylist.add(track);
            adapter.notifyDataSetChanged();
            //songAdapter.notifyDataSetChanged();
        }
        recyclerView.setAdapter(adapter);
        //playlistView.setAdapter(songAdapter);
    }

    public void removeSongToDefault(Track track) {
        if (!likedPlaylist.contains(track)) {
            likedPlaylist.remove(track);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_new_playlist) {
            showCustomDialog(R.layout.new_playlist_dialog);
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

        //dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.background);
        }
    }
}