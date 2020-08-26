package tp.edu.sg.musicstream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    CircleImageView profile_pic;
    String image_url;

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        String image_url = getDefaults("image_url", getActivity());
        setHasOptionsMenu(true);// allow menu item to show
        Toolbar toolbar = rootView.findViewById(R.id.toolbar_with_profile_pic);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        profile_pic = toolbar.findViewById(R.id.profile_pic);
        if(image_url != null)
        {
            Picasso.with(getActivity()).load(image_url).into(profile_pic);
        }
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.tutorial)
        {
            Intent intent = new Intent(getActivity(), Tutorial.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
