package tp.edu.sg.musicstream;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String id;
    private String title;
    private String artist;
    private String fileLink;
    private double songLength;    //attributes
    private String coverArt;

    public Song(String _id, String _title, String _artist, String _fileLink, double _songLength, String _coverArt)
    {
        id = _id;
        title = _title;
        artist = _artist;         // constructor
        fileLink = _fileLink;
        songLength = _songLength;
        coverArt = _coverArt;
    }


    protected Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        fileLink = in.readString();
        songLength = in.readDouble();
        coverArt = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

    public String getFileLink() { return fileLink; }

    public void setFileLink(String fileLink) { this.fileLink = fileLink; }

    public double getSongLength() { return songLength; }

    public void setSongLength(double songLength) { this.songLength = songLength; }

    public String getCoverArt() { return coverArt; }

    public void setCoverArt(String coverArt) { this.coverArt = coverArt; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(fileLink);
        dest.writeDouble(songLength);
        dest.writeString(coverArt);
    }
}
