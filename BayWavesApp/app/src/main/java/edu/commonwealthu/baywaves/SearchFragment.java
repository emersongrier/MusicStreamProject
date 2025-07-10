package edu.commonwealthu.baywaves;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SongAdapter.OnSongClickListener {

    private static final String TAG = "SearchFragment";
    private EditText searchInput;
    private RecyclerView recyclerView;
    private List<Track> searchResults = new ArrayList<>();
    private SongAdapter adapter;
    private TrackRepository trackRepository;
    private MusicClient musicClient;
    public boolean isClicked = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        trackRepository = TrackRepository.getInstance();
        musicClient = new MusicClient(getContext());

        searchInput = view.findViewById(R.id.search_input);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SongAdapter(searchResults, this);
        recyclerView.setAdapter(adapter);

        // Add text change listener to search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) { // Start search after at least 2 characters
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    // Clear results when search box is empty
                    searchResults.clear();
                    adapter.setTracks(searchResults);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    /**
     * Performs a search operation for the given query.
     * First attempts to search the server database, then falls back to local search if needed.
     * The search is performed in a background thread to avoid blocking the UI.
     *
     * @param query The search query string entered by the user
     */
    private void performSearch(String query) {
        new Thread(() -> {
            try {
                String jsonResponse = musicClient.searchDb(query, 20, 0); // Limit to 20 results

                if (jsonResponse != null && getActivity() != null) {
                    List<Track> serverResults = trackRepository.parseTracksFromJson(jsonResponse);

                    // Update UI on the main thread
                    getActivity().runOnUiThread(() -> {
                        searchResults.clear();
                        searchResults.addAll(serverResults);
                        adapter.setTracks(searchResults);
                    });
                } else {
                    List<Track> localResults = searchLocalTracks(query);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            searchResults.clear();
                            searchResults.addAll(localResults);
                            adapter.setTracks(searchResults);

                            if (searchResults.isEmpty()) {
                                //Toast.makeText(getContext(), "No songs found", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error performing search", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    });
                }
            }
        }).start();
    }


    /**
     * Searches through local tracks as a fallback when server search fails.
     * Performs a case-insensitive search on track names and artist names.
     *
     * @param query The search query string
     * @return List of Track objects that match the search query
     */
    private List<Track> searchLocalTracks(String query) {
        List<Track> results = new ArrayList<>();
        List<Track> allTracks = trackRepository.getLocalFallbackTracks();

        String lowerQuery = query.toLowerCase();
        for (Track track : allTracks) {
            if (track.getName().toLowerCase().contains(lowerQuery) ||
                    (track.getArtistId() > 0 &&
                            ArtistRepository.getInstance().getArtistById(track.getArtistId()).getName().toLowerCase().contains(lowerQuery))) {
                results.add(track);
            }
        }

        return results;
    }


    /**
     * Handles song selection from search results.
     * When a song is clicked, this method validates the track, adds it to the player's queue,
     * and switches to the Home tab for playback. It integrates with the HomeFragment's
     * music player functionality.
     *
     * @param track The Track object that was selected by the user
     */
    @Override
    public void onSonglistClick(Track track) {
        if (track.getId() <= 0) {
           // Toast.makeText(getContext(), "Song cannot be played: invalid track ID", Toast.LENGTH_SHORT).show();
            return;
        }
        HomeFragment homeFragment = (HomeFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(HomeFragment.class.getSimpleName());

        if (homeFragment != null) {

            // Load the track into the player (only at specific spot)
            homeFragment.allTracks.add(homeFragment.currentTrackIndex + 1, track);
            homeFragment.loadTrack(track);

            if (homeFragment.isPlaying) {
                homeFragment.playAndPause();
            }
            if (!homeFragment.isPlaying) {
                homeFragment.playAndPause();
            }

            // Switch to Home tab
            try {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.switchToHomeTab();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error switching to home tab " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "Cannot play song: Player not found", Toast.LENGTH_SHORT).show();
        }
    }
}