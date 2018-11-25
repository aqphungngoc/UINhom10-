package com.icebear.speechnote.itemadapter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import com.icebear.speechnote.WidgetProvider;
import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.activity.SpeechNote;
import com.icebear.speechnote.notefile.Noteib;
import com.icebear.speechnote.utils.Helper;

import java.util.ArrayList;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> implements View.OnClickListener{

    private ArrayList<Noteib> arrayList;

    private Activity activity;
    private Context context;


    public NoteAdapter(Activity activity, Context context, ArrayList<Noteib> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.context = context;
//        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, createdtime, checklistcount, des;
        public ImageView more;
        public ImageView checklistimg;

        public LinearLayout itemNote;
        public LinearLayout category;
        public LinearLayout checklist;


        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            createdtime = (TextView) itemView.findViewById(R.id.time);
            des = (TextView) itemView.findViewById(R.id.des);

            more = (ImageView) itemView.findViewById(R.id.more);
            itemNote = (LinearLayout) itemView.findViewById(R.id.item_note);

            category = (LinearLayout) itemView.findViewById(R.id.category);
            checklist = (LinearLayout) itemView.findViewById(R.id.checklist);

            checklistcount = (TextView) itemView.findViewById(R.id.checklist_count);
            checklistimg = (ImageView) itemView.findViewById(R.id.checklist_img);

        }



    }


    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note,parent, false);
        return new MyViewHolder(itemView);
    }


    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Noteib note = arrayList.get(position);

        if (note.getTitle().equals("")){
            holder.title.setText(context.getString(R.string.untitled_note));
        } else {
            holder.title.setText(note.getTitle());
        }

        if (note.getDespreview().equals("")){
            holder.des.setText(note.getTitle());
        } else {
            holder.des.setText(note.getDespreview());
        }


        if (note.getPriority() == 1){
            holder.category.setVisibility(View.VISIBLE);
        } else {
            holder.category.setVisibility(View.GONE);
        }

        if (note.getAlltask() == 0){
            holder.checklist.setVisibility(View.INVISIBLE);
        } else {
            holder.checklist.setVisibility(View.VISIBLE);
        }

        if (note.getAlltask() == note.getCurtask()){
            holder.checklistimg.setImageResource(R.drawable.checklist_done);
        }
        holder.checklistcount.setText(note.getCurtask() + "/" + note.getAlltask());

        String time = Helper.getDate(note.getCreatedtime(), NoteConst.DATE_FORMAT);
        holder.createdtime.setText(time);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupMenu(holder.more, note);
            }
        });
        holder.itemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechNoteEditActivity(note);
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public Noteib getItem(int position){
        return arrayList.get(position) ;
    }


    private void openPopupMenu(ImageView more, final Noteib noteib) {
        PopupMenu popup = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(activity, more, Gravity.BOTTOM);
        } else {
            popup = new PopupMenu(activity, more);
        }
//Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_note, popup.getMenu());

//registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    startSpeechNoteEditActivity(noteib);
                } else if (id == R.id.action_delete) {
                    confirmDelete(noteib);
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void startSpeechNoteEditActivity(Noteib noteib) {
        Intent intent = new Intent(activity, SpeechNote.class);
        intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_EDIT_NOTE);
        intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
        intent.putExtra(NoteConst.OBJECT, noteib);
        activity.startActivity(intent);
    }

    private void confirmDelete(final Noteib noteib) {
        CharSequence message;
        CharSequence title;


        title = context.getResources().getText(R.string.delete_note_confirm);

        new AlertDialog.Builder(activity).
                setTitle(title).
//                setMessage(message).
                setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onDelete(noteib);
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



    private void onDelete(Noteib noteib) {
        Intent intent = new Intent(NoteConst.DELETE_NOTE);
        intent.putExtra(NoteConst.OBJECT, noteib);
        activity.sendBroadcast(intent);


    }



}