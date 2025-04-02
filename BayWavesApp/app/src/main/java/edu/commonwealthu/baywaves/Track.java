package edu.commonwealthu.baywaves;

public class Track {

    private int id;
    private String name;
    private String filePath;
    private int position;
    private String lyrics;
    private int length;
    private int streams;
    private int likes;
    private int albumId;
    private int artistId;

    private transient boolean isLikedLocally = false;

    public Track(int id, String name, String filePath, int position, String lyrics, int length, int streams, int likes, int albumId, int artistId){
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.position = position;
        this.lyrics = lyrics;
        this.length = length;
        this.streams = streams;
        this.likes = likes;
        this.albumId = albumId;
        this.artistId = artistId;
    }

    // Getters and setters
    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName(){return name;}
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFilePath(){return filePath;}
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }
    public String getLyrics(){return lyrics;}
    public void setPosition(int position) { this.position = position; }
    public int getPosition(){return position;}
    public void setLength(int length) { this.length = length; }
    public int getLength(){return length;}
    public void setStreams(int streams) { this.streams = streams; }
    public int getStreams(){return streams;}
    public void setLikes(int likes) { this.likes = likes; }
    public int getLikes(){return likes;}
    public void setAlbumId(int albumId) { this.albumId = albumId; }
    public int getAlbumId(){return albumId;}
    public void setArtistId(int artistId) { this.artistId = artistId; }
    public int getArtistId(){return artistId;}

    public void setLocalLikedState(boolean liked) {
        this.isLikedLocally = liked;
    }

    public boolean isLocalLikedState() {
        return this.isLikedLocally;
    }
}
