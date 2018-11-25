package com.icebear.speechnote.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.WidgetProvider;
import com.icebear.speechnote.itemadapter.NoteAdapter;
import com.icebear.speechnote.itemadapter.SpinerPiorityAdapter;
import com.icebear.speechnote.notefile.DatabaseHelper;
import com.icebear.speechnote.notefile.Noteib;
import com.icebear.speechnote.utils.Helper;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public DatabaseHelper database;
    private RecyclerView recyclerView;
    private CardView filter;
    private MenuItem filterItem;
    private ImageView close;
    private Spinner filterSpiner;

    private int filterCode = NoteConst.DEFAULT_CATE_AND_PIO;
    private String cursearch = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Helper.setColorStatusBar(this, R.color.colorAccent);
        filter = (CardView) findViewById(R.id.filter);
        close = (ImageView) findViewById(R.id.closefilter);
        filterSpiner = (Spinner) findViewById(R.id.category_spiner);
        filter.setVisibility(View.GONE);
        setUpSpinerPiority();


        database = new DatabaseHelper(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LoadDataByDesCatePior loadData = new LoadDataByDesCatePior();
        loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{"", "" ,""});

        IntentFilter searchIntent = new IntentFilter(NoteConst.SEARCH_TITLE);
        registerReceiver(loadDataByDesCatePior, searchIntent);

        IntentFilter updateIntent = new IntentFilter(NoteConst.UPDATE_NOTE);
        registerReceiver(loadDataByDesCatePior, updateIntent);

        IntentFilter deletedbIntent = new IntentFilter(NoteConst.DELETE_NOTE);
        registerReceiver(deletedb, deletedbIntent);
    }

    private BroadcastReceiver deletedb = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Noteib noteib = (Noteib) intent.getSerializableExtra(NoteConst.OBJECT);
            database.deleteNote(noteib);

            Intent intent1 = new Intent(WidgetProvider.BROADCAST_WIDGET_2);
            sendBroadcast(intent1);
//        Intent intent1 = new Intent(activity, WidgetProvider.class);
//        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
//        intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
//        activity.sendBroadcast(intent1);

            Log.i("aaaaa", "delete note");
            LoadDataByDesCatePior loadData = new LoadDataByDesCatePior();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{filterCode+"", "" ,cursearch});

        }
    };

    private void setUpSpinerPiority() {

        ArrayList<String> piority = new ArrayList<>();
        piority.add(getString(R.string.pio_all));
        piority.add(getString(R.string.important));


        SpinerPiorityAdapter adapter = new SpinerPiorityAdapter(this, R.layout.item_spiner_piority, piority);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpiner.setAdapter(adapter);

        filterSpiner.setSelection(0);
        filterSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                filterCode = position;
                Intent intent = new Intent(NoteConst.SEARCH_TITLE);
                sendBroadcast(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterCode = NoteConst.DEFAULT_CATE_AND_PIO;
                Intent intent = new Intent(NoteConst.SEARCH_TITLE);
                sendBroadcast(intent);
                filter.setVisibility(View.GONE);
            }
        });






    }

    private BroadcastReceiver loadDataByDesCatePior = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadDataByDesCatePior loadData = new LoadDataByDesCatePior();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{filterCode+"", "" ,cursearch});
        }
    };

    public class LoadDataByDesCatePior extends AsyncTask<String, Void, ArrayList<Noteib>> {


        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(ArrayList<Noteib> s) {
            super.onPostExecute(s);
            NoteAdapter adapter = new NoteAdapter(SearchActivity.this, getApplicationContext(), s);
            recyclerView.setAdapter(adapter);
        }

        protected ArrayList<Noteib> doInBackground(String... strings) {
            Log.i("ssssss", " PiorPos " + strings[0]);
            Log.i("ssssss", " Category " + strings[1]);
            Log.i("ssssss", " Des " + strings[2]);

            ArrayList<Noteib> notelist;

            if (strings[0].equals(NoteConst.DEFAULT_CATE_AND_PIO+"")){
                notelist = database.getListNoteDesbyCatePior("",  strings[1], strings[2]);
            }else {
                notelist = database.getListNoteDesbyCatePior(strings[0], strings[1], strings[2]);
            }

            return notelist;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        filterItem = menu.findItem(R.id.action_filter);

        MenuItem searchitem = menu.findItem(R.id.action_search);
        searchitem.expandActionView();

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setIconifiedByDefault(true);
        search.requestFocus();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
//                    Log.d("xxxxx", "onQueryTextSubmit ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("xxxxx", "onQueryTextChange ");
                cursearch = s;
                Intent intent = new Intent(NoteConst.SEARCH_TITLE);
                sendBroadcast(intent);
                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_filter:
                filter.setVisibility(View.VISIBLE);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataByDesCatePior != null){
            unregisterReceiver(loadDataByDesCatePior);
        }
        if (deletedb!=null){
            unregisterReceiver(deletedb);
        }
    }
}
