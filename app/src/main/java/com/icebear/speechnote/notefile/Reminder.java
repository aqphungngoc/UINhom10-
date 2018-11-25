package com.icebear.speechnote.notefile;

import java.io.Serializable;

public class Reminder implements Serializable{

    private int id;
    private int noteid;
    private long time;
    private int ringtone;
    private int vibrate;
    private int repeatable;

    private String notedes;

    public Reminder() {
        id = -1;
        noteid = -1;
        time = 0;
        ringtone = 0;
        vibrate = 0;
        repeatable = 0;
        notedes = "";
    }

    public Reminder(int id, int noteid, long time) {
        this.id = id;
        this.noteid = noteid;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteid() {
        return noteid;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRingtone() {
        return ringtone;
    }

    public void setRingtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public int getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(int repeatable) {
        this.repeatable = repeatable;
    }

    public String getNotedes() {
        return notedes;
    }

    public void setNotedes(String notedes) {
        this.notedes = notedes;
    }

    @Override
    public String  toString() {
        return "Reminder{" +
                "id=" + id +
                ", noteid=" + noteid +
                ", time=" + time +
                ", ringtone=" + ringtone +
                ", vibrate=" + vibrate +
                ", repeatable=" + repeatable +
                ", notedes='" + notedes + '\'' +
                '}';
    }
}
