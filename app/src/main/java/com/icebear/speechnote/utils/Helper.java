package com.icebear.speechnote.utils;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.icebear.speechnote.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Helper {



    public static Typeface getTypeFace(Context context, int type){
        Typeface typeface = null;
        if (type == 1){
            return Typeface.createFromAsset(context.getAssets(), "Montserrat-Medium.ttf");
        }else if (type == 2){
            return Typeface.createFromAsset(context.getAssets(), "SVN-Kelson-Sans-Bold.otf");
        }else {
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

    public static void setSoundfor(final Context context, int type,  String pathMusic) {
        if (Build.VERSION.SDK_INT < 23) {
            setSoundFile(context, type, new File(pathMusic));
        } else {
            if (Settings.System.canWrite(context)) {
                setSoundFile(context, type, new File(pathMusic));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Write Setting permission required");
                builder.setNegativeButton(R.string.app_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }

    }

    public static void setSoundFile(Context context, int type, File newSoundFile) {
        ContentValues values = new ContentValues();
        values.put("_data", newSoundFile.getAbsolutePath());
        //values.put(ShareConstants.WEB_DIALOG_PARAM_TITLE, "smartbird");
        values.put("mime_type", "audio/mp3");
        values.put("_size", newSoundFile.length());
        values.put("artist", R.string.app_name);
        boolean ring = false;
        boolean notification = false;
        boolean alarm = false;
        if (type == 1) {
            ring = true;
        } else if (type == 2) {
            notification = true;
        } else {
            alarm = true;
        }
        values.put("is_ringtone", ring);
        values.put("is_notification", notification);
        values.put("is_alarm", alarm);
        values.put("is_music", false);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(newSoundFile.getAbsolutePath());
        ContentResolver mCr = context.getContentResolver();
        mCr.delete(uri, "_data=\"" + newSoundFile.getAbsolutePath() + "\"", null);
        try {
            RingtoneManager.setActualDefaultRingtoneUri(context, type, mCr.insert(uri, values));
        } catch (Throwable th) {
        }
        Toast.makeText(context, R.string.app_success, Toast.LENGTH_SHORT).show();
    }

    private static final String track_id = MediaStore.Audio.Media._ID;
    private static final String track_no = MediaStore.Audio.Media.TRACK;
    private static final String track_name = MediaStore.Audio.Media.TITLE;
    private static final String artist = MediaStore.Audio.Media.ARTIST;
    private static final String artist_id = MediaStore.Audio.Media.ARTIST_ID;
    private static final String duration = MediaStore.Audio.Media.DURATION;
    private static final String album = MediaStore.Audio.Media.ALBUM;
    private static final String composer = MediaStore.Audio.Media.COMPOSER;
    private static final String year = MediaStore.Audio.Media.YEAR;
    private static final String path = MediaStore.Audio.Media.DATA;
    private static final String date_added = MediaStore.Audio.Media.DATE_ADDED;

    private static String[] columns = {track_id ,track_no, artist, track_name,
            album, duration, path, year, composer};


    public static void shareMp3(Context context, String file) {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + file));
            context.startActivity(Intent.createChooser(share, "Share Music File"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        //Chuyển đổi thời gian sang định dạng Hours:Minutes:Seconds
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        //Thêm giờ nếu có
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        //Thêm vào số 0 nếu có 1 chữ số
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //kết quả trả về
        return finalTimerString;
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        //Tính toán tỉ lệ %
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        //trả về giá trị %
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        //trả về thời gian hiện tại trong mili giây
        return currentDuration * 1000;
    }
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static float roundFloat(float number, int value) {
        if (value == 0) return (float) (Math.round(number * 1.0) / 1.0);
        else if (value == 1) return (float) (Math.round(number * 10.0) / 10.0);
        else if (value == 2) return (float) (Math.round(number * 100.0) / 100.0);
        else return number;
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlock = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlock = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
        } else {
            availableBlock = statFs.getAvailableBlocks();
            blockSize = statFs.getBlockSize();
        }

        return availableBlock * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = 0;
        long totalBlock = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            totalBlock = statFs.getBlockCountLong();
        } else {
            blockSize = statFs.getBlockSize();
            totalBlock = statFs.getBlockCount();
        }

        return totalBlock * blockSize;
    }

    public static String getDate(Context context) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm - dd/MM/yyyy");
        String date = "";
        Date date1 = Calendar.getInstance().getTime();
        date = format.format(date1);
        return date;
    }

    public static void unMuteSound(Context mContext) {
        try {
            AudioManager aManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            aManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void muteSound(Context mContext) {
        try {
            AudioManager aManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            aManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getColorNativeAds(Context context) {
        int[] arrColor = {R.color.blue_native, R.color.purple_native, R.color.green_native,
                R.color.orange_native, R.color.red_native, R.color.darkblue_native, R.color.darkpurple_native,
                R.color.darkgreen_native, R.color.darkorange_native, R.color.darkred_native};
        int color = new Random().nextInt(arrColor.length);

        return arrColor[color];
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

    //get more game

    public static void feedback(Context context) {
        context.startActivity(createEmailIntent(context));
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

    public static void onClickApp(Context context, String uri) {
        if (isAppInstalled(context, uri)) {
            //openApp(context, uri);
            callPlayStore(context, uri);
        } else {
            callPlayStore(context, uri);
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void callPlayStore(Context context, String packageName) {
        if (packageName.contains("https://")) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(packageName)));
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }
    }


    // format size
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0 B/s";
        final String[] units = new String[]{"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    // format size
    public static String getFileSize_2(long size) {
        if (size <= 0)
            return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    // format size
    public static String getFileSize_3(long size) {
        if (size <= 0)
            return "0 Hz";
        final String[] units = new String[]{"KHz", "MHz", "GHz", "THz"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1000));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static Long getTimeMilis(String date, String dateFormat)
    {
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
