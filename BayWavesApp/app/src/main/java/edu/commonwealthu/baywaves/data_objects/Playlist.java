package edu.commonwealthu.baywaves.data_objects;

import java.util.List;

public class Playlist {

    private int id;
    private String name;
    private String description;
    private String cover;
    private int followers;
    private int userId;
    private List<Track> songs;

    public Playlist(int id, String name, String description, String cover, int followers, int userId, List<Track> songs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cover = cover;
        this.followers = followers;
        this.userId = userId;
        this.songs = songs;
    }

    public void setId(int id) { this.id = id; }
    public int getId() {return id;}
    public void setName(String name) { this.name = name; }
    public String getName() {return name;}
    public void setDescription(String description) { this.description = description; }
    public String getDescription() {return description;}
    public void setCover(String cover) { this.cover = cover; }
    public String getCover() {return cover;}
    public void setFollowers(int followers) { this.followers = followers; }
    public int getFollowers() {return followers;}
    public void setUserId(int userId) { this.userId = userId; }
    public int getUserId() {return userId;}
    public void setSongs(List<Track> songs) {this.songs = songs;}
    public List<Track> getSongs() {return songs;}

    public void addSong(Track song) {
        this.songs.add(song);
    }

    public void removeSong(Track song) {
        this.songs.remove(song);
    }


}
