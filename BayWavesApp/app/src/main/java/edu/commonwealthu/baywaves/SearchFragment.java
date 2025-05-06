package edu.commonwealthu.baywaves;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

    private TextView songName, artistName;
    private ImageView musicPlaying;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize the repository and client
        trackRepository = TrackRepository.getInstance();
        musicClient = new MusicClient(getContext());

        // Initialize views
        searchInput = view.findViewById(R.id.search_input);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter with the listener
        adapter = new SongAdapter(searchResults, this);
        recyclerView.setAdapter(adapter);

        // Add text change listener to search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger search when text changes
                if (s.length() >= 2) { // Start search after at least 2 characters
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    // Clear results when search box is empty
                    searchResults.clear();
                    adapter.setTracks(searchResults); // Use the existing method to update adapter
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        return view;
    }

    private void performSearch(String query) {
        // Show loading indicator if needed
        // loadingIndicator.setVisibility(View.VISIBLE);

        // Run search in a background thread to avoid blocking UI
        new Thread(() -> {
            try {
                // Use the MusicClient to search the server database
                String jsonResponse = musicClient.searchDb(query, 20, 0); // Limit to 20 results

                if (jsonResponse != null && getActivity() != null) {
                    // Parse the JSON response into Track objects
                    List<Track> serverResults = parseTracksFromJson(jsonResponse);

                    // Update UI on the main thread
                    getActivity().runOnUiThread(() -> {
                        searchResults.clear();
                        searchResults.addAll(serverResults);
                        adapter.setTracks(searchResults); // Use the existing method to update adapter

                        if (searchResults.isEmpty()) {
                            Toast.makeText(getContext(), "No songs found", Toast.LENGTH_SHORT).show();
                        }

                        // Hide loading indicator if needed
                        // loadingIndicator.setVisibility(View.GONE);
                    });
                } else {
                    // If server search fails, fall back to local search
                    List<Track> localResults = searchLocalTracks(query);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            searchResults.clear();
                            searchResults.addAll(localResults);
                            adapter.setTracks(searchResults); // Use the existing method to update adapter

                            if (searchResults.isEmpty()) {
                                Toast.makeText(getContext(), "No songs found", Toast.LENGTH_SHORT).show();
                            }

                            // Hide loading indicator if needed
                            // loadingIndicator.setVisibility(View.GONE);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error performing search", e);

                // Handle error on UI thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // Hide loading indicator if needed
                        // loadingIndicator.setVisibility(View.GONE);
                    });
                }
            }
        }).start();
    }

    // Helper method to parse JSON if TrackRepository.parseTracksFromJson is not accessible
    private List<Track> parseTracksFromJson(String json) {
        // You can either:
        // 1. Make the parseTracksFromJson method in TrackRepository public, or
        // 2. Implement the parsing logic here

        // For now, let's call the repository method if it's accessible
        try {
            return trackRepository.parseTracksFromJson(json);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON, method might be private", e);
            // If the method is private, you would need to implement the parsing here
            return new ArrayList<>();
        }
    }

    private List<Track> searchLocalTracks(String query) {
        // Search in local tracks as fallback
        List<Track> results = new ArrayList<>();
        List<Track> allTracks = trackRepository.getLocalFallbackTracks();

        // Simple case-insensitive search
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

    // Modify the onSonglistClick method in your SearchFragment.java

    // Modify the onSonglistClick method in your SearchFragment.java

    @Override
    public void onSonglistClick(Track track) {
        Log.d(TAG, "Song clicked: " + track.getName() + " (ID: " + track.getId() + ")");

        // Log the track details to help debug
        Log.d(TAG, "Track details - ID: " + track.getId() +
                ", Name: " + track.getName() +
                ", FilePath: " + track.getFilePath() +
                ", ArtistID: " + track.getArtistId() +
                ", AlbumID: " + track.getAlbumId());

        // Make sure we have a valid track ID
        if (track.getId() <= 0) {
            Log.e(TAG, "Track has invalid ID: " + track.getId());
            Toast.makeText(getContext(), "Song cannot be played: invalid track ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the HomeFragment to play the selected song
        HomeFragment homeFragment = (HomeFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(HomeFragment.class.getSimpleName());

        if (homeFragment != null) {
            try {
                // Get the track from repository using the ID to ensure we have complete track info
                // This is a key step to ensure we're using the correct track data
                Track fullTrack = trackRepository.getTrackById(track.getId());

                if (fullTrack == null) {
                    // If the repository doesn't have this track, use the one from search results
                    fullTrack = track;
                    Log.d(TAG, "Using track from search results as repository returned null");
                } else {
                    Log.d(TAG, "Using track from repository - ID: " + fullTrack.getId());
                }

                // Load the track into the player
                homeFragment.loadTrack(fullTrack);

                // Add a small delay before playing to ensure ExoPlayer is ready
                new Handler().postDelayed(() -> {
                    if (homeFragment.exoPlayer != null && !homeFragment.isPlaying) {
                        homeFragment.playAndPause();
                    }
                }, 500);  // 500ms delay

                // Switch to Home tab
                try {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        mainActivity.switchToHomeTab();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error switching to home tab", e);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error playing track: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Error playing track: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "HomeFragment not found");
            Toast.makeText(getContext(), "Cannot play song: Player not found", Toast.LENGTH_SHORT).show();
        }
    }
}