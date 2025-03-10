package edu.commonwealthu.baywaves;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;


public class HomeFragment extends Fragment {

    private ExoPlayer exoPlayer;
    private ImageButton playPauseButton;
    private SeekBar seekBar;
    final private Handler handler = new Handler();
    private boolean isPlaying = false;
    private TextView startTime;
    private TextView endTime;
    private ImageView circularVisualizer;
    private CircleVisualizer circleVisualizer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);
        playPauseButton = view.findViewById(R.id.play_pause);
        seekBar = view.findViewById(R.id.seekBar);
        circularVisualizer = view.findViewById(R.id.circle_visualizer);


        /* Initialize the visualizer
        circleVisualizer = view.findViewById(R.id.visualizer);

        // Set custom color to the line
        circleVisualizer.setColor(ContextCompat.getColor(requireContext(), R.color.bayWave));

        // Customize the size of the circle (optional)
        circleVisualizer.setRadiusMultiplier(1.2f);

        // Set the line width for the visualizer (optional)
        circleVisualizer.setStrokeWidth(1);*/

        // Initialize ExoPlayer
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.aves_rollin));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();


      /*  exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    // Now we can set the audio session ID
                    circleVisualizer.setPlayer(exoPlayer.getAudioSessionId());
                }
            }
        }); */

        startTime.setText("0:00");
        endTime.setText(formatTime((int) exoPlayer.getDuration()));

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
