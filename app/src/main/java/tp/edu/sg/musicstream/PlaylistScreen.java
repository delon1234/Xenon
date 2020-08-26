package tp.edu.sg.musicstream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import tp.edu.sg.musicstream.util.AppUtil;

public class PlaylistScreen extends AppCompatActivity {

    public static String no_of_playlists;
    ArrayList<String> playlist_num = new ArrayList<>();
    ArrayList<Integer> images = new ArrayList<>();
    ArrayList<String> playlist_name = new ArrayList<>();
    ArrayList<Song> list = new ArrayList<>();
    SongCollection songCollection = new SongCollection(list);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_screen);
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        no_of_playlists = sharedPreferences.getString("no_of_playlists", null);
        no_of_playlists = "3";
        for (int index = 1; index <= Integer.parseInt(no_of_playlists); index++)
        {
            String temp = Integer.toString(index);
            playlist_num.add(temp);
            loadPlaylist(temp);
        }
        playlist_name.add("JPOP");
        playlist_name.add("Hype");
        playlist_name.add("English Songs");
        PlaylistAdapter playlistAdapter = new PlaylistAdapter();
        ListView listView = findViewById(R.id.listViewPlaylist);
        ImageView backbutton = findViewById(R.id.backtolibrary);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView.setAdapter(playlistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saveData();
                Intent intent = new Intent(PlaylistScreen.this, MainActivity.class);
                intent.putExtra("playlist_num", playlist_num.get(position));
                intent.putExtra("playlist_name", playlist_name.get(position));
                startActivity(intent);
            }
        });
    }
    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("no_of_playlists", no_of_playlists);
        editor.commit();
    }
    private void loadPlaylist(String playlist_no) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Playlist " + playlist_no, null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        ArrayList<Song> temp = gson.fromJson(json, type);
        if (temp == null){
            temp = songCollection.Songs;
        }
        images.add(AppUtil.getImageIdFromDrawable(this, temp.get(0).getCoverArt()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("event", "toLibrary");
        startActivity(intent);
    }

    class PlaylistAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.playlistlayout, parent, false);
            final ImageView coverart = convertView.findViewById(R.id.coverart);
            TextView title = convertView.findViewById(R.id.title);
            coverart.setImageResource(images.get(position));
            title.setText(playlist_name.get(position));
            return convertView;
        }
    }
}