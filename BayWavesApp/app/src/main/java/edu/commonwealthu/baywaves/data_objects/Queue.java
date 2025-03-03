package edu.commonwealthu.baywaves.data_objects;

import java.util.List;

public class Queue {

    private int id;
    private int userId;
    private List<Song> songs;

    public Queue(int id, int userId, List<Song> songs) {
        this.id = id;
        this.userId = userId;
        this.songs = songs;
    }

    public void setId(int id) { this.id = id; }
    public int getId() {return id;}
    public void setUserId(int userId) { this.userId = userId; }
    public int getUserId() {return userId;}
    public void setSongs(List<Song> songs) { this.songs = songs;}
    public List<Song> getSongs() {return songs;}

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
    }



}
