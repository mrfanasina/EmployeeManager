package com.fa.baiboly.models;

public class Verse {

    private int number;
    private String text;

    private String title;

    public Verse(int number, String text, String title) {
        this.number = number;
        this.text = text;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }
}