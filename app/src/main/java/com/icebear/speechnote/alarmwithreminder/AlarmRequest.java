package com.icebear.speechnote.alarmwithreminder;

import com.icebear.speechnote.model.Notifi;

import java.util.ArrayList;

public class AlarmRequest {
    private ArrayList<Notifi> data;

    public AlarmRequest(ArrayList<Notifi> list) {
        this.data = list;
    }
}
