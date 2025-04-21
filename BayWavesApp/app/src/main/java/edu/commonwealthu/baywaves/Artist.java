package edu.commonwealthu.baywaves;

public class Artist {

    private int id;
    private String name;
    private String bio;
    private int followers;
    private int members;

    public Artist(int id, String name, String bio, int followers, int members) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.followers = followers;
        this.members = members;
    }

    public void setId(int id) { this.id = id; }
    public int getId() {return id;}
    public void setName(String name) { this.name = name; }
    public String getName() {return name;}
    public void setBio(String bio) { this.bio = bio; }
    public String getBio() {return bio;}
    public void setFollowers(int followers) { this.followers = followers; }
    public int getFollowers() {return followers;}
    public void setMembers(int members) { this.members = members; }
    public int getMembers() {return members;}

}
