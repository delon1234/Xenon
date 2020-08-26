package tp.edu.sg.musicstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import tp.edu.sg.musicstream.HomeFragment;
import tp.edu.sg.musicstream.R;
import tp.edu.sg.musicstream.SearchFragment;
import tp.edu.sg.musicstream.VoiceControlFragment;


public class HomeScreen extends AppCompatActivity {

    //Bundle bundle;
    //String image_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        /*Intent intent = getIntent();
        if (intent.hasExtra("image_url"))
        {
            image_url = intent.getStringExtra("image_url");
        }
        bundle = new Bundle();
        bundle.putString("image_url", image_url);*/
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        // pass listener to bottomnav
        Intent intent = getIntent();
        if (intent.hasExtra("event")){
            String event = intent.getStringExtra("event");
            if (event.equals("toLibrary"))
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LibraryFragment()).commit();
            }
        }
        else
        {
            Fragment homeFragment = new HomeFragment();

            /*if (bundle != null){
            homeFragment.setArguments(bundle);}*/
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                homeFragment).commit(); //open fragment when app starts
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            //selectedFragment.setArguments(bundle);
                            break;
                        case R.id.voice_control:
                            selectedFragment = new VoiceControlFragment();
                            break;
                        case R.id.search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.library:
                            selectedFragment = new LibraryFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit(); // change the screen

                    return true;
                }
            };
}