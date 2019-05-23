package com.icebear.speechnote.model;

public class Medit {
    private int position;
    private int key;
    private String text;

    public Medit() {
        this.position = -1;
        this.key = -1;
        this.text = "";
    }

    public Medit(int position, int key, String text) {
        this.position = position;
        this.key = key;
        this.text = text;
    }


    public Medit( int key, String text) {
        this.key = key;
        this.text = text;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Medit{" +
                "position=" + position +
                ", key=" + key +
                ", text='" + text + '\'' +
                '}';
    }
}
