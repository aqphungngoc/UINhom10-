package com.icebear.speechnote.itemadapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.notefile.Reminder;
import com.icebear.speechnote.utils.Helper;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder>implements View.OnClickListener {


    private ArrayList<Reminder> arrayList;

    private MainActivity activity;
    private Context context;


    public ReminderAdapter(MainActivity activity, Context context, ArrayList<Reminder> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.context = context;


//        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView des, time;
        public ImageView more, ringtone, vibrate, repeat, noti;
        LinearLayout itemNote;

        public MyViewHolder(View itemView) {
            super(itemView);
            des = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            noti = (ImageView) itemView.findViewById(R.id.noti);
            more = (ImageView) itemView.findViewById(R.id.more);
            ringtone = (ImageView) itemView.findViewById(R.id.ringtone);
            vibrate = (ImageView) itemView.findViewById(R.id.vibrate);
            repeat = (ImageView) itemView.findViewById(R.id.repeate);
            itemNote = (LinearLayout) itemView.findViewById(R.id.item_note);

        }


    }


    public ReminderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);

        return new MyViewHolder(itemView);
    }


    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Reminder reminder = arrayList.get(position);
        holder.des.setText(reminder.getNotedes());
        if (reminder.getTime() < System.currentTimeMillis() && reminder.getRepeatable() == 0 ){
            holder.noti.setImageResource(R.drawable.notification_off);
        } else {
            holder.noti.setImageResource(R.drawable.notifications_on);
        }
        if (reminder.getRingtone() == 0){
            holder.ringtone.setImageResource(R.drawable.volume_off);
        } else {
            holder.ringtone.setImageResource(R.drawable.volume_on);
        }
        if (reminder.getVibrate() == 0){
            holder.vibrate.setImageResource(R.drawable.vibrate_off);
        } else {
            holder.vibrate.setImageResource(R.drawable.vibrate_on);
        }
        if (reminder.getRepeatable() == 0){
            holder.repeat.setImageResource(R.drawable.repeat_off);
        } else {
            holder.repeat.setImageResource(R.drawable.repeat_on);
        }
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupMenu(holder.more, reminder);
            }
        });
        holder.itemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editReminder(reminder);
            }
        });

        String time = Helper.getDate(reminder.getTime(), NoteConst.DATE_FORMAT_HOUR_MINUTES);
        holder.time.setText(time);


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public Reminder getItem(int position) {
        return arrayList.get(position);
    }


    private void openPopupMenu(ImageView more, final Reminder category) {
        PopupMenu popup = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(context, more, Gravity.BOTTOM);
        } else {
            popup = new PopupMenu(context, more);
        }
//Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_reminder, popup.getMenu());

//registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_delete) {
                    confirmDelete(category);
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void editReminder(final Reminder reminder) {

    }

    private void confirmDelete(final Reminder category) {
        CharSequence message;
        CharSequence title;

        Log.i("aaaa", category.getId() + " cateId: ");
        title = context.getResources().getText(R.string.delete_note_confirm);
        new AlertDialog.Builder(activity).
                setTitle(title).
//                setMessage(message).
        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        onDelete(category);
    }
}).
                setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).
                setCancelable(true).
                show();


    }

    private void onDelete(Reminder reminder) {
        Intent intent = new Intent(NoteConst.DELETE_REMINDER);
        intent.putExtra(NoteConst.OBJECT, reminder);
        activity.sendBroadcast(intent);
    }

}
