package com.icebear.speechnote.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.fragment.DepthTransformation;
import com.icebear.speechnote.notefile.DatabaseHelper;
import com.icebear.speechnote.utils.Constant;
import com.icebear.speechnote.utils.Helper;
import com.icebear.speechnote.fragment.FragmentTabsAdapter;
import com.icebear.speechnote.utils.MySetting;
import com.icebear.speechnote.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;
    Intent introIntent;

    public static final String FRAGMENT_NOTE_LIST = "Notes";
    public static final String FRAGMENT_REMINDER = "Reminder";
    public static final String FRAGMENT_CATEGORY = "Notebook";

    private TabLayout tabLayout;
    private ViewPager viewPager;
//    public LinearLayout overlay;


    private Button tip;
    private FrameLayout tipbanner;

    private ImageView giftads;

    public DatabaseHelper database;

    private int tabpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setColorStatusBar(this, R.color.colorAccent);
        initLanguageList();

        tip = (Button) findViewById(R.id.tip);
        tipbanner = (FrameLayout) findViewById(R.id.tipbanner);


        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(MainActivity.this, PREF_USER_FIRST_TIME, "true"));
        introIntent = new Intent(MainActivity.this, OnboardingActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

       

        if (isUserFirstTime){
            startActivity(introIntent);
            tipbanner.setVisibility(View.VISIBLE);
        } else {
            tipbanner.setVisibility(View.GONE);
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        database = new DatabaseHelper(getApplicationContext());




        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(introIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        tabLayout.addTab(tabLayout.newTab().setText(FRAGMENT_NOTE_LIST).setIcon(R.drawable.tab_addnote));
        tabLayout.addTab(tabLayout.newTab().setText(FRAGMENT_REMINDER).setIcon(R.drawable.tab_reminder));
        tabLayout.addTab(tabLayout.newTab().setText(FRAGMENT_CATEGORY).setIcon(R.drawable.tab_notebook));

        DepthTransformation hingeTransformation = new DepthTransformation();

        final FragmentTabsAdapter adapter = new FragmentTabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setPageTransformer(true, hingeTransformation);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#007aff"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#9e9e9e"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#9e9e9e"), PorterDuff.Mode.SRC_IN);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabpos = tab.getPosition();
                tabLayout.getTabAt(tabpos).getIcon().setColorFilter(Color.parseColor("#007aff"), PorterDuff.Mode.SRC_IN);

                viewPager.setCurrentItem(tabpos);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(Color.parseColor("#9e9e9e"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




//        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
//        sendOrderedBroadcast(
//                detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);




    }



    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
                Log.i("show", getClass().toString());
                Intent intent = new Intent(MainActivity.this, ExitSplashScreen.class);
                startActivity(intent);
                finish();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.tap_back_two_time), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_feedback) {
            Helper.feedback(this);
        } else if (id == R.id.nav_policy) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.LINK_POLICY)));
        } else if (id == R.id.nav_share) {
            Helper.shareApp(this);
        } else if (id == R.id.nav_intruction) {
            startActivity(introIntent);
        } else if (id == R.id.nav_choose_language_input) {
            showLanguageDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private ArrayList<String> supportedLanguages;

    private void initLanguageList() {
        supportedLanguages = new ArrayList<String>();
        supportedLanguages.add("");
        supportedLanguages.add("af-ZA");
        supportedLanguages.add("id-ID");
        supportedLanguages.add("ms-MY");
        supportedLanguages.add("bn-BD");
        supportedLanguages.add("bn-IN");
        supportedLanguages.add("ca-ES");
        supportedLanguages.add("cs-CZ");
        supportedLanguages.add("de-DE");
        supportedLanguages.add("en-GB");
        supportedLanguages.add("en-US");
        supportedLanguages.add("es-AR");
        supportedLanguages.add("es-ES");
        supportedLanguages.add("es-US");
        supportedLanguages.add("fil-PH");
        supportedLanguages.add("fr-FR");
        supportedLanguages.add("gu-IN");
        supportedLanguages.add("is-IS");
        supportedLanguages.add("is-IS");
        supportedLanguages.add("jv-ID");
        supportedLanguages.add("km-KH");
        supportedLanguages.add("lo-LA");
        supportedLanguages.add("ne-NP");
        supportedLanguages.add("pl-PL");
        supportedLanguages.add("pt-PT");
        supportedLanguages.add("sk-SK");
        supportedLanguages.add("su-ID");
        supportedLanguages.add("sw-TZ");
        supportedLanguages.add("fi-FI");
        supportedLanguages.add("ta-IN");
        supportedLanguages.add("vi-VN");
        supportedLanguages.add("tr-TR");
        supportedLanguages.add("ur-PK");
        supportedLanguages.add("el-GR");
        supportedLanguages.add("ru-RU");
        supportedLanguages.add("ar-IL");
        supportedLanguages.add("fa-IR");
        supportedLanguages.add("hi-IN");
        supportedLanguages.add("th-TH");
        supportedLanguages.add("cmn-Hant-TW");
        supportedLanguages.add("yue-Hant-HK");
        supportedLanguages.add("ja-JP");
        supportedLanguages.add("cmn-Hans-HK");
        supportedLanguages.add("cmn-Hans-CN");

        NoteConst.languagePreference = "";
    }

    private void showLanguageDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.dialog_choose_language_input);
        dialog.setCanceledOnTouchOutside(true);

        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        Button ok = (Button) dialog.findViewById(R.id.cofirm);
        RadioButton rbst=new RadioButton(MainActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
        rbst.setText("Default");
        rg.addView(rbst);
        for(int i=1;i<supportedLanguages.size();i++){

            String[] country = supportedLanguages.get(i).split("-");
            String languageCode = country.length == 3 ? (country[0]+"-"+country[1]) : country[0];
            String countryCode = country.length == 2 ? country[1]:
                    country.length == 3 ? country[2]:"";
            Locale auxLocale = new Locale(languageCode,countryCode);
            Log.i("llllll", i + ": "+ languageCode + " size " + countryCode);

            String name = auxLocale.getDisplayLanguage(auxLocale); // English
            String countryname = auxLocale.getDisplayCountry(auxLocale);

            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            RadioButton rb =new RadioButton(MainActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(name+" (" + countryname+")");
            rg.addView(rb);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int ida = rg.getCheckedRadioButtonId();
                RadioButton rdb = (RadioButton) rg.findViewById(ida);
                int idx = rg.indexOfChild(rdb);
                NoteConst.languagePreference = supportedLanguages.get(idx);
                dialog.dismiss();

            }
        });
        dialog.show();

    }




}
