package edu.commonwealthu.baywaves;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.commonwealthu.baywaves.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Use add() and show()/hide() instead of replace() to keep fragment state
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

        for (Fragment frag : fragmentManager.getFragments()) {
            fragmentTransaction.hide(frag); // Hide all fragments first
        }

        if (existingFragment != null) {
            fragmentTransaction.show(existingFragment);
        } else {
            fragmentTransaction.add(R.id.frame_layout, fragment, fragment.getClass().getSimpleName());
        }

        fragmentTransaction.commit();
    }

}