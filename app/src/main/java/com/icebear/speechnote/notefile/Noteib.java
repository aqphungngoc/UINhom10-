package com.icebear.speechnote.notefile;

import java.io.Serializable;

public class Noteib implements Serializable {

    private  int id;
    private String title;
    private String des;
    private int categoryid;
    private int alltask;
    private int curtask;
    private String despreview;
    private int priority;
    private long createdtime;

    public Noteib() {
        id = -1;
        title = "";
        des = "";
        despreview ="";
        categoryid = 0;
        priority = 0;
        alltask = 0;
        curtask = 0;
    }

    public Noteib(int id, String title , String des, int priority, long createdtime, int alltask, int curtask, String despreview) {
        this.id = id;
        this.title = title;
        this.des = des;
        this.priority = priority;
        this.createdtime = createdtime;
        this.alltask = alltask;
        this.curtask = curtask;
        this.despreview = despreview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(long createdtime) {
        this.createdtime = createdtime;
    }

    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public int getAlltask() {
        return alltask;
    }

    public void setAlltask(int alltask) {
        this.alltask = alltask;
    }

    public int getCurtask() {
        return curtask;
    }

    public void setCurtask(int curtask) {
        this.curtask = curtask;
    }

    public String getDespreview() {
        return despreview;
    }

    public void setDespreview(String despreview) {
        this.despreview = despreview;
    }
}
