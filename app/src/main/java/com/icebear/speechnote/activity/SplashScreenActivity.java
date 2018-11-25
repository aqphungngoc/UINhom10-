package com.icebear.speechnote.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.icebear.speechnote.R;
import com.icebear.speechnote.utils.Helper;
import com.icebear.speechnote.utils.MySetting;


public class SplashScreenActivity extends AppCompatActivity {

    CountDownTimer timer;

    public static boolean isShow = true;

    String[] listPermission = new String[]{
            Manifest.permission.RECORD_AUDIO,
    };
    boolean isPermissionSet1 = false;
    boolean isPermissionSet2 = false;
    int numberPermissionDenied;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Helper.setColorStatusBar(this, R.color.colorAccent);

        checkPermission1(false);

        isShow = true;


        timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();

                isShow = false;
            }
        }.start();

    }

//    private void checkPermission2(boolean isClickButton) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Settings.System.canWrite(SplashScreenActivity.this)) {
//                isPermissionSet2 = true;
//            }
//            //isPermissionSet2 = true;
//        } else {
//            isPermissionSet2 = true;
//        }
//
//
//    }



    private void checkPermission1(boolean isClickButton) {
        if (!isClickButton) {
            checkStatePermission1();
        }
    }
    private void checkStatePermission1(){
        numberPermissionDenied = 0;
        for (int i = 0; i < listPermission.length; i++) {
            if (ContextCompat.checkSelfPermission(this, listPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                numberPermissionDenied++;
            }
        }

        if (numberPermissionDenied == 0) {
            isPermissionSet1 = true;
        } else {
            isPermissionSet1 = false;
        }
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
//        try {
//            if (!AdsService.isLive)startService(new Intent(this, AdsService.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
