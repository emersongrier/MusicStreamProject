package edu.commonwealthu.baywaves.data_objects;

import java.util.List;

import edu.commonwealthu.baywaves.Track;

public class Queue {

    private int id;
    private int userId;
    private List<Track> songs;

    public Queue(int id, int userId, List<Track> songs) {
        this.id = id;
        this.userId = userId;
        this.songs = songs;
    }

    public void setId(int id) { this.id = id; }
    public int getId() {return id;}
    public void setUserId(int userId) { this.userId = userId; }
    public int getUserId() {return userId;}
    public void setSongs(List<Track> songs) { this.songs = songs;}
    public List<Track> getSongs() {return songs;}

    public void addSong(Track song) {
        this.songs.add(song);
    }

    public void removeSong(Track song) {
        this.songs.remove(song);
    }



}
