package com.icebear.speechnote.notefile;

public class Notifi {
    public String title;
    public String message;
    public String time;
    public Boolean repeate;

    public Notifi() {
        this.title = "";
        this.message = "";
        this.time = "2019-05-22 21:00";
        this.repeate = false;
    }

    public Notifi(String title, String message, String time, Boolean repeate) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.repeate = repeate;
    }

}
