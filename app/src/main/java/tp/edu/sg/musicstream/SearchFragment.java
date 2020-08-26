package tp.edu.sg.musicstream;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import tp.edu.sg.musicstream.util.AppUtil;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    ImageView speechButton;
    EditText speechText;
    ArrayList<Integer> images = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> artists = new ArrayList<>();
    ArrayList<String> coverarts = new ArrayList<>();
    private ListView listView;
    private CustomAdapter customAdapter;
    ArrayList<Song> list = new ArrayList<>();
    private static final int recognizer_Result = 1; //global variable
    SongCollection songCollection = new SongCollection(list);
    ArrayList<Song> database = new ArrayList<>(songCollection.Songs);
    ArrayList<Song> databaseFull = new ArrayList<>(database);
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == recognizer_Result && resultCode == RESULT_OK)
        {
            ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); //store words into list
            speechText.setText(speech.get(0).toString()); //set the edit text to what the user says.
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        speechButton = view.findViewById(R.id.speechButton); //find the button
        speechText = view.findViewById(R.id.speechText); //find the edittext
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
        for (int index = 0; index < database.size(); index++) {
            coverarts.add(database.get(index).getCoverArt());
            titles.add(database.get(index).getTitle());
            artists.add(database.get(index).getArtist());
            images.add(AppUtil.getImageIdFromDrawable(getContext(), coverarts.get(index)));
        }
        customAdapter = new CustomAdapter();
        listView = view.findViewById(R.id.listViewSearch);
        listView.setAdapter(customAdapter);
        speechText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchFragment.this.customAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PlayOneSong.class);
                intent.putExtra("id", database.get(position).getId());
                intent.putExtra("title", database.get(position).getTitle());
                intent.putExtra("artist", database.get(position).getArtist());
                intent.putExtra("fileLink", database.get(position).getFileLink());
                intent.putExtra("coverArt", database.get(position).getCoverArt());
                startActivity(intent);
            }
        });

        return view;
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
                    images.add(AppUtil.getImageIdFromDrawable(getContext(), coverarts.get(index)));
                }
                notifyDataSetChanged();
            }
        };
    }


}
