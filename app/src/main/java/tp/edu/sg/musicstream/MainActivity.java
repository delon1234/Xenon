package tp.edu.sg.musicstream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tp.edu.sg.musicstream.util.AppUtil;

public class MainActivity extends AppCompatActivity
{

    ArrayList<Song> list = new ArrayList<>();
    SongCollection test = new SongCollection(list);
    ArrayList<Song> normal = test.Songs;
    public SongCollection songCollection;
    ArrayList<Integer> images = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> artists = new ArrayList<>();
    ArrayList<String> coverarts = new ArrayList<>();
    private ListView listView;
    private CustomAdapter customAdapter;
    String playlist_num;
    public static boolean isActionMode = false;
    public static List<String> userSelection = new ArrayList<>();
    public static ActionMode actionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView backbutton = findViewById(R.id.backtoplaylists);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaylistScreen.class);
                saveData(playlist_num);
                startActivity(intent);
            }
        });
        ImageView clickaddSong = findViewById(R.id.clickaddSong);
        clickaddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSongScreen.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("playlist", songCollection.Songs);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });
        songCollection = new SongCollection(list);
        normal = new ArrayList<>(songCollection.Songs);
        playlist_num = getIntent().getStringExtra("playlist_num");
        loadData(playlist_num);
        TextView playlist_name = findViewById(R.id.playlist_name);
        playlist_name.setText(getIntent().getStringExtra("playlist_name"));
        int size = songCollection.Songs.size();
        listView = findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(modeListener);
        for (int index = 0; index < size; index++)
        {
            coverarts.add(songCollection.Songs.get(index).getCoverArt());
            titles.add(songCollection.Songs.get(index).getTitle());
            artists.add(songCollection.Songs.get(index).getArtist());
            images.add(AppUtil.getImageIdFromDrawable(this, coverarts.get(index)));
        }
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song selectedSong = songCollection.searchByTitle(titles.get(position));
                sendDataToActivity(selectedSong, songCollection.Songs);
            }
        });

    }
    private void saveData(String number){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(songCollection.Songs);
        editor.putString("Playlist " + number, json);
        editor.apply();
    }
    private void loadData(String number) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Playlist " + number, null);
        Type type = new TypeToken<ArrayList<Song>>() {}.getType();
        ArrayList<Song> temp = gson.fromJson(json, type);
        if (temp != null)
        {
            songCollection.Songs = temp;
        }
    }
    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater(); //when enter into contextual action mode
            inflater.inflate(R.menu.menudeletesong, menu);
            isActionMode = true;
            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId() == R.id.deletesong)
            {
                customAdapter.removeSongs(userSelection);
                mode.finish();
                return true;
            }
            else
            {
                return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isActionMode = false; //when exit contextual action mode
            actionMode = null;
            userSelection.clear();
        }
    };


    public void sendDataToActivity(Song song, ArrayList<Song> updatedSongs)
    {
        //1. Create a new Intent and specify the source and destination screen/activity
        Intent intent = new Intent(this, PlaySongActivity.class); //create new instance
        //2. Store the song information into the Intent object  to be sent over to destination
        // screen.
        intent.putExtra("id", song.getId());
        intent.putExtra("title", song.getTitle());
        intent.putExtra("artist", song.getArtist());
        intent.putExtra("fileLink", song.getFileLink());
        intent.putExtra("coverArt", song.getCoverArt());
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("arraylist", updatedSongs);
        intent.putExtras(bundle);
        saveData(playlist_num);
        //3. Launch the destination screen/activity
        startActivityForResult(intent, 1);
    }

    class CustomAdapter extends BaseAdapter
    {

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
            convertView = getLayoutInflater().inflate(R.layout.customlayout, parent, false);
            final ImageView coverart = convertView.findViewById(R.id.coverart);
            TextView title = convertView.findViewById(R.id.title);
            TextView artist = convertView.findViewById(R.id.artist);
            coverart.setImageResource(images.get(position));
            title.setText(titles.get(position));
            artist.setText(artists.get(position));
            CheckBox checkBox = convertView.findViewById(R.id.checkbox);
            checkBox.setTag(position); //keep track of which checkbox the user selects
            if(isActionMode)
            {
                checkBox.setVisibility(View.VISIBLE);
            }
            else
            {
                checkBox.setVisibility(View.GONE);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (int)buttonView.getTag();

                    if(userSelection.contains(coverarts.get(position)))
                    {
                        userSelection.remove(coverarts.get(position));
                        userSelection.remove(String.valueOf(images.get(position)));
                        userSelection.remove(titles.get(position));
                        userSelection.remove(artists.get(position));
                    }
                    else
                    {
                        userSelection.add(coverarts.get(position));
                        userSelection.add(String.valueOf(images.get(position)));
                        userSelection.add(titles.get(position));
                        userSelection.add(artists.get(position));
                    }
                    actionMode.setTitle(userSelection.size()/4+" songs selected...");
                }
            });
            return convertView;
        }
        public void removeSongs(List<String> songs)
        {
            int iteration = 0;
            for (int index = 0; index < songs.size(); index++)
            {
                String song = songs.get(index);
                if (index == iteration * 4)
                {
                    Iterator<String> itr = coverarts.iterator();
                    while (itr.hasNext()) {
                        String temp = itr.next();
                        if (temp.equals(song))
                        {
                            itr.remove();
                            break; // break out of the loop to be more efficient
                        }
                    }
                    songCollection.deleteSong(song);
                    continue;
                }
                if (index == iteration * 4 + 1) {
                    Iterator<Integer> itr1 = images.iterator();
                    int imageid = Integer.parseInt(song);
                    while (itr1.hasNext()) {
                        Integer temp = itr1.next();
                        if (temp.equals(imageid)) {
                            itr1.remove();
                            break; // break out of the loop to be more efficient
                        }
                    }
                    continue;
                }
                if (index == iteration * 4 + 2) {
                    Iterator<String> itr2 = titles.iterator();
                    while (itr2.hasNext()) {
                        String temp = itr2.next();
                        if (temp.equals(song)) {
                            itr2.remove();
                            break; // break out of the loop to be more efficient
                        }
                    }
                    continue;
                }
                if (index == iteration * 4 + 3)
                {
                    Iterator<String> itr3 = artists.iterator();
                    while (itr3.hasNext()) {
                        String temp = itr3.next();
                        if (temp.equals(song))
                        {
                            itr3.remove();
                            break; // break out of the loop to be more efficient
                        }
                    }
                    iteration = iteration + 1;
                }
            }
            notifyDataSetChanged();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                /*Bundle updatedSongs = data.getExtras();
                list = updatedSongs.getParcelableArrayList("updatedArraylist");
                songCollection.updateSongs(list);*/
                loadData(playlist_num);
                int size = songCollection.Songs.size();
                coverarts.clear();
                titles.clear();
                artists.clear();
                images.clear();
                for (int index = 0; index < size; index++)
                {
                    coverarts.add(songCollection.Songs.get(index).getCoverArt());
                    titles.add(songCollection.Songs.get(index).getTitle());
                    artists.add(songCollection.Songs.get(index).getArtist());
                    images.add(AppUtil.getImageIdFromDrawable(this, coverarts.get(index)));
                }
                customAdapter.notifyDataSetChanged();

            }
        }
        if(requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle updatedSongs = data.getExtras();
                list = updatedSongs.getParcelableArrayList("updatedArraylist");
                songCollection.updateSongs(list);
                int size = songCollection.Songs.size();
                coverarts.clear();
                titles.clear();
                artists.clear();
                images.clear();
                for (int index = 0; index < size; index++)
                {
                    coverarts.add(songCollection.Songs.get(index).getCoverArt());
                    titles.add(songCollection.Songs.get(index).getTitle());
                    artists.add(songCollection.Songs.get(index).getArtist());
                    images.add(AppUtil.getImageIdFromDrawable(this, coverarts.get(index)));
                }
                customAdapter.notifyDataSetChanged();
            }
        }
    }
}
