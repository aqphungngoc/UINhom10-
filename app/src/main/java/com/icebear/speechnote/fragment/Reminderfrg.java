package com.icebear.speechnote.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionButton;
import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.activity.SpeechNote;
import com.icebear.speechnote.alarmwithreminder.ReminderManager;
import com.icebear.speechnote.itemadapter.ReminderAdapter;
import com.icebear.speechnote.notefile.Reminder;

import java.util.ArrayList;

public class Reminderfrg extends Fragment {


    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout bannernonote;
    private LinearLayout bannernote;
    private MainActivity activity;
    private FloatingActionButton fabreminder;
    public Reminderfrg(){

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        activity = (MainActivity) getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        bannernonote = (LinearLayout) view.findViewById(R.id.banner_no_note);
        bannernote = (LinearLayout) view.findViewById(R.id.banner_note);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_reminder);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabreminder = (FloatingActionButton) view.findViewById(R.id.fab);
        fabreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SpeechNote.class);
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_REMINDER);
                intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
                startActivity(intent);
            }
        });

        LoadData loadData = new LoadData();
        loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);



        IntentFilter deletecate = new IntentFilter(NoteConst.DELETE_REMINDER);
        activity.registerReceiver(deletereminderReceiver, deletecate);

        IntentFilter updatecate = new IntentFilter(NoteConst.UPDATE_REMINDER);
        activity.registerReceiver(updatereminderReceiver, updatecate);

        return view;
    }



    BroadcastReceiver deletereminderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Reminder reminder = (Reminder) intent.getSerializableExtra(NoteConst.OBJECT);
            activity.database.deleteReminder(reminder);
            ReminderManager.setAlarms(context);
            LoadData loadData = new LoadData();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    };

    private BroadcastReceiver updatereminderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadData loadData = new LoadData();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            ReminderManager.setAlarms(context);
        }
    };
    
    public class LoadData extends AsyncTask<Void, Void, ArrayList<Reminder>> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            bannernote.setVisibility(View.GONE);
        }

        protected void onPostExecute(ArrayList<Reminder> s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);


            if (s.size() == 0){
                bannernonote.setVisibility(View.VISIBLE);
                bannernote.setVisibility(View.GONE);

            } else {
                bannernonote.setVisibility(View.GONE);
                bannernote.setVisibility(View.VISIBLE);
            }


            recyclerView.setAdapter(new ReminderAdapter((MainActivity) getActivity(), getContext(), s));
        }

        protected ArrayList<Reminder> doInBackground(Void... voids) {
            return activity.database.getAllReminder();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updatereminderReceiver != null){
            activity.unregisterReceiver(updatereminderReceiver);
        }
        if (deletereminderReceiver != null){
            activity.unregisterReceiver(deletereminderReceiver);
        }
    }


}
