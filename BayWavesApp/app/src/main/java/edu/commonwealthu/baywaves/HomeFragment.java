package edu.commonwealthu.baywaves;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class HomeFragment extends Fragment {

    private ExoPlayer exoPlayer;
    private ImageButton playPauseButton;
    private ImageButton nextSong;
    private ImageButton lastSong;
    private ImageButton likeSong;
    private SeekBar seekBar;
    final private Handler handler = new Handler();
    private boolean isPlaying = false;
    private boolean isLiked = false;
    private TextView startTime;
    private TextView endTime;
    private TextView songName;
    private TextView artistName;
    private ImageView circularVisualizer;

    private Track currentTrack;
    private TrackRepository trackRepository;
    private Artist currentArtist;
    private ArtistRepository artistRepository;

    private int currentTrackID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);
        songName = view.findViewById(R.id.title_text);
        playPauseButton = view.findViewById(R.id.play_pause);
        nextSong = view.findViewById(R.id.skip_button);
        lastSong = view.findViewById(R.id.unskip_button);
        likeSong = view.findViewById(R.id.like_song);
        seekBar = view.findViewById(R.id.seekBar);
        circularVisualizer = view.findViewById(R.id.circle_visualizer);
        currentTrackID = 0;

        // Initialize TrackRepository
        artistRepository = ArtistRepository.getInstance();
        trackRepository = TrackRepository.getInstance();
        boolean isConnected = trackRepository.isDatabaseConnected();
        String connectionMessage = trackRepository.getConnectionErrorMessage();

        // Load the first track from the repository
        loadTrack(trackRepository.getAllTracks().get(currentTrackID));

        // Setup play/pause button
        playPauseButton.setOnClickListener(v -> playAndPause());

        nextSong.setOnClickListener(v -> {
            List<Track> tracks = trackRepository.getAllTracks();
            if (currentTrackID < tracks.size() - 1) {
                currentTrackID++;
                Track newTrack = tracks.get(currentTrackID);
                loadTrack(newTrack);
                playAndPause();
            }
        });

        lastSong.setOnClickListener(v -> {
            List<Track> tracks = trackRepository.getAllTracks();
            if (currentTrackID > 0) {
                currentTrackID--;
                Track newTrack = tracks.get(currentTrackID);
                loadTrack(newTrack);
                playAndPause();
            }
        });

        // Setup like button
        likeSong.setOnClickListener(v -> addSong());

        showCustomToast(connectionMessage);

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

    private void loadTrack(Track track) {
        // Update current track
        currentTrack = track;

        // Set up ExoPlayer with the track's file path
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(track.getFilePath()));

        // Release any existing player
        if (exoPlayer != null) {
            exoPlayer.release();
        }

        // Create new ExoPlayer instance
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();

        // Update UI elements with track metadata
        updateTrackMetadata(track);

        // Reset like state
        isLiked = track.getLikes() > 0 || track.isLocalLikedState();
        updateLikeButton();
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

        songName.setText(track.getName());
        //artistName.setText();
    }

    private void updateLikeButton() {
        Glide.with(requireContext())
                .load(isLiked ? R.drawable.liked_song : R.drawable.like_song)
                .dontAnimate()
                .into(likeSong);
    }

    // Method to change track (you can call this when you want to switch tracks)
    public void changeNextTrack(int trackId) {
        List<Track> tracks = trackRepository.getAllTracks();
        if (currentTrackID < tracks.size() - 1) {
            currentTrackID++;
            Track newTrack = tracks.get(currentTrackID);
            loadTrack(newTrack);
            playAndPause();
        }
    }

    // Method to change track (you can call this when you want to switch tracks)
    public void changeLastTrack(int trackId) {
        Track newTrack = trackRepository.getTrackById(trackId);
        if (newTrack != null && currentTrackID > 0) {
            loadTrack(newTrack);
            playAndPause(); // Start playing the new track
        }
    }

    private void playAndPause() {
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
    }


    private void addSong() {
        currentTrack.setLocalLikedState(isLiked);
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
                                    trackRepository.updateTrack(currentTrack);

                                    // Change to liked state image
                                    Glide.with(requireContext())
                                            .load(R.drawable.liked_song)
                                            .dontAnimate()
                                            .into(likeSong);

                                    isLiked = true;
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
            trackRepository.updateTrack(currentTrack);

            isLiked = false;
            showCustomToast(getString(R.string.song_unliked));
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