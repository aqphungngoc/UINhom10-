package com.icebear.speechnote.model;

import java.io.Serializable;

public class Category implements Serializable {

    private int id;
    private String category;
    private int notecount;

    public Category() {
        this.id = -1;
        this.category = "";
        this.notecount = 0;
    }

    public Category(int id, String category, int notecount) {
        this.id = id;
        this.category = category;
        this.notecount = notecount;

    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotecount() {
        return notecount;
    }

    public void setNotecount(int notecount) {
        this.notecount = notecount;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", notecount=" + notecount +
                '}';
    }
}
