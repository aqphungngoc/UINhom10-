package com.icebear.speechnote.alarmwithreminder;

import android.content.Context;

public class ApiService {

    public static DataService getService(Context context){
        return ApiRetrofitClient.getClient(context).create(DataService.class);
    }

    // for push data report
    public static DataService getService(String baseURL){
        return ApiRetrofitClient.getClient(baseURL).create(DataService.class);
    }
}
