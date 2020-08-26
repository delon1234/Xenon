package tp.edu.sg.musicstream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class SongCollection
{

    public ArrayList<Song> Songs = new ArrayList<Song>();
    // use ArrayList instead of array to allow for adding of song into songCollection
    // as array is of a fixed size but ArrayList size is dynamic.
    public SongCollection(ArrayList<Song> updatedSongs) //default constructor
    {

        if ((updatedSongs!= null) && !updatedSongs.isEmpty())
        {
            updateSongs(updatedSongs);
        }
        else
        {
            prepareSongs();
        }

    }

    public Song searchByTitle(String title)
    {
        //1. create a temp Song object call song and set it null
        Song song = null;
        //2. Starting from index 0 of songs arraylist to last index, loop through
        // every song item. Increment index by 1 after every iteration.
        // so that it goes to the next item until the last item.
        for (int index = 0; index < Songs.size(); index++)
        {
            //3.  Store each song to the temporary song object
            song = Songs.get(index);

            //4. Compare each song title to the title that we want to find.
            // If the titles are equal, return this as the result.
            if (song.getTitle().equals(title))
            {
                return song;
            }
        }
        // if song object cannot be found in the arraylist of songs, return null song object
        return song;
    }
    private void prepareSongs() // song name is based on _coverArt
    {
        Song michael_buble_collection = new Song("s1001",
                "The Way You Look Tonight",
                "Michael Buble",
                "a5b8972e764025020625bbf9c1c2bbb06e394a60?cid=2afe87a64b0042dabf51f37318616965",
                4.66,
                "michael_buble_collection");
        Song billie_jean = new Song("s1002",
                "Billie Jean",
                "Michael Jackson",
                "fdfa0fe346a16b6aeb8136e6d2b7e11cacc6a06d?cid=2afe87a64b0042dabf51f37318616965",
                4.9,
                "billie_jean");
        Song kiss_of_death = new Song("s1003",
                "Kiss of Death (Produced by Hyde)", "Mika Nakashima",
                "c04b1a8b73dae51622c368084a45452c5c6855e1?cid=2afe87a64b0042dabf51f37318616965",
                4.14, "kiss_of_death");
        Song the_bravery = new Song("s1004", "The Bravery", "supercell",
                "a78a5329c63d76b7ced54a67ab6b31c6ac606c1c?cid=2afe87a64b0042dabf51f37318616965",
                6.11, "the_bravery");
        Song death_of_a_bachelor = new Song("s1005", "Death of a Bachelor", "Panic! At The Disco",
                "1f5f24f8f0dfa6d3b7baea9c5fa9c45278abbe35?cid=2afe87a64b0042dabf51f37318616965",
                2.98,"death_of_a_bachelor");
        Songs.add(michael_buble_collection);
        Songs.add(billie_jean);
        Songs.add(kiss_of_death);
        Songs.add(the_bravery);
        Songs.add(death_of_a_bachelor);

    }
    public Song getNextSong(String currentSongId)
    {
        //1. Create a temporary Song object called song and set it to null.
        Song song = null;

        //2. Starting from index 0 of the song arraylist to the last one,
        // loop through every song item. Increment the index by one after every loop.
        // so that the system knows how to go to the next item until the last one.
        for (int index = 0; index < Songs.size(); index++)
        {
            //3. Create another temporary String variable and name it tempSongId,
            // and assign the ID of each song item to tempSongId.
            String tempSongId = Songs.get(index).getId();

            //4. Compare the song ID in tempSong with the current song ID using the equals()
            // method and check the current index value. If the ID in tempSong equals to the current song
            // and the index value is less than the last item of songs arraylist
            if (tempSongId.equals(currentSongId) && (index < Songs.size() -1))
            {
                //1. Assign the next item in Songs array to the song variable.
                song = Songs.get(index + 1);

                //2. Break and exit the loop.
                break;
            }

        }

        //5. Return the song.
        return song;
    }
    public Song getPreviousSong(String currentSongId)
    {
        //1. Create a temporary Song object called song and set it to null.
        Song song = null;

        //2. Starting from index 0 of the song arraylist to the last one,
        // loop through every song item. Increment the index by one after every loop.
        // so that the system knows how to go to the next item until the last one.
        for (int index = 0; index < Songs.size(); index++)
        {
            //3. Create another temporary String variable and name it tempSongId,
            // and assign the ID of each song item to tempSongId.
            String tempSongId = Songs.get(index).getId();

            //4. Compare the song ID in tempSong with the current song ID using the equals()
            // method and check the current index value. If the ID in tempSong equals to the current song
            // and the index value is less than the last item of songs arraylist
            if (tempSongId.equals(currentSongId) && (index > 0))
            {
                //1. Assign the next item in Songs arraylist to the song variable.
                song = Songs.get(index - 1);

                //2. Break and exit the loop.
                break;
            }

        }

        //5. Return the song.
        return song;
    }
    public void deleteSong(String songTitle) // to remove while iterating Arraylist to avoid ConcurrentModfiicationException
    {
        Iterator<Song> itr = Songs.iterator();
        while (itr.hasNext()) {
            String tempSongTitle = itr.next().getCoverArt(); // i set the name of song to be coverart.
            if (tempSongTitle.equals(songTitle))
            {
                itr.remove();
                break; // break out of the loop to be more efficient
            }
        }
    }
    public void addSong(ArrayList<Song> selectedSongList)
    {
        Songs.addAll(selectedSongList);
    }

    public void updateSongs(ArrayList<Song> updatedSongs)
    {
        Songs = updatedSongs;
    }

}
