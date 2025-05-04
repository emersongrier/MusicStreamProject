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

        trackRepository.setContext(requireContext());

        boolean isConnected = trackRepository.isDatabaseConnected();
        String connectionMessage = trackRepository.getConnectionErrorMessage();

        // Get all tracks once and reuse the list
        allTracks = trackRepository.getAllTracks();

        // Load the first track from the repository
        loadTrack(allTracks.get(currentTrackIndex));

        // Setup play/pause button
        playPauseButton.setOnClickListener(v -> playAndPause());

        // Setup next song button
        nextSong.setOnClickListener(v -> {
            changeNextTrack();
        });

        // Setup previous song button
        lastSong.setOnClickListener(v -> {
            changePreviousTrack();
        });

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
        for (int i = 0; i < allTracks.size(); i++) {
            if (allTracks.get(i).getId() == trackId) {
                return i;
            }
        }
        return 0; // Default to first track if not found
    }

    public void loadTrack(Track track) {
        PlaylistFragment playlistFragment = (PlaylistFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(PlaylistFragment.class.getSimpleName());

        // Update current track
        currentTrack = track;
        currentTrackIndex = findTrackIndexById(track.getId());

        // Get the streaming URI
        String streamingUri = trackRepository.getStreamingUri(track);
        Log.d("HomeFragment", "Loading track with URI: " + streamingUri);

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

        // Load artist and update UI
        loadArtistForTrack(track);
        updateTrackMetadata(track);
        updateAlbumCover(track);

        // Reset like state
        isLiked = track.getLikes() > 0 || trackRepository.isTrackLiked(track.getId());
        updateLikeButton();
        //playlistFragment.updateSongItem();

    }

    private void loadArtistForTrack(Track track) {
        int artistId = track.getArtistId();

        // Get artist from repository
        Artist artist = artistRepository.getArtistById(artistId);

        if (artist != null) {
            currentArtist = artist;
        } else {
            // Fallback if artist not found
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

    // Method to change to next track
    public void changeNextTrack() {
        if (currentTrackIndex < allTracks.size() - 1) {
            currentTrackIndex++;
            Track newTrack = allTracks.get(currentTrackIndex);
            loadTrack(newTrack);
            playAndPause();
            updatePlaylistFragment();
        }
    }

    // Method to change to previous track
    public void changePreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            Track newTrack = allTracks.get(currentTrackIndex);
            loadTrack(newTrack);
            playAndPause();
            updatePlaylistFragment();
        }
    }

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

    // Add this helper method to safely find and update the PlaylistFragment
    private void updatePlaylistFragment() {
        // Find the PlaylistFragment using the tag specified in MainActivity
        PlaylistFragment playlistFragment = (PlaylistFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(PlaylistFragment.class.getSimpleName());

        // If fragment found, update the playlist
        if (playlistFragment != null && playlistFragment.isInside) {
            playlistFragment.updateCurrentlyPlayingSong(currentTrack.getId(), isPlaying);
            if (isLiked) {
                playlistFragment.addSongToDefault(currentTrack);
            } else {
                playlistFragment.removeSongFromDefault(currentTrack);
            }
        }
    }

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

    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return (exoPlayer.getDuration() >= 600000) ?
                String.format("%02d:%02d", minutes, seconds) :
                String.format("%01d:%02d", minutes, seconds);
    }

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