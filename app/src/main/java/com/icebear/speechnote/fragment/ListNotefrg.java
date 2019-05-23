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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.WidgetProvider;
import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.activity.SpeechNote;
import com.icebear.speechnote.itemadapter.NoteAdapter;
import com.icebear.speechnote.model.DatabaseHelper;
import com.icebear.speechnote.model.Noteib;

import java.util.ArrayList;


public class ListNotefrg extends Fragment {


    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout bannernonote;
    private LinearLayout bannernote;
    private int mScrollOffset = 4;


    ArrayList<Noteib> notelista;

    private MainActivity activity;

    private FloatingActionMenu fabmenu;
    private FloatingActionButton fabvoice, fabtext, fabtodo;
    public LinearLayout overlay;

    private DatabaseHelper database;

    public ListNotefrg() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_note, container, false);

        database = new DatabaseHelper(getContext());

        activity = (MainActivity) getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        bannernonote = (LinearLayout) view.findViewById(R.id.banner_no_note);
        bannernote = (LinearLayout) view.findViewById(R.id.banner_note);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_note);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        fabmenu = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fabvoice = (FloatingActionButton) view.findViewById(R.id.fab_voicenote);
        fabtext = (FloatingActionButton) view.findViewById(R.id.fab_textnote);
        fabtodo = (FloatingActionButton) view.findViewById(R.id.fab_todolist);
        overlay = (LinearLayout) view.findViewById(R.id.overlay);
        overlay.setVisibility(View.GONE);


        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.GONE);
                fabmenu.toggle(true);
                fabtext.hide(true);
                fabtodo.hide(true);
                fabvoice.hide(true);
            }
        });

        fabmenu.setClosedOnTouchOutside(true);

        fabmenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabmenu.isOpened()) {
                    overlay.setVisibility(View.GONE);
                    fabmenu.close(true);
                    fabtext.hide(true);
                    fabtodo.hide(true);
                    fabvoice.hide(true);
                } else {
                    overlay.setVisibility(View.VISIBLE);
//                    fabmenu.open(true);
                }
                fabmenu.toggle(true);

            }
        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (Math.abs(dy) > mScrollOffset) {
//                    if (dy > 0) {
//                        fabmenu.hideMenu(true);
//                    } else {
//                        fabmenu.showMenu(true);
//                    }
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                fabmenu.showMenu(true);
//            }
//        });

        fabtodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SpeechNote.class);
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_TODOLIST);
                intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
                startActivity(intent);
                overlay.setVisibility(View.GONE);
                fabmenu.close(true);
            }
        });
        fabtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SpeechNote.class);
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_TEXT_NOTE);
                intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
                startActivity(intent);
                overlay.setVisibility(View.GONE);
                fabmenu.close(true);
            }
        });
        fabvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SpeechNote.class);
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_VOICE_NOTE);
                intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
                startActivity(intent);
                overlay.setVisibility(View.GONE);
                fabmenu.close(true);
            }
        });

        LoadData loadData = new LoadData();
        loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);

        IntentFilter updatedbIntent = new IntentFilter(NoteConst.UPDATE_NOTE);
        getActivity().registerReceiver(updatedb, updatedbIntent);

        IntentFilter deletedbIntent = new IntentFilter(NoteConst.DELETE_NOTE);
        getActivity().registerReceiver(deletedb, deletedbIntent);


        return view;
    }


    private BroadcastReceiver deletedb = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Noteib noteib = (Noteib) intent.getSerializableExtra(NoteConst.OBJECT);
            database.deleteNote(noteib);
            database.deleteSig(noteib.getId());
            Intent intent1 = new Intent(WidgetProvider.BROADCAST_WIDGET_2);
            activity.sendBroadcast(intent1);

            Log.i("aaaaa", "delete note");
            LoadData loadData = new LoadData();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);


        }
    };

    private BroadcastReceiver updatedb = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LoadData loadData = new LoadData();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);


        }
    };


    public class LoadData extends AsyncTask<Void, Void, ArrayList<Noteib>> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            bannernote.setVisibility(View.GONE);
        }

        protected void onPostExecute(ArrayList<Noteib> s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);


            if (s.size() == 0) {
                bannernonote.setVisibility(View.VISIBLE);
                bannernote.setVisibility(View.GONE);

            } else {
                bannernonote.setVisibility(View.GONE);
                bannernote.setVisibility(View.VISIBLE);
            }


            recyclerView.setAdapter(new NoteAdapter((MainActivity) getActivity(), getContext(), s));
        }

        protected ArrayList<Noteib> doInBackground(Void... voids) {

            ArrayList<Noteib> notelist = new ArrayList<>();

            notelist = database.getAllNotes();
            return notelist;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updatedb != null) {
            getActivity().unregisterReceiver(updatedb);
        }

        if (deletedb != null) {
            getActivity().unregisterReceiver(deletedb);
        }


    }
}
