package tp.edu.sg.musicstream.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/*import sg.edu.tp.musicstream.Album;
import sg.edu.tp.musicstream.Song;*/

/**
 * Created by fjor on 14/2/17.
 */

public final class AppUtil
{
    public static void popMessage(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getResourceId(Context context, View view)
    {
        String id = context.getResources().getResourceEntryName(view.getId());

        return id;
    }

    public static int getImageIdFromDrawable(Context context, String imageName)
    {
        int imageID = context.getResources().getIdentifier(imageName,"drawable", context.getPackageName());

        return imageID;
    }

    public static Bitmap getBitmapFromAsset(Context context, String image)
    {
        AssetManager assetManager = context.getAssets();
        InputStream stream = null;

        try
        {
            stream = assetManager.open(image);

            return BitmapFactory.decodeStream(stream);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJsonFromAsset(String filename, Context context)
    {
        String json = null;

        try
        {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    /*    public static ArrayList<Song> getItemsFromFile(String filename, Context context)
    {
        final ArrayList<Song> songList = new ArrayList<>();

        try
        {
            // Load data
            String jsonString = loadJsonFromAsset(filename, context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray songs = json.getJSONArray("songs");

            // Get Song objects from data
            for (int i = 0; i < songs.length(); i++)
            {
                String title = songs.getJSONObject(i).getString("title");
                String artist = songs.getJSONObject(i).getString("artist");
                String albumName = songs.getJSONObject(i).getString("album");
                String fileLink = songs.getJSONObject(i).getString("fileLink");
                double price = songs.getJSONObject(i).getDouble("price");
                String coverArt = songs.getJSONObject(i).getString("coverArt");

                Album album = new Album(albumName, artist, price, coverArt);
                Song song = new Song(i + 1, title, artist, album, fileLink, price);

                songList.add(song);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return songList;
    }*/

/*    public static ArrayList<Object> getItemsFromFile(String filename, String rootTag, Context context)
    {
        final ArrayList<Object> itemList = new ArrayList<>();

        try
        {
            // Load data
            String jsonString = loadJsonFromAsset(filename, context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray items = json.getJSONArray(rootTag);

            // Get Recipe objects from data
            for (int i = 0; i < items.length(); i++)
            {
                String title = items.getJSONObject(i).getString("title");
                String artist = items.getJSONObject(i).getString("artist");
                String albumName = items.getJSONObject(i).getString("album");
                String fileLink = items.getJSONObject(i).getString("fileLink");
                double price = items.getJSONObject(i).getDouble("price");
                String coverArt = items.getJSONObject(i).getString("coverArt");

                Album album = new Album(albumName, artist, price, coverArt);
                Song song = new Song(i + 1, title, artist, album, fileLink, price);

                itemList.add(song);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return itemList;
    }*/
}