package com.icebear.speechnote.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.icebear.speechnote.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Helper {


    public static Typeface getTypeFace(Context context, int type) {
        Typeface typeface = null;
        if (type == 1) {
            return Typeface.createFromAsset(context.getAssets(), "Montserrat-Medium.ttf");
        } else if (type == 2) {
            return Typeface.createFromAsset(context.getAssets(), "SVN-Kelson-Sans-Bold.otf");
        } else {
            return Typeface.createFromAsset(context.getAssets(), "SVN-Kelson-Sans-Regular.otf");
        }
    }

    public static void scanMedia(Context context, String filePath) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDirPathDownloaded(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/qrcode";
    }

    public static void sharePicturePNG(Context context, ImageView content) {
        content.setDrawingCacheEnabled(true);

        File directory = new File(getDirPathDownloaded(context));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Bitmap bitmap = content.getDrawingCache();
        File cachePath = new File(directory + "/" + System.currentTimeMillis() + ".png");

        try {
            cachePath.createNewFile();
            FileOutputStream ostream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("xxxx", e.getMessage());
        }


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
        context.startActivity(Intent.createChooser(share, "Share PNG"));

    }


    public static void shareContentText(Context context, String content) {
        int applicationNameId = context.getApplicationInfo().labelRes;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
        String text = content;

        i.putExtra(Intent.EXTRA_TEXT, text + "");
        context.startActivity(Intent.createChooser(i, "Share code"));
    }

    public static void shareApp(Context context) {
        int applicationNameId = context.getApplicationInfo().labelRes;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
        String text = "";
        String link = Constant.PLAY_STORE_APP_URL + context.getPackageName();
        i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        context.startActivity(Intent.createChooser(i, "Share Appp"));
    }

    // check conneted internet
    public static boolean isConnectedInternet(Context context) {
        boolean isConnectedWifi = false, isConnected3G = false;
        // kiem  tra co ket noi internet hoac wifi khong
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        try {
            if (manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
                isConnectedWifi = (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo()
                        .isConnected());
            }
            if (manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE) {
                isConnected3G = (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo()
                        .isConnected());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("hieptb", (isConnectedWifi || isConnected3G) + "");
        return (isConnectedWifi || isConnected3G);
    }

    // set status bar color
    public static void setColorStatusBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }

    public static Intent createEmailIntent(Context context) {
        String toEmail = Constant.EMAIL_DEVELOPER;

        String release = Build.VERSION.RELEASE;
        int SDK = Build.VERSION.SDK_INT;
        String yourName = "customer";
        String phoneName = getDeviceName();
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version1 = info.versionName;
        String subject = context.getString(R.string.app_name);
        String message = "--------------------\nDevice information:\n\nPhone name :" + phoneName + "\nAPI Level: " + SDK + "\nVersion: " + release +
                "\nApp version: " + version1 + "\nUsername: " + yourName + "\n--------------------\n\nContent : ";

        Intent sendTo = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(toEmail) +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(message);
        Uri uri = Uri.parse(uriText);
        sendTo.setData(uri);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(sendTo, 0);

        // Emulators may not like this check...
        if (!resolveInfos.isEmpty()) {
            return sendTo;
        }

        // Nothing resolves send to, so fallback to send...
        Intent send = new Intent(Intent.ACTION_SEND);

        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_EMAIL,
                new String[]{toEmail});
        send.putExtra(Intent.EXTRA_SUBJECT, subject);
        send.putExtra(Intent.EXTRA_TEXT, message);

        return Intent.createChooser(send, "Send feedback for developer");
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static Long getTimeMilis(String date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.getTime();
    }


}
