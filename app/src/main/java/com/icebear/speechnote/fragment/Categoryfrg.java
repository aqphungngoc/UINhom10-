package com.icebear.speechnote.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.itemadapter.CategoryAdapter;
import com.icebear.speechnote.model.Category;

import java.util.ArrayList;

public class Categoryfrg extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout bannernonote;
    private LinearLayout bannernote;
    private MainActivity activity;
    private FloatingActionButton fabcategory;


    public Categoryfrg() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        activity = (MainActivity) getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        bannernonote = (LinearLayout) view.findViewById(R.id.banner_no_note);
        bannernote = (LinearLayout) view.findViewById(R.id.banner_note);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_category);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabcategory = (FloatingActionButton) view.findViewById(R.id.fab);
        fabcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteConst.ADD_CATEGORY);
                activity.sendBroadcast(intent);
            }
        });
        LoadData loadData = new LoadData();
        loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);

        IntentFilter addcate = new IntentFilter(NoteConst.ADD_CATEGORY);
        activity.registerReceiver(addcateReceiver, addcate);

        IntentFilter deletecate = new IntentFilter(NoteConst.DELETE_CATEGORY);
        activity.registerReceiver(deletecateReceiver, deletecate);

        IntentFilter updatecate = new IntentFilter(NoteConst.UPDATE_CATEGORY);
        activity.registerReceiver(updatecateReceiver, updatecate);

        return view;
    }

    BroadcastReceiver addcateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fileNameDlg();
        }
    };

    BroadcastReceiver deletecateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Category category = (Category) intent.getSerializableExtra(NoteConst.OBJECT);
            activity.database.deleteCategory(category);
            LoadData loadData = new LoadData();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    private BroadcastReceiver updatecateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadData loadData = new LoadData();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    @SuppressLint("StaticFieldLeak")
    public class LoadData extends AsyncTask<Void, Void, ArrayList<Category>> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            bannernote.setVisibility(View.GONE);
        }

        protected void onPostExecute(ArrayList<Category> s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            if (s.size() == 0) {
                bannernonote.setVisibility(View.VISIBLE);
                bannernote.setVisibility(View.GONE);

            } else {
                bannernonote.setVisibility(View.GONE);
                bannernote.setVisibility(View.VISIBLE);
            }

            CategoryAdapter adapter = new CategoryAdapter((MainActivity) getActivity(), getContext(), s);
            recyclerView.setAdapter(adapter);
        }

        protected ArrayList<Category> doInBackground(Void... voids) {

//            categories = new ArrayList<>();
//
//            categories = activity.database.getAllCategories();
            return activity.database.getAllCategories();
        }
    }

    private boolean toastisshowing = true;

    private void fileNameDlg() {
        final Dialog dlg = new Dialog(activity);
        Log.i("quanpna", "step2: show file name diaglog ");
//        dlg.requestWindowFeature(1);
        dlg.setContentView(R.layout.dialog_add_new_category);
        dlg.setCanceledOnTouchOutside(true);
        final Button cancel = (Button) dlg.findViewById(R.id.cancel);
        final EditText fileNameEtx = (EditText) dlg.findViewById(R.id.edit_name);
        fileNameEtx.requestFocus();
        Button ok = (Button) dlg.findViewById(R.id.cofirm);

        dlg.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fileNameEtx.getText().toString().trim().equalsIgnoreCase("")) {

                    if (toastisshowing) {
                        Log.i("show", getClass().toString());
                        Toast.makeText(activity, R.string.filename_empty, Toast.LENGTH_SHORT).show();
                        toastisshowing = false;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toastisshowing = true;
                        }
                    }, 4000);
                } else {
                    fileNameEtx.setSelection(fileNameEtx.getText().toString().length() - 1);
                    Category category = new Category();
                    category.setCategory(fileNameEtx.getText().toString());
                    activity.database.addCategory(category);
                    dlg.dismiss();
                    LoadData loadData = new LoadData();
                    loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updatecateReceiver != null) {
            activity.unregisterReceiver(updatecateReceiver);
        }
        if (deletecateReceiver != null) {
            activity.unregisterReceiver(deletecateReceiver);
        }

        if (addcateReceiver != null) {
            activity.unregisterReceiver(addcateReceiver);
        }
    }


}
