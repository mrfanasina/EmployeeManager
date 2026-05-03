package com.fa.baiboly.models;

public class SongVerse {
    private String song_id;
    private int andininy;
    private  String tononkira;
    private Boolean fiverenany;

    public SongVerse(String song_id, int andininy, String tononkira, Boolean fiverenany) {
        this.song_id = song_id;
        this.andininy = andininy;
        this.fiverenany = fiverenany;
        this.tononkira = tononkira;
    }

    public String getSong_id() {
        return song_id;
    }

    public Boolean getFiverenany() {
        return fiverenany;
    }

    public int getAndininy() {
        return andininy;
    }

    public String getTononkira() {
        return tononkira;
    }

    public void setAndininy(int andininy) {
        this.andininy = andininy;
    }

    public void setFiverenany(Boolean fiverenany) {
        this.fiverenany = fiverenany;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

}
