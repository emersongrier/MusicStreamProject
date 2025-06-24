package edu.commonwealthu.baywaves;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

/**
 * HomeFragment serves as the main music player interface for the BayWaves application.
 * This fragment handles music playback, track navigation, like/unlike functionality,
 * and displays album artwork with visual animations.
 *
 * The fragment manages two distinct UI states:
 * 1. Empty state - displayed when no music is available in the library
 * 2. Player state - displayed when music is available, showing full playback controls
 *
 * Key features include:
 * - Audio playback using ExoPlayer
 * - Track metadata display (title, artist, album art)
 * - Playback controls (play/pause, next/previous)
 * - Like/unlike functionality with animated feedback
 * - Seek bar for track position control
 * - Visual animations during playback
 *
 * @author Jacob Leonardo
 */

public class HomeFragment extends Fragment {


    public ExoPlayer exoPlayer;
    private ImageButton playPauseButton, nextSong, lastSong, likeSong;
    private Button browseMusic;
    private ImageView musicIcon, albumCover, circularVisualizer;
    private TextView startTime, endTime, songName, artistName, emptyLibrary, searchForSongs;
    private CardView albumVisualizer;
    private SeekBar seekBar;

    final private Handler handler = new Handler();
    public boolean isPlaying = false;
    private boolean isLiked = false;

    public Track currentTrack;
    private TrackRepository trackRepository;
    private Artist currentArtist;
    private ArtistRepository artistRepository;
    private AlbumRepository albumRepository;
    private MusicClient client;

    public int currentTrackIndex;
    public List<Track> allTracks;

    public View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setUpRepositories();

        if(allTracks.isEmpty()) {
            view = inflater.inflate(R.layout.no_song, container, false);
            setUpStartScreenViews(view);
            setUpStartScreenListeners();
        }
        else {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            setUpPlayScreenViews(view);
            setConstraints();
            setUpEventListeners();
            setUpSeekBar();

            currentTrackIndex = 0;
            loadTrack(allTracks.get(currentTrackIndex));
        }

