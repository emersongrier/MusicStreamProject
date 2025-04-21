package edu.commonwealthu.baywaves;

import android.widget.ImageView;

import java.util.List;

public class Album {

    private int id;
    private String type;
    private String name;
    private ImageView cover;
    private List<Track> tracks;
    private int likes;
    private int artistId;

    public Album(int id, String type, String name, ImageView cover, List<Track> tracks, int likes, int artistId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.cover = cover;
        this.tracks = tracks;
        this.likes = likes;
        this.artistId = artistId;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public void setType(String type) { this.type = type; }
    public String getType() { return type; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void setCover(ImageView cover) { this.cover = cover; }
    public ImageView getCover() { return cover; }
    public void setTracks(List<Track> tracks) { this.tracks = tracks; }
    public List<Track> getTracks() { return tracks; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getLikes() { return likes; }
    public void setArtistId(int artistId) { this.artistId = artistId; }
    public int getArtistId() { return artistId; }


    public void addSong(Track song) {
        this.tracks.add(song);
    }

    public void removeSong(Track song) {
        this.tracks.remove(song);
    }



}
