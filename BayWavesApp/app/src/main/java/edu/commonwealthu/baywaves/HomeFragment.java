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


public class HomeFragment extends Fragment {

    private ExoPlayer exoPlayer;
    private ImageButton playPauseButton;
    private ImageButton likeSong;
    private SeekBar seekBar;
    final private Handler handler = new Handler();
    private boolean isPlaying = false;
    private boolean isLiked = false;
    private TextView startTime;
    private TextView endTime;
    private ImageView circularVisualizer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);
        playPauseButton = view.findViewById(R.id.play_pause);
        likeSong = view.findViewById(R.id.like_song);
        seekBar = view.findViewById(R.id.seekBar);
        circularVisualizer = view.findViewById(R.id.circle_visualizer);



        // Initialize ExoPlayer
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.aves_rollin));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();


        startTime.setText("0:00");
        endTime.setText(formatTime((int) exoPlayer.getDuration()));

        Glide.with(requireContext())
                .load(R.drawable.like_song) // or use a still image version if available
                .dontAnimate()
                .into(likeSong);

        likeSong.setOnClickListener(v -> addSong());

        playPauseButton.setOnClickListener(v -> playAndPause());
        seekBar.setMax(100);
        updateSeekBar();

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


    private void playAndPause() {
        if (isPlaying) {
            exoPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play_button);
            Glide.with(requireContext())
                    .load(R.drawable.media_playing) // or use a still image version if available
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
                                    // Change to liked state image
                                    Glide.with(requireContext())
                                            .load(R.drawable.liked_song)
                                            .dontAnimate()
                                            .into(likeSong);
                                }
                            });
                            return false;
                        }
                    })
                    .into(likeSong);

            isLiked = true;
            showCustomToast(getString(R.string.song_liked));
        } else {
            // Reset to original state
            Glide.with(requireContext())
                    .load(R.drawable.like_song)
                    .dontAnimate()
                    .into(likeSong);

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
        return (exoPlayer.getDuration() >= 600000) ? String.format("%02d:%02d", minutes, seconds) : String.format("%01d:%02d", minutes, seconds);
    }

    /**
     * Displays a given string in a custom toast.
     * @param message The message to be displayed with the toast
     */
    private void showCustomToast(String message) {
        if (getContext() == null) return; // Prevent crashes if Fragment is detached

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 0, 200); // Position similar to Spotify
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