        return view;
    }


    /**
     * Helper method to set up views/organize code
     * @param view the start screen/no music in library layout
     */
    private void setUpStartScreenViews(View view) {
        emptyLibrary = view.findViewById(R.id.empty_library);
        searchForSongs = view.findViewById(R.id.search_for_songs);
        musicIcon = view.findViewById(R.id.music_icon);
        browseMusic = view.findViewById(R.id.browse_button);
    }

    /**
     * Helper method to set up views/organize code
     * @param view the play screen layout
     */
    private void setUpPlayScreenViews(View view) {
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
        albumVisualizer = view.findViewById(R.id.album_visualizer);
    }

    /**
     * Helper method to set up repositories/organize code
     */
    private void setUpRepositories() {
        artistRepository = ArtistRepository.getInstance();
        trackRepository = TrackRepository.getInstance();
        albumRepository = AlbumRepository.getInstance();
        client = new MusicClient(this.getContext());

        trackRepository.setContext(requireContext());
        allTracks = trackRepository.getAllTracks();
    }

    /**
     * Helper method to set up event listeners/organize code
     */
    private void setUpEventListeners() {
        playPauseButton.setOnClickListener(v -> playAndPause());
        nextSong.setOnClickListener(v -> changeNextTrack());
        lastSong.setOnClickListener(v -> changePreviousTrack());
        likeSong.setOnClickListener(v -> addSong());
    }

    /**
     * Helper method to set up the browse music button/organize code
     */
    private void setUpStartScreenListeners () {
        browseMusic.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.switchToSearchTab();
        });
    }

    /**
     * Dynamically sets album cover and visualizer constraints to be consistent with screen size
     */
    private void setConstraints(){
        albumVisualizer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                albumVisualizer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                float radius = albumVisualizer.getWidth() / 2f;
                float visualizerRadius = albumVisualizer.getWidth();

                circularVisualizer.setMinimumWidth((int) visualizerRadius);
                circularVisualizer.setMinimumHeight((int) visualizerRadius);
                albumVisualizer.setRadius(radius);
            }
        });
    }

    /**
     * Sets up seekbar for position control
     */
    private void setUpSeekBar() {
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
    }

    /**
     * Updates the seekbar to correspond with the position of the song
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
     * Formats the the start and end time for the corresponding song
     * @param timeInMillis the current time of the currently playing song
     * @return the formatted time
     */
    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return (exoPlayer.getDuration() >= 600000) ?
                String.format("%02d:%02d", minutes, seconds) :
                String.format("%01d:%02d", minutes, seconds);
    }

    /**
     * Finds track's index in the list by its Id
     * @param trackId The Id of the current track
     * @return index of track in the list, returns 0 if not found
     */
    public int findTrackIndexById(int trackId) {
        if (allTracks == null || allTracks.isEmpty()) {
            showCustomToast("Track list is empty or null");
            return 0;
        }

        for (int i = 0; i < allTracks.size(); i++) {
            if (allTracks.get(i).getId() == trackId) {
                return i;
            }
        }

        showCustomToast("Track ID " + trackId + " not found in playlist, defaulting to first track"); // Exception check
        return 0;
    }


    /**
     * Loads the current track to be played by the ExoPlayer
     * @param track Current track being loaded
     */
    public void loadTrack(Track track) {

        if (track.getId() <= 0) {
            showCustomToast("Invalid track ID");
            return;
        }

        TrackRepository trackRepo = TrackRepository.getInstance();
        /*Track completeTrack = trackRepository.getTrackById(track.getId()); //replace with client.downloadSong
        if (completeTrack != null) {
            track = completeTrack;
        }*/

        currentTrack = track;
        currentTrackIndex = findTrackIndexById(track.getId());

        String streamingUri = client.getStreamingUrl(String.valueOf(track.getId()));
        if (streamingUri == null || streamingUri.isEmpty()) {
            showCustomToast("Streamed Song not loaded");
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(streamingUri));

        // Release any existing player
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();

        updateTrackMetadata(track);

        // Reset like state
        isLiked = track.getLikes() > 0 || trackRepo.isTrackLiked(track.getId());
        updateLikeButton();
    }


    /**
     * Sets the metadata for the currently loaded track
     * @param track The track who's metadata will be loaded
     */
    private void updateTrackMetadata(Track track) {
        track.setLength((int) exoPlayer.getDuration());

        startTime.setText("0:00");
        endTime.setText(formatTime(track.getLength()));

        seekBar.setProgress(0);
        seekBar.setMax(100);
        updateSeekBar();

        songName.setText(track.getName());
        loadArtistForTrack(track);
        updateAlbumCover(track);
    }


    /**
     * Sets the album cover for the corresponding song if it exists
     * @param track The track of the album cover to be loaded
     */
    private void updateAlbumCover(Track track) {
        int albumId = track.getAlbumId();
        int coverResourceId = albumRepository.getAlbumCoverResourceId(albumId);

        Glide.with(requireContext()).clear(albumCover);

        Glide.with(requireContext())
                .load(coverResourceId)
                .skipMemoryCache(true)  // Skip memory cache
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Skip disk cache
                .into(albumCover);
    }

    /**
     * Loads the artist for the current track
     * @param track The track who's artist will be loaded for
     */
    private void loadArtistForTrack(Track track) {
        int artistId = track.getArtistId();
        Artist artist = artistRepository.getArtistById(artistId);

        if (artist != null) {
            currentArtist = artist;
            artistName.setText(currentArtist.getName());
        } else {
            currentArtist = new Artist(0, "Unknown Artist", "No bio available", 0, 1);
            artistName.setText("Unknown Artist");
        }
    }


    /**
     * Sets the like button to the current song's like state
     */
    private void updateLikeButton() {
        Glide.with(requireContext())
                .load(isLiked ? R.drawable.liked_song : R.drawable.like_song)
                .dontAnimate()
                .into(likeSong);
    }

    /**
     * Toggles playback state. Updates play/pause UI and visualizer animation,
     * and notifies PlaylistFragment of current playback state.
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
     * Adds a song to the default playlist and performs like button animation
     */
    private void addSong() {
        if (!isLiked) {

            // Play animation
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
                                    currentTrack.setLikes(currentTrack.getLikes() + 1);
                                    client.toggleSongLike(String.valueOf(currentTrack.getId()));

                                    // Keeps the like button in the like state
                                    Glide.with(requireContext())
                                            .load(R.drawable.liked_song)
                                            .dontAnimate()
                                            .into(likeSong);

                                    isLiked = true;
                                    trackRepository.setTrackLiked(currentTrack.getId(), isLiked);
                                    updatePlaylistFragment();
                                }
                            });
                            return false;
                        }
                    })
                    .into(likeSong);

            showCustomToast(getString(R.string.song_liked));
        } else {
            // Resets like button to the unlike state
            Glide.with(requireContext())
                    .load(R.drawable.like_song)
                    .dontAnimate()
                    .into(likeSong);


            currentTrack.setLikes(currentTrack.getLikes() - 1);
            client.toggleSongLike(String.valueOf(currentTrack.getId())); 

            isLiked = false;
            trackRepository.setTrackLiked(currentTrack.getId(), isLiked);
            updatePlaylistFragment();

            showCustomToast(getString(R.string.song_unliked));
        }
    }


    /**
     * Helper method to update the Playlist Fragment once a song is liked
     * Adds song to the default liked songs playlist
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

    /**
     * Releases and the ExoPlayer and sets it to null
     */
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