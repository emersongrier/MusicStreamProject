package edu.commonwealthu.baywaves;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private SeekBar seekBar;
    final private Handler handler = new Handler();
    private boolean isPlaying = false;
    private int lastPlaybackPosition = 0; // Save playback position
    private TextView startTime;
    private TextView endTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);

        // Initialize MediaPlayer
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.aves_rollin);
        }

        startTime.setText("0:00");
        endTime.setText(formatTime(mediaPlayer.getDuration()));


        // Restore playback position if available
        if (savedInstanceState != null) {
            lastPlaybackPosition = savedInstanceState.getInt("lastPlaybackPosition", 0);
            mediaPlayer.seekTo(lastPlaybackPosition);
            isPlaying = savedInstanceState.getBoolean("isPlaying", false);
        }

        // Find views inside play_screen.xml
        playPauseButton = view.findViewById(R.id.play_pause);
        seekBar = view.findViewById(R.id.seekBar);

        // Set Play/Pause button listener
        playPauseButton.setOnClickListener(v -> playAndPause());

        // Initialize SeekBar
        seekBar.setMax(100);
        updateSeekBar();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int newPosition = (int) ((progress / 100.0) * mediaPlayer.getDuration());
                    mediaPlayer.seekTo(newPosition);
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
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play_button);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause_button);
        }
        isPlaying = !isPlaying;
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    int progress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                    seekBar.setProgress(progress);

                    startTime.setText(formatTime(currentPosition));

                    // Update end time (remaining time)
                    int remainingTime = duration - currentPosition;
                    endTime.setText(formatTime(remainingTime));
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // Helper method to format time in mm:ss
    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        if(mediaPlayer.getDuration() >= 600000) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        else {
            return String.format("%01d:%02d", minutes, seconds);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mediaPlayer != null) {
            outState.putInt("lastPlaybackPosition", mediaPlayer.getCurrentPosition());
            outState.putBoolean("isPlaying", isPlaying);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            lastPlaybackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
