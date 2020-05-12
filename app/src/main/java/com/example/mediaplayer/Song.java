package com.example.mediaplayer;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String path;
    private String duration;
    private int album_id;
    private String artist;

    public Song(String title, String path, String duration, int album_id, String artist) {
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.album_id = album_id;
        this.artist = artist;
    }

    protected Song(Parcel in) {
        title = in.readString();
        path = in.readString();
        duration = in.readString();
        album_id = in .readInt();
        artist = in.readString();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeString(duration);
        parcel.writeInt(album_id);
        parcel.writeString(artist);
    }
}
