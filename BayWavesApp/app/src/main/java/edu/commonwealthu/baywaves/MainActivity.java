package edu.commonwealthu.baywaves;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.commonwealthu.baywaves.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            splashScreen.setKeepOnScreenCondition(() -> false);
        }, 3000);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home){
                replaceFragment(new HomeFragment());
            }
            else if(item.getItemId() == R.id.search){
                replaceFragment(new SearchFragment());
            }
            else if(item.getItemId() == R.id.playlists){
                replaceFragment(new PlaylistFragment());
            }

            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment existingFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

        for (Fragment frag : fragmentManager.getFragments()) {
            fragmentTransaction.hide(frag);
        }

        if (existingFragment != null) {
            fragmentTransaction.show(existingFragment);
        } else {
            fragmentTransaction.add(R.id.frame_layout, fragment, fragment.getClass().getSimpleName());
        }

        fragmentTransaction.commit();
    }

}