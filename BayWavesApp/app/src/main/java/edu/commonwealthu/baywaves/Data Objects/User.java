package edu.commonwealthu.baywaves;

import java.util.List;

public class User {

    private int id;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private int friends;
    private List<String> playListsIds; // IDs of user's playlists

    public User(int id, String userName, String password, String email, String phone, int friends, List<String> playListsIds) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.friends = friends;
        this.playListsIds = playListsIds;
    }

    public void setId(int id) { this.id = id; }
    public int getId() {return id;}
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserName() {return userName;}
    public void setPassword(String password) { this.password = password; }
    public String getPassword() {return password;}
    public void setEmail(String email) { this.email = email; }
    public String getEmail() {return email;}
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() {return phone;}
    public void setFriends(int friends) { this.friends = friends; }
    public int getFriends() {return friends;}
    public void setPlaylistsIds(List<String> playListsIds) {this.playListsIds = playListsIds;}
    public List<String> getPlayListsIds() {return playListsIds;}
}
