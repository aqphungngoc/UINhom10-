package com.icebear.speechnote.alarmwithreminder;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DataService {

    @POST("/set-alarm")
    Call<Void> setAlarms(@Body AlarmRequest classAlarmRequest);

}
