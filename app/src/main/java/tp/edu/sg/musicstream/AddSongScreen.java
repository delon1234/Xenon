package tp.edu.sg.musicstream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import tp.edu.sg.musicstream.Song;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tp.edu.sg.musicstream.util.AppUtil;

public class AddSongScreen extends AppCompatActivity {
    ArrayList<Song> list = new ArrayList<>();
    SongCollection songCollection;
    ArrayList<Song> database;
    ArrayList<Song> databaseFull;
    public static boolean isActionMode = false;
    public static List<String> userSelection = new ArrayList<>();
    public static ActionMode actionMode = null;
    ArrayList<Integer> images = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> artists = new ArrayList<>();
    ArrayList<String> coverarts = new ArrayList<>();
    private ListView listView;
    private CustomAdapter customAdapter;
    Button addSongsButton;
    ImageView speechButton;
    EditText speechText;
    private static final int recognizer_Result = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_screen);
        ImageView backbutton = findViewById(R.id.backtoplaylist);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        speechButton = findViewById(R.id.voice_search_add_songs); //find the button
        speechText = findViewById(R.id.speechText); //find the edittext
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // when speechButton is clicked, connect to google api.
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                // EXTRA_LANGUAGE_MODEL informs recognizer which speech model to prefer when performing ACTION_RECOGNIZE_SPEECH
                // LANGUAGE_MODEL_FREE_FORM use free-form speech recognition language model
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech To Text."); // set the msg
                startActivityForResult(speechIntent,recognizer_Result);
            }
        });
        songCollection = new SongCollection(list);
        database = songCollection.Songs; // song arraylist for listview that would get updated
        databaseFull = new ArrayList<>(database); // contains all songs
        Bundle dataFromPlaylist = getIntent().getExtras();
        list = dataFromPlaylist.getParcelableArrayList("playlist");
        songCollection.updateSongs(list);
        addSongsButton = findViewById(R.id.addSongs);
        listView = findViewById(R.id.listView);
        int size = database.size();
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        for (int index = 0; index < size; index++) {
            coverarts.add(database.get(index).getCoverArt());
            titles.add(database.get(index).getTitle());
            artists.add(database.get(index).getArtist());
            images.add(AppUtil.getImageIdFromDrawable(this, coverarts.get(index)));
        }
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        addSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSongs(userSelection);
                userSelection.clear();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("updatedArraylist", songCollection.Songs);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        speechText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddSongScreen.this.customAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == recognizer_Result && resultCode == RESULT_OK) {
            ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); //store words into list
            speechText.setText(speech.get(0).toString()); //set the edit text to what the user says.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    class CustomAdapter extends BaseAdapter implements Filterable
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
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (int)buttonView.getTag();

                    if(userSelection.contains(coverarts.get(position)))
                    {
                        userSelection.remove(coverarts.get(position));
                    }
                    else
                    {
                        userSelection.add(coverarts.get(position));
                    }
                    String noSelected = Integer.toString(userSelection.size());
                    String string = "Add" + noSelected + " songs...";
                    addSongsButton.setText(string);
                }
            });
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return databaseFilter;
        }
        private Filter databaseFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Song> filteredList = new ArrayList<>();
                if(constraint == null || constraint.length() == 0)
                {
                    filteredList.addAll(databaseFull);
                }
                else
                {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Song song:databaseFull){
                        if (song.getTitle().toLowerCase().contains(filterPattern) || song.getArtist().toLowerCase().contains(filterPattern)) {
                            filteredList.add(song);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                database.clear();
                coverarts.clear();
                titles.clear();
                artists.clear();
                images.clear();
                database.addAll((ArrayList) results.values);
                for (int index = 0; index < database.size(); index++) {
                    coverarts.add(database.get(index).getCoverArt());
                    titles.add(database.get(index).getTitle());
                    artists.add(database.get(index).getArtist());
                    images.add(AppUtil.getImageIdFromDrawable(getApplicationContext(), coverarts.get(index)));
                }
                notifyDataSetChanged();
            }
        };
    }
    public void addSongs(List<String> songs)
    {
        for (int index = 0; index < songs.size(); index++) // loop thru userSelection
        {
            String song = songs.get(index);
            for (int i = 0; i < database.size(); i++) {
                String tempSongTitle = database.get(i).getCoverArt();
                if (tempSongTitle.equals(song)) {
                    songCollection.Songs.add(database.get(i));
                    break;
                }
            }
        }
    }

}