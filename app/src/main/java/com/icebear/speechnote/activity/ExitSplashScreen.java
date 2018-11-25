package com.icebear.speechnote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.icebear.speechnote.R;
import com.icebear.speechnote.utils.Helper;


public class ExitSplashScreen extends Activity {
    private static int TIMEOUT = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.splashscreenexit);
        Helper.setColorStatusBar(this, R.color.colorAccent);
        getIntent();
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {

                finish();
//                Intent i = new Intent(ExitSplashScreen.this, ContenShowtActivity.class);
//                startActivity(i);
                // close this activity7
            }
        }, TIMEOUT);
    }

    @Override
    public void onBackPressed() {

    }
}
