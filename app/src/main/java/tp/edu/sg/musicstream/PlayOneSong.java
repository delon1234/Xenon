package tp.edu.sg.musicstream;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tp.edu.sg.musicstream.util.AppUtil;

public class PlayOneSong extends AppCompatActivity
{
    // This is the constant variable that contains the website URL
    // where we will stream the music.
    private static final String BASE_URL = "https://p.scdn.co/mp3-preview/";

    // These variables are the song information that we will be
    // using in the codes here.
    private String songId = "";
    private String title = "";
    private String artist = "";
    private String fileLink = "";
    private String coverArt = "";
    private String url = "";

    private double startTime;
    private double finalTime;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = ""; // stores what the user says
    private Boolean voice_enabled = false;
    // This is the built-in MediaPlayer object that we will use
    // to play the music
    private MediaPlayer player = null;
    // This is the position of the song in playback.
    // we set it to 0 so that it starts at the beginning.
    private int musicPosition = 0;
    // This Button variable is created to link to the Play button
    // at the playback screen. We need to do this because it will act as both
    // Play and Pause button.
    private Button btnPlayPause = null;

    // we declare at class lvl as it will be used throughout the methods.
    private SeekBar seekBarMP = null;
    private Handler seekBarUpdateHandler = new Handler(); // event handler
    private TextView currentSongPosition,maxSongDuration;
    private Runnable updateSeekBar = new Runnable()
    {
        @Override
        public void run()
        {
            seekBarMP.setProgress(player.getCurrentPosition());
            seekBarUpdateHandler.postDelayed(this, 50); // updates the seek every 50ms
            startTime = player.getCurrentPosition();
            currentSongPosition.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))); // set currentSongPosition and update it.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) // onCreate method is called when you switch screens
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_one_song);
        checkVoiceCommandPermission();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(PlayOneSong.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null) // user said smth
                {
                    keeper = matchesFound.get(0); // get the most accurate text that the user said
                    //display what command has been received.
                    Toast.makeText(PlayOneSong.this, "Result:" + keeper, Toast.LENGTH_LONG).show();
                    keeper = keeper.toLowerCase();
                    if (keeper.equals("pause song") || keeper.equals("pause music") || keeper.equals("stop")){
                        pauseMusic();
                    }
                    if (keeper.equals("play song") || keeper.equals("play music") || keeper.equals("continue")){
                        playOrPauseMusic(null);
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        ImageView voiceButton = findViewById(R.id.voiceButton);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!voice_enabled)
                {
                    speechRecognizer.startListening(speechRecognizerIntent);
                    keeper = "";
                    voice_enabled = true;
                }
                else
                {
                    speechRecognizer.stopListening();
                    voice_enabled = false;
                }
            }
        });
        btnPlayPause = findViewById(R.id.btnPlayPause);
        seekBarMP = findViewById(R.id.seekBar); // ref seekbar in class to seekBar in ui
        retrieveData();
        displaySong(title, artist, coverArt);
        Toolbar toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar); // allow toolbar to have options menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
        getSupportActionBar().setTitle("Playing song"); // set title
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        currentSongPosition = findViewById(R.id.currentSongPosition);
        maxSongDuration = findViewById(R.id.maxSongDuration);
        initialPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.add_to_playlist:
            // switch to add playlist Screen
            case R.id.share: // when share is clicked
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied text", url); // copy url and store into clip
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show(); // show msg copied to clipboard
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        stopActivities();
        super.onBackPressed();
    }
    private void checkVoiceCommandPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //check version above api ? as it requires permission
        {
            if (!(ContextCompat.checkSelfPermission(PlayOneSong.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                //if permission not granted go to permissions page
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
    private void initialPlay()
    {
        preparePlayer();
        player.start();
        seekBarMP.setMax(player.getDuration()); //set max duration
        seekBarUpdateHandler.postDelayed(updateSeekBar, 0);
        seekBarMP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    player.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void retrieveData() // we know what song the user press in previous screen
    {
        // we retrieve data stored in intent and store it in a bundle called songData
        Bundle songData = getIntent().getExtras();
        // we retreive each individual data associated with the key from songData where the data used to be in intent
        songId = songData.getString("id");
        title = songData.getString("title");
        artist = songData.getString("artist");
        fileLink = songData.getString("fileLink");
        coverArt = songData.getString("coverArt");

        url = BASE_URL + fileLink; // note that base url for all songs are the same
    }
    private void displaySong(String title, String artist, String coverArt)
    {
        // This is to retrieve the song title TextView from the UI Screen.
        TextView txtTitle = findViewById(R.id.txtSongTitle);

        // This is to set the text of the song title TextView to the selected title.
        txtTitle.setText(title);

        // This is to retrieve the artist TextView from the UI screen.
        TextView txtArtist = findViewById(R.id.txtArtist);

        // This is to set the text of the artist TextView to the selected artist name.
        txtArtist.setText(artist);

        // This is to get the ID of the cover art from the drawable folder.
        int imageId = AppUtil.getImageIdFromDrawable(this, coverArt);

        // This is to retrieve the cover art ImageView from the UI Screen.
        ImageView ivCoverArt = findViewById(R.id.imgCoverArt);

        //This is to set the selected cover art image to the ImageView in the screen.
        ivCoverArt.setImageResource(imageId);
    }
    private void preparePlayer()
    {
        //1. Create a new MediaPlayer
        player = new MediaPlayer();

        // The try and catch codes are required by the prepare() method.
        // It is to catch any error that may occur and to handle the error.
        // error is printed out to the console using the method printStackTrace().
        try
        {
            //2. This sets the Audio Stream Type to music streaming
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //3. Sets the source of the music.
            // For example, the url for Billie Jean will look like:
            // https://p.scdn.co/mp3-preview/4eb779428d40d579f14d12a9daf98fc66c7d0be4?cid=null
            player.setDataSource(url);

            //4. Prepare the player for playback.
            player.prepare();
            //5. Set maxSongDuration
            finalTime = player.getDuration();
            maxSongDuration.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    finalTime))));
            //6. check if loop is on, if loop is on setlooping to true
            /*if (repeat)
            {
                player.setLooping(true);
            }*/
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void playOrPauseMusic(View view)
    {
        //1. If no MediaPlayer object is created, call
        // preparePlayer method to create MediaPlayer.
        if (player == null)
        {
            preparePlayer();
        }
        //2. If the player is not playing
        if (!player.isPlaying())
        {
            // 1. If the position of the music is greater than 0
            if (musicPosition > 0)
            {
                //1. Get the player to go to the music position
                player.seekTo(musicPosition);
            }

            //2. Start the player
            player.start();

            //3. Set the text of the play button to "PAUSE"
            btnPlayPause.setText("PAUSE");
            // I need to change the image to pause button
            //4. Set the heading title of the app to the music that is currently playing.
            //setTitle("Now Playing: " + title + " = " + artist);
            seekBarMP.setMax(player.getDuration()); //set max duration
            seekBarUpdateHandler.postDelayed(updateSeekBar, 0); //call this handler when
            // audio starts to play
            // this part only applicable when music is playing.
            seekBarMP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        player.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //5. When the music ends, stop the player.
            gracefullyStopWhenMusicEnds();
        }
        else
        {
            //3. Pause the music.
            pauseMusic();
        }
    }
    private void pauseMusic()
    {
        //1. Pause the player.
        player.pause();
        seekBarUpdateHandler.removeCallbacks(updateSeekBar); // call this when music stops/pauses
        //2. Get the current position of the music that is playing.
        musicPosition = player.getCurrentPosition();
        //3. Set the text on the button back to "PLAY"
        btnPlayPause.setText("PLAY");
        // I need to change this to a image of play button
    }
    private void gracefullyStopWhenMusicEnds()
    {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                stopActivities();
            }
        });
    }
    private void stopActivities()
    {
        //1. If MediaPlayer object exists
        if (player != null)
        {
            //2. Set the text on the button back to "PLAY"
            btnPlayPause.setText("PLAY");
            //3. Set music position to 0
            musicPosition = 0;
            //5. Stop the music player
            player.stop();
            //6. Release resource for the music player
            player.release();
            //7. Set player to null
            player = null;
            seekBarUpdateHandler.removeCallbacks(updateSeekBar);
            // whenever pause/stop music need to remove callbacks.
        }
    }
}