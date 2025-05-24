package edu.commonwealthu.baywaves;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class HomeFragment extends Fragment {

    public ExoPlayer exoPlayer;
    private ImageButton playPauseButton;
    private ImageButton nextSong;
    private ImageButton lastSong;
    private ImageButton likeSong;
    private SeekBar seekBar;
    final private Handler handler = new Handler();
    public boolean isPlaying = false;
    private boolean isLiked = false;
    private TextView startTime;
    private TextView endTime;
    private TextView songName;
    private TextView artistName;
    private ImageView albumCover;
    private ImageView circularVisualizer;

    public Track currentTrack;
    private TrackRepository trackRepository;
    private Artist currentArtist;
    private ArtistRepository artistRepository;
    private AlbumRepository albumRepository;
    private MusicClient client;

    private int currentTrackIndex;
    private List<Track> allTracks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);
        songName = view.findViewById(R.id.title_text);
        artistName = view.findViewById(R.id.artist_name);
        albumCover = view.findViewById(R.id.album_art);
        playPauseButton = view.findViewById(R.id.play_pause);
        nextSong = view.findViewById(R.id.skip_button);
        lastSong = view.findViewById(R.id.unskip_button);
        likeSong = view.findViewById(R.id.like_song);
        seekBar = view.findViewById(R.id.seekBar);
        circularVisualizer = view.findViewById(R.id.circle_visualizer);
        currentTrackIndex = 0;

        // Initialize repositories
        artistRepository = ArtistRepository.getInstance();
        trackRepository = TrackRepository.getInstance();
        albumRepository = AlbumRepository.getInstance();
        client = new MusicClient(this.getContext());

        trackRepository.setContext(requireContext());

        if (!trackRepository.isLoggedIn()) {
            boolean loginSuccess = trackRepository.login("your_username", "your_password");
            //showCustomToast("Login attempt: " + loginSuccess);
        }

        boolean isConnected = trackRepository.isServerConnected();
        String connectionMessage = trackRepository.getConnectionErrorMessage();
        //showCustomToast("Server connected: " + isConnected + " - " + connectionMessage);

        boolean isLoggedIn = trackRepository.isLoggedIn();
       // showCustomToast("Logged in: " + isLoggedIn);

        // Get all tracks once and reuse the list
        allTracks = trackRepository.getAllTracks();

        // Load the first track from the repository
        loadTrack(allTracks.get(currentTrackIndex));

        // Setup play/pause button
        playPauseButton.setOnClickListener(v -> playAndPause());

        // Setup next song button
        nextSong.setOnClickListener(v -> changeNextTrack());

        // Setup previous song button
        lastSong.setOnClickListener(v -> changePreviousTrack());

        // Setup like button
        likeSong.setOnClickListener(v -> addSong());

        // Setup seekBar
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long newPosition = (progress * exoPlayer.getDuration()) / 100;
                    exoPlayer.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar();
            }
        });

        return view;
    }

    // Find track index in our list by track id
    private int findTrackIndexById(int trackId) {
        if (allTracks == null || allTracks.isEmpty()) {
            Log.e("HomeFragment", "Track list is empty or null");
            return 0;
        }

        for (int i = 0; i < allTracks.size(); i++) {
            if (allTracks.get(i).getId() == trackId) {
                return i;
            }
        }

        Log.w("HomeFragment", "Track ID " + trackId + " not found in playlist, defaulting to first track");
        return 0; // Default to first track if not found
    }

    private void tryFallbackPlayback(Track track) {
        try {
            Log.d("HomeFragment", "Trying fallback playback for track ID: " + track.getId());

            // Try direct download instead of streaming URL
            if (client != null && track.getId() > 0) {
                try {
                    String downloadedPath = client.downloadSong(String.valueOf(track.getId()));
                    Log.d("HomeFragment", "Downloaded song to: " + downloadedPath);

                    if (exoPlayer != null) {
                        exoPlayer.release();
                    }

                    exoPlayer = new ExoPlayer.Builder(requireContext()).build();
                    exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(downloadedPath)));
                    exoPlayer.prepare();
                    exoPlayer.play();
                    isPlaying = true;
                    playPauseButton.setImageResource(R.drawable.pause_button);

                    // Update visualizer
                    Glide.with(requireContext())
                            .asGif()
                            .load(R.drawable.media_playing)
                            .into(circularVisualizer);

                    return; // Early return if successful
                } catch (Exception e) {
                    Log.e("HomeFragment", "Direct download failed: " + e.getMessage(), e);
                }
            }

            // If all else fails, try to find a local fallback track with the same ID
            List<Track> localTracks = TrackRepository.getInstance().getLocalFallbackTracks();
            for (Track localTrack : localTracks) {
                if (localTrack.getId() == track.getId()) {
                    Log.d("HomeFragment", "Found matching local track: " + localTrack.getName());

                    // Try to play the local track
                    if (exoPlayer != null) {
                        exoPlayer.release();
                    }

                    exoPlayer = new ExoPlayer.Builder(requireContext()).build();
                    exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(localTrack.getFilePath())));
                    exoPlayer.prepare();
                    exoPlayer.play();
                    isPlaying = true;
                    playPauseButton.setImageResource(R.drawable.pause_button);

                    // Update visualizer
                    Glide.with(requireContext())
                            .asGif()
                            .load(R.drawable.media_playing)
                            .into(circularVisualizer);

                    return;
                }
            }

            showCustomToast("Could not play track. Please try another song.");
        } catch (Exception e) {
            showCustomToast("Could not play track. Please try another song.");
        }
    }

    public void loadTrack(Track track) {
        try {
            Log.d("HomeFragment", "Loading track: " + track.getName() + " (ID: " + track.getId() + ")");

            // Make sure track is valid with a proper ID
            if (track.getId() <= 0) {
                showCustomToast("Invalid track ID");
                return;
            }

            // Complete track info if needed
            TrackRepository trackRepo = TrackRepository.getInstance();
            Track completeTrack = trackRepo.completeTrackInfo(track);

            if (completeTrack != null) {
                track = completeTrack;
            }

            // Update current track
            currentTrack = track;
            currentTrackIndex = findTrackIndexById(track.getId());

            Log.d("HomeFragment", "Track index in playlist: " + currentTrackIndex);

            // Get the streaming URI
            String streamingUri = trackRepo.getStreamingUri(track);
            Log.d("HomeFragment", "Using streaming URI: " + streamingUri);

            // Validate URI
            if (streamingUri == null || streamingUri.isEmpty()) {
                showCustomToast("Cannot play: missing audio source");
                return;
            }

            // Set up ExoPlayer with the track's streaming URI
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(streamingUri));

            // Release any existing player
            if (exoPlayer != null) {
                exoPlayer.release();
            }

            // Create new ExoPlayer instance
            exoPlayer = new ExoPlayer.Builder(requireContext()).build();
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();

            // Add a listener to detect playback errors
            Track finalTrack = track;
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    showCustomToast("Playback error: " + error.getMessage());

                    // Try fallback approach if the streaming URI failed
                    tryFallbackPlayback(finalTrack);
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_READY) {
                        updateTrackMetadata(finalTrack);
                    }
                }
            });

            // Load artist and update UI
            loadArtistForTrack(track);
            updateTrackMetadata(track);
            updateAlbumCover(track);

            // Reset like state
            isLiked = track.getLikes() > 0 || trackRepo.isTrackLiked(track.getId());
            updateLikeButton();
        } catch (Exception e) {
            showCustomToast("Error loading track: " + e.getMessage());
        }
    }



    private void loadArtistForTrack(Track track) {
        int artistId = track.getArtistId();
        Artist artist = artistRepository.getArtistById(artistId);

        if (artist != null) {
            currentArtist = artist;
        } else {
            currentArtist = new Artist(0, "Unknown Artist", "No bio available", 0, 1);
        }
    }

    private void updateTrackMetadata(Track track) {
        track.setLength((int) exoPlayer.getDuration());

        // Set initial time displays
        startTime.setText("0:00");
        endTime.setText(formatTime(track.getLength()));

        // Update seek bar max based on track length
        seekBar.setProgress(0);
        seekBar.setMax(100);

        // Update seekBar functionality
        updateSeekBar();

        // Update track name display
        songName.setText(track.getName());

        // Update artist name display
        if (currentArtist != null) {
            artistName.setText(currentArtist.getName());
        } else {
            artistName.setText("Unknown Artist");
        }
    }

    private void updateLikeButton() {
        Glide.with(requireContext())
                .load(isLiked ? R.drawable.liked_song : R.drawable.like_song)
                .dontAnimate()
                .into(likeSong);
    }

    private void updateAlbumCover(Track track) {
        int albumId = track.getAlbumId();
        int coverResourceId = albumRepository.getAlbumCoverResourceId(albumId);

        // Clear any existing images from Glide's cache for this view
        Glide.with(requireContext()).clear(albumCover);

        // Load the album cover using Glide with no caching
        Glide.with(requireContext())
                .load(coverResourceId)
                .skipMemoryCache(true)  // Skip memory cache
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Skip disk cache
                .into(albumCover);
    }

    /**
     * Changes to the next song once the skip button is clicked
     */
    public void changeNextTrack() {
        if (currentTrackIndex < allTracks.size() - 1) {
            currentTrackIndex++;
            Track newTrack = allTracks.get(currentTrackIndex);
            loadTrack(newTrack);
            playAndPause();
            updatePlaylistFragment();
        }
    }

    /**
     * Goes back to the previous song once the go back button is clicked
     */
    public void changePreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            Track newTrack = allTracks.get(currentTrackIndex);
            loadTrack(newTrack);
            playAndPause();
            updatePlaylistFragment();
        }
    }

    /**
     * Method for playing and pausing the current song
     */
    public void playAndPause() {
        if (isPlaying) {
            exoPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play_button);
            Glide.with(requireContext())
                    .load(R.drawable.media_playing)
                    .dontAnimate()
                    .into(circularVisualizer);
        } else {
            exoPlayer.play();
            playPauseButton.setImageResource(R.drawable.pause_button);
            Glide.with(requireContext())
                    .asGif()
                    .load(R.drawable.media_playing)
                    .into(circularVisualizer);
        }
        isPlaying = !isPlaying;
        updatePlaylistFragment();
    }

    private void addSong() {
        if (!isLiked) {
            // Play the animation once
            Glide.with(requireContext())
                    .asGif()
                    .load(R.drawable.like_song)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<GifDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model,
                                                       Target<GifDrawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            // Set to play only once
                            resource.setLoopCount(1);

                            // When animation ends, show the liked state
                            resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                                @Override
                                public void onAnimationEnd(Drawable drawable) {
                                    // Update track likes
                                    currentTrack.setLikes(currentTrack.getLikes() + 1);
                                    currentTrack.setLocalLikedState(true); // Set the liked state
                                    trackRepository.updateTrack(currentTrack);

                                    // Change to liked state image
                                    Glide.with(requireContext())
                                            .load(R.drawable.liked_song)
                                            .dontAnimate()
                                            .into(likeSong);

                                    isLiked = true;
                                    trackRepository.setTrackLiked(currentTrack.getId(), isLiked);

                                    // Get reference to the PlaylistFragment and update the liked songs playlist
                                    updatePlaylistFragment();
                                }
                            });
                            return false;
                        }
                    })
                    .into(likeSong);

            showCustomToast(getString(R.string.song_liked));
        } else {
            // Reset to original state
            Glide.with(requireContext())
                    .load(R.drawable.like_song)
                    .dontAnimate()
                    .into(likeSong);

            // Update track likes
            currentTrack.setLikes(currentTrack.getLikes() - 1);
            currentTrack.setLocalLikedState(false); // Set the liked state
            trackRepository.updateTrack(currentTrack);

            isLiked = false;
            trackRepository.setTrackLiked(currentTrack.getId(), isLiked);

            // Get reference to the PlaylistFragment and update the liked songs playlist
            updatePlaylistFragment();

            showCustomToast(getString(R.string.song_unliked));
        }
    }


    /**
     * Helper method to update the Playlist Fragment once a song is liked
     */
    private void updatePlaylistFragment() {
        PlaylistFragment playlistFragment = (PlaylistFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(PlaylistFragment.class.getSimpleName());

        if (playlistFragment != null && playlistFragment.isInside) {
            playlistFragment.updateCurrentlyPlayingSong(currentTrack.getId(), isPlaying);
            if (isLiked) {
                playlistFragment.addSongToDefault(currentTrack);
            } else {
                playlistFragment.removeSongFromDefault(currentTrack);
            }
        }
    }

    /**
     * Updates the seekbar to correspond with the position of the media-item
     */
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    long currentPosition = exoPlayer.getCurrentPosition();
                    long duration = exoPlayer.getDuration();

                    int progress = (int) ((currentPosition * 100) / duration);
                    seekBar.setProgress(progress);

                    startTime.setText(formatTime((int) currentPosition));
                    endTime.setText(formatTime((int) (duration - currentPosition)));
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    /**
     * Formats the the start and end time corresponding song
     * @param timeInMillis the current time of the currently playing song
     * @return the correct formatted time
     */
    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return (exoPlayer.getDuration() >= 600000) ?
                String.format("%02d:%02d", minutes, seconds) :
                String.format("%01d:%02d", minutes, seconds);
    }

    /**
     * Helper method to display a toast with a specific message
     * @param message the current message
     */
    private void showCustomToast(String message) {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}