package com.icebear.speechnote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.icebear.speechnote.activity.SpeechNote;
import com.icebear.speechnote.model.DatabaseHelper;
import com.icebear.speechnote.model.Noteib;

import java.util.ArrayList;

class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Noteib> listItemList = new ArrayList<Noteib>();
    private Context context = null;
    private int appWidgetId;

    DatabaseHelper db;


    public ListProvider(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_row);
        Noteib note = listItemList.get(position);
        Log.i("bbbbbb", note.getTitle() + " noteTitle");

        if (note.getTitle().equals("") && note.getDespreview().equals("") ) {
            remoteView.setTextViewText(R.id.widget_item_title, context.getString(R.string.untitled_note));
        }
        if (note.getTitle().equals("") && !note.getDespreview().equals("")) {
            remoteView.setTextViewText(R.id.widget_item_title, note.getDespreview());
        }
        if (!note.getTitle().equals("")) {
            remoteView.setTextViewText(R.id.widget_item_title, note.getTitle());
        }

        Intent intent = new Intent(context ,SpeechNote.class);
        intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_EDIT_NOTE);
        intent.putExtra(NoteConst.OBJECT, note);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteView.setOnClickPendingIntent(R.id.item_row, pendingIntent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onCreate() {
        db = new DatabaseHelper(context);
        this.listItemList = db.getAllNotes();
        Log.i("bbbbbb", listItemList.size() + " widget _size");
    }

    @Override
    public void onDataSetChanged() {
        this.listItemList = db.getAllNotes();
    }

    @Override
    public void onDestroy() {

    }
}
