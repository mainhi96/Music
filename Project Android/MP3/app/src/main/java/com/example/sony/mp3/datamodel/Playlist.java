package com.example.sony.mp3.datamodel;

/**
 * Created by SONY on 5/16/2018.
 */

public class Playlist {
    private int playlistid;
    private String username;
    private String name;

    public Playlist() {
    }

    public Playlist(int playlistid, String username, String name) {
        this.playlistid = playlistid;
        this.username = username;
        this.name = name;
    }

    public int getPlaylistid() {
        return playlistid;
    }

    public void setPlaylistid(int playlistid) {
        this.playlistid = playlistid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistid=" + playlistid +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
