package com.icebear.speechnote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.RemoteViews;

import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.activity.SpeechNote;
import com.icebear.speechnote.activity.SplashScreenActivity;

public class WidgetProvider extends AppWidgetProvider {

    public static final String REQUEST_CODE = "ActivityType";
    public static final int CODE_CREATE_VOICE_NOTE = 101;

    public static final String BROADCAST_WIDGET_2 = "widgetnote2";
    public static final int REQUESTCODE_WIDGET = 999999;
    public static final String ID = "id";

    private boolean isTurnOn;

    @Override
    public void onReceive(Context context, Intent intent) {
        isTurnOn = intent.getBooleanExtra(WidgetProvider.ID, false);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            onUpdate(context, appWidgetManager, appWidgetIds);
        }

    }

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                appWidgetManager.updateAppWidget(appWidgetId, views);

            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_WIDGET_2);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        /*int[] appWidgetIds holds ids of multiple instance
         * of your widget
         * meaning you are placing more than one widgets on
         * your homescreen*/
        for (int i = 0; i <appWidgetIds.length; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
//            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_note);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.widget_list, svcIntent);
        remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view);

        Intent startAddActivity = new Intent(context, SpeechNote.class);
        startAddActivity.putExtra(NoteConst.REQUEST_CODE, CODE_CREATE_VOICE_NOTE);
        PendingIntent startAddAct = PendingIntent.getActivity(context, 0, startAddActivity, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_bar, startAddAct);




        return remoteViews;

    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
    }


}

