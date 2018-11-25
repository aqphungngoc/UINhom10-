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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.WidgetProvider;
import com.icebear.speechnote.itemadapter.NoteAdapter;

import com.icebear.speechnote.itemadapter.SpinerPiorityAdapter;
import com.icebear.speechnote.notefile.Category;
import com.icebear.speechnote.notefile.DatabaseHelper;
import com.icebear.speechnote.notefile.Noteib;
import com.icebear.speechnote.utils.Helper;

import java.util.ArrayList;

public class CategoryDetails extends AppCompatActivity {

    public DatabaseHelper database;
    private RecyclerView recyclerView;

//    private ImageView tip;
    private ImageView close;
    private Spinner filterSpiner;
    private CardView filterCardView;
    private LinearLayout bannernonote;
    private LinearLayout bannernote;

    private Category category;

    private int filterCode = NoteConst.DEFAULT_CATE_AND_PIO;
    private String cursearch = "";


    private FloatingActionMenu fabmenu;
    private FloatingActionButton fabvoice, fabtext, fabtodo, fabreminder;
    public LinearLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Helper.setColorStatusBar(this, R.color.colorAccent);

        Intent intent1 = getIntent();
        category = (Category) intent1.getSerializableExtra(NoteConst.OBJECT);

        setTitle(category.getCategory());

        Log.i("tttttt", category.getId() + " category id ");

        database = new DatabaseHelper(getApplicationContext());


        fabmenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fabvoice = (FloatingActionButton) findViewById(R.id.fab_voicenote);
        fabtext = (FloatingActionButton) findViewById(R.id.fab_textnote);
        fabtodo = (FloatingActionButton) findViewById(R.id.fab_todolist);
        fabreminder = (FloatingActionButton) findViewById(R.id.fab_reminder);
        overlay = (LinearLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.GONE);


        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.GONE);
                fabmenu.toggle(true);
                fabtext.hide(true);
                fabtodo.hide(true);
                fabvoice.hide(true);
                fabreminder.hide(true);
            }
        });

        fabmenu.setClosedOnTouchOutside(true);

        fabmenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabmenu.isOpened()){
                    overlay.setVisibility(View.GONE);
                    fabtext.hide(true);
                    fabtodo.hide(true);
                    fabvoice.hide(true);
                    fabreminder.hide(true);
                } else {
                    overlay.setVisibility(View.VISIBLE);

                }
                fabmenu.toggle(true);
            }
        });

        final Intent intent = new Intent(CategoryDetails.this, SpeechNote.class);
        intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
        intent.putExtra(NoteConst.CATEGORY, category.getId());
        fabtodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_TODOLIST);
                startActivity(intent);
                overlay.setVisibility(View.GONE);
                fabmenu.close(true);
            }
        });
        fabtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_TEXT_NOTE);
                startActivity(intent);
                overlay.setVisibility(View.GONE);
                fabmenu.close(true);
            }
        });
        fabvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_VOICE_NOTE);
                startActivity(intent);
                overlay.setVisibility(View.GONE);
                fabmenu.close(true);
            }
        });

        fabreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryDetails.this, SpeechNote.class);
                intent.putExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_REMINDER);
                intent.putExtra(NoteConst.LANGUAGE, NoteConst.languagePreference);
                startActivity(intent);
            }
        });




        recyclerView = (RecyclerView) findViewById(R.id.list_note);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        bannernonote = (LinearLayout) findViewById(R.id.banner_no_note);
        bannernote = (LinearLayout) findViewById(R.id.banner_note);
        filterCardView = (CardView) findViewById(R.id.filter);
        close = (ImageView) findViewById(R.id.closefilter);
        filterSpiner = (Spinner) findViewById(R.id.category_spiner);
        filterCardView.setVisibility(View.GONE);
        setUpSpinerPiority();

        LoadDataByCate loadDataByCate = new LoadDataByCate();
        loadDataByCate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{category.getId()+""});

        IntentFilter deletedbIntent = new IntentFilter(NoteConst.DELETE_NOTE);
        registerReceiver(deletedb, deletedbIntent);

        IntentFilter searchIntent = new IntentFilter(NoteConst.SEARCH_TITLE);
        registerReceiver(loadDataByDesCatePior, searchIntent);

        IntentFilter updatedbIntent = new IntentFilter(NoteConst.UPDATE_NOTE);
        registerReceiver(updatebyCate, updatedbIntent);


    }



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
                filterCardView.setVisibility(View.GONE);
            }
        });

    }


    private BroadcastReceiver deletedb = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Noteib noteib = (Noteib) intent.getSerializableExtra(NoteConst.OBJECT);
            database.deleteNote(noteib);

            Intent intent1 = new Intent(WidgetProvider.BROADCAST_WIDGET_2);
            sendBroadcast(intent1);

            Log.i("aaaaa", "delete note");
            LoadDataByDesCatePior loadData = new LoadDataByDesCatePior();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{"", category.getId()+"" ,""});

        }
    };

    private BroadcastReceiver loadDataByDesCatePior = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadDataByDesCatePior loadData = new LoadDataByDesCatePior();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{filterCode+"", category.getId()+"" ,cursearch});
        }
    };

    private BroadcastReceiver updatebyCate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadDataByDesCatePior loadData = new LoadDataByDesCatePior();
            loadData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{filterCode+"", category.getId()+"" ,cursearch});
        }
    };

    public class LoadDataByDesCatePior extends AsyncTask<String, Void, ArrayList<Noteib>> {


        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(ArrayList<Noteib> s) {
            super.onPostExecute(s);

            if (s.size() == 0){
                bannernonote.setVisibility(View.VISIBLE);
                bannernote.setVisibility(View.GONE);

            } else {
                bannernonote.setVisibility(View.GONE);
                bannernote.setVisibility(View.VISIBLE);
            }

            NoteAdapter adapter = new NoteAdapter(CategoryDetails.this, getApplicationContext(), s);
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


    public class LoadDataByCate extends AsyncTask<String, Void, ArrayList<Noteib>> {


        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(ArrayList<Noteib> s) {
            super.onPostExecute(s);

            if (s.size() == 0){
                bannernonote.setVisibility(View.VISIBLE);
                bannernote.setVisibility(View.GONE);

            } else {
                bannernonote.setVisibility(View.GONE);
                bannernote.setVisibility(View.VISIBLE);
            }

            NoteAdapter adapter = new NoteAdapter(CategoryDetails.this, getApplicationContext(), s);
            recyclerView.setAdapter(adapter);
        }

        protected ArrayList<Noteib> doInBackground(String... strings) {
            Log.i("ssssss", "cateid: " + strings[0]);
            ArrayList<Noteib> notelist;
            if (strings[0].equals("0")){
                notelist = database.getAllNotes();
            }else {
                notelist = database.getListNotebyCate(strings[0]);
            }
            return notelist;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_detail, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

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
                Log.i("ssssss", "close");
                return true;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_filter:
                filterCardView.setVisibility(View.VISIBLE);
                break;
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

        if (deletedb != null){
            unregisterReceiver(deletedb);
        }

        if (updatebyCate != null){
            unregisterReceiver(updatebyCate);
        }
    }
}
