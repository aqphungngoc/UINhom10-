package com.icebear.speechnote.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.WidgetProvider;
import com.icebear.speechnote.itemadapter.SpinerCategoryAdapter;

import com.icebear.speechnote.notefile.Category;
import com.icebear.speechnote.notefile.DatabaseHelper;
import com.icebear.speechnote.notefile.Medit;
import com.icebear.speechnote.notefile.Noteib;
import com.icebear.speechnote.notefile.Reminder;
import com.icebear.speechnote.utils.Helper;


import java.io.StringWriter;
import java.util.ArrayList;


public class SpeechNote extends AppCompatActivity {


    public static final int MY_PERMISSIONS_REQUEST = 127;


    protected SpeechRecognizer speechRecognizer;

    private EditText firstMeditItem;
    protected Intent reconigerIntent;
    private ImageView voicebtn, textinput, listinput;

    //    private ImageView imageinput;

    private LinearLayout voicereconigDiaglog;
    private RecognitionProgressView recognitionProgressView;
    private TextView errorLog;
    private Button closedlg;
    private boolean dialogopen = false;
    private String languagePreference;
    //    private String textonedittext = "";
    private DatabaseHelper database;

    //category

    private Spinner categorySpiner;
    private LinearLayout important;
    private CheckBox importantcb;


    private Noteib note;
    private int request_code;


    private int mHour, mMinute;
    private String reminderdate;

    //medittool
    private LinearLayout medit;
    private ArrayList<Medit> oldmeditlist;
    private ArrayList<Medit> curmeditlist;
    private EditText curEditText;
    private int alltask = 0;
    private int curtask = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_note);
        init();

        oldmeditlist = new ArrayList<>();
        curmeditlist = new ArrayList<>();
        // add first edit text

        Intent intent = getIntent();
        request_code = intent.getIntExtra(NoteConst.REQUEST_CODE, NoteConst.CODE_CREATE_VOICE_NOTE);
//        languagePreference = intent.getStringExtra(NoteConst.LANGUAGE);
        languagePreference = NoteConst.languagePreference;
        int cateid = intent.getIntExtra(NoteConst.CATEGORY, 0);


        if (request_code == NoteConst.CODE_CREATE_TEXT_NOTE || request_code == NoteConst.CODE_CREATE_REMINDER) {
            note = new Noteib();
            note.setCategoryid(cateid);
        } else if (request_code == NoteConst.CODE_CREATE_TODOLIST) {

            note = new Noteib();
            note.setCategoryid(cateid);

            alltask++;
//            oldmeditlist.add(new Medit(pos, NoteConst.CHECKBOX_UNCHECK, ""));
            createNewMeditItem(NoteConst.CHECKBOX_UNCHECK, "");
            alltask++;
//            oldmeditlist.add(new Medit(pos1, NoteConst.CHECKBOX_UNCHECK, ""));
            createNewMeditItem(NoteConst.CHECKBOX_UNCHECK, "");

            firstMeditItem.setText(getString(R.string.todolisthint));
            firstMeditItem.requestFocus();

        } else if (request_code == NoteConst.CODE_CREATE_VOICE_NOTE) {
            note = new Noteib();
            note.setCategoryid(cateid);

        } else if (request_code == NoteConst.CODE_EDIT_NOTE) {
            note = (Noteib) intent.getSerializableExtra(NoteConst.OBJECT);
            oldmeditlist = NoteConst.readJsonMeditList(note.getDes());
            alltask = note.getAlltask();
            curtask = note.getCurtask();
            readMeditList(oldmeditlist);
            Log.i("aaaaa", oldmeditlist.size() + " size edit request code");
        }

        // use json translate here
        Log.i("aaaaa", "Id: " + note.getId() + " Des: " + note.getDes());

        textinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("map", "add text");
                if (getCurLastMeditItem().getKey() != NoteConst.TEXT_JS) {
                    createNewMeditItem(NoteConst.TEXT_JS, "");
                }

                showSoftKeyboard(SpeechNote.this);

            }
        });
        listinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("map", "add checkbox");
//                int pos = oldmeditlist.size();
                alltask++;
//                oldmeditlist.add(new Medit(pos, NoteConst.CHECKBOX_UNCHECK, ""));
                createNewMeditItem(NoteConst.CHECKBOX_UNCHECK, "");
            }
        });


        int[] colors = {
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.pink)
        };
        recognitionProgressView.setColors(colors);

        recognitionProgressView.play();
        voicebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opendlg(curEditText);

            }
        });

        closedlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closedlg();
            }
        });
//note title

        setUpSpinerCategory();

        important = (LinearLayout) findViewById(R.id.important);
        importantcb = (CheckBox) findViewById(R.id.important_checkbox);
//        important.setButtonDrawable(R.drawable.selector_cbx_audio);

        setUpPiority();

        setupFirstMeditItem();
        if (request_code == NoteConst.CODE_CREATE_VOICE_NOTE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            opendlg(curEditText);
        }
    }


    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Helper.setColorStatusBar(this, R.color.colorAccent);

        setTitle(R.string.make_note);
        voicereconigDiaglog = (LinearLayout) findViewById(R.id.voicereconigdiaglog);
        voicereconigDiaglog.setVisibility(View.GONE);
        errorLog = (TextView) findViewById(R.id.errorlog);
        closedlg = (Button) findViewById(R.id.closedialog);
        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);

        firstMeditItem = (EditText) findViewById(R.id.title_note);

        categorySpiner = (Spinner) findViewById(R.id.category_spiner);

        textinput = (ImageView) findViewById(R.id.text_input);
        listinput = (ImageView) findViewById(R.id.checklist_input);
        voicebtn = (ImageView) findViewById(R.id.fab);
//        imageinput = (ImageView) findViewById(R.id.image_input);
        medit = (LinearLayout) findViewById(R.id.medittool);

    }

    private void createNewMeditItem(int typecode, String msg) {
        final View view;
        Log.i("ssssss", "type code = " + typecode);
        final Medit medititem = new Medit(typecode, msg);
        if (typecode == NoteConst.TEXT_JS) {
            Log.i("map", "add text");
            view = getLayoutInflater().inflate(R.layout.medit_text_item, null);
        } else {

            view = getLayoutInflater().inflate(R.layout.medit_todo_item, null);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            if (typecode == NoteConst.CHECKBOX_CHECKED) {
                // Check box checked
                checkBox.setChecked(true);
            } else {
                // Check box unchecked
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        medititem.setKey(NoteConst.CHECKBOX_CHECKED);
                        curtask++;
                    } else {
                        medititem.setKey(NoteConst.CHECKBOX_UNCHECK);
                        curtask--;
                    }
                }
            });
        }

        final EditText editText = (EditText) view.findViewById(R.id.text);
        editText.setText(msg);
        editText.requestFocus();
        medit.addView(view);
        curEditText = editText;

        final String[] textOnEditText = new String[]{""};

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    curEditText = editText;
                    Log.i("aaaaa", " touch editext");
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textOnEditText[0] = charSequence.toString();
                editText.setSelection(editText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                medititem.setText(textOnEditText[0]);
//                Log.i("ssssss", oldmeditlist.get(pos).getText() + " edittext final result");
            }
        });
//        Log.i("ssssss", pos + " + " + oldmeditlist.size());
        curmeditlist.add(medititem);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL
                        && textOnEditText[0].equals("")) {
                    //this is for backspace
                    if (medititem.getKey() == NoteConst.CHECKBOX_CHECKED) {
                        curtask--;
                        alltask--;
                    } else if (medititem.getKey() == NoteConst.CHECKBOX_UNCHECK) {
                        alltask--;
                    }
                    curmeditlist.remove(medititem);
                    medit.removeView(view);
                    Log.i("ssssss", medititem.getPosition() + " + " + curmeditlist.size());
                }
                return false;
            }
        });
        final ImageView close = (ImageView) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View edview) {
                if (medititem.getKey() == NoteConst.CHECKBOX_CHECKED) {
                    curtask--;
                    alltask--;
                } else if (medititem.getKey() == NoteConst.CHECKBOX_UNCHECK) {
                    alltask--;
                }
                curmeditlist.remove(medititem);
                medit.removeView(view);
                Log.i("ssssss", medititem.getPosition() + " + " + curmeditlist.size());
            }
        });

    }

    private void setupSpeechReconizer(final EditText editText) {


        //ghi lại mọi sự thay dổi trên edittext
//        final String[] textOnEditText = new String[]{""};
        // ghi lại mọi sự thay dổi của speech note
        final String[] textOnSpeech = new String[]{""};
        // ghi lại mọi sự thay dổi tạm thời của speech note

        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
                textOnSpeech[0] = editText.getText().toString();

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                closedlg();
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "Speech Recognizer is busy";

                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "Server error";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Speech Recognizer cannot understand you";
                        break;
                }
                errorLog.setText(message);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(textOnSpeech[0] + " " + matches.get(0));
                editText.setSelection(editText.getText().length());
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String tmp = textOnSpeech[0];
                String a = "";
                tmp += matches.get(0);
//                Log.i("ssssss", a);
                editText.setText(tmp);

                editText.setSelection(editText.getText().length());

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

    }


    private void closedlg() {

        if (dialogopen){
            dialogopen = false;
            voicereconigDiaglog.setVisibility(View.GONE);
//        voicebtn.setVisibility(View.VISIBLE);
            voicebtn.setClickable(true);
            speechRecognizer.stopListening();
        }

    }

    private void opendlg(EditText editText) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST);
                return;
            }
        }
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {

            String last = curEditText.getText().toString();
            if (last.length() != 0) {
                last = last.substring(last.length() - 1);
                if (!last.equals("\n")) {
                    curEditText.append(".\n");
                }

            }


            dialogopen = true;
            voicereconigDiaglog.setVisibility(View.VISIBLE);
            voicereconigDiaglog.animate()
                    .translationY(0)
                    .setDuration(300);
//        voicebtn.setVisibility(View.GONE);
            voicebtn.setClickable(false);
            recognitionProgressView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRecognition();
                }
            }, 50);
            setupSpeechReconizer(editText);
        } else {
            new AlertDialog.Builder(SpeechNote.this).
                    setTitle(getString(R.string.app_install_requirement)).
                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS), 0);
                        }
                    }).
                    setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).
                    setCancelable(true).
                    show();
        }

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (speechRecognizer != null) {
            try {
                speechRecognizer.destroy();
            } catch (Throwable exc) {

            } finally {
                speechRecognizer = null;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (dialogopen) {
            closedlg();
        } else {
            if (request_code == NoteConst.CODE_CREATE_REMINDER) {
                timedialog();
            } else {
                onSave(note);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.speech_note, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.action_about).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (request_code == NoteConst.CODE_CREATE_REMINDER) {
                    timedialog();
                } else {
                    onSave(note);
                }
                break;
            case R.id.action_remider:
                timedialog();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSave(Noteib note) {
        if (curmeditlist.size() != 0) {
            note.setDespreview(curmeditlist.get(0).getText());
        } else {
            note.setDespreview(note.getTitle());
        }

        if (note.getTitle().equals("") && curmeditlist.size() == 0) {

        } else {
            note.setTitle(firstMeditItem.getText().toString());
            note.setDes(getNoteDes());
            note.setAlltask(alltask);
            note.setCurtask(curtask);
            note.setCreatedtime(System.currentTimeMillis());

            if (note.getId() == -1) {
                database.addNote(note);
            } else {
                database.updateNote(note);
            }


            Intent intent = new Intent(NoteConst.UPDATE_NOTE);
            sendBroadcast(intent);
            Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();


            Intent intent1 = new Intent(WidgetProvider.BROADCAST_WIDGET_2);
            sendBroadcast(intent1);


        }
        finish();

    }

    private String getNoteDes() {
        StringWriter output = new StringWriter();
        try {

            NoteConst.writeJsonMeditList(output, curmeditlist);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonText = output.toString();

        return jsonText;
    }

    private void setUpPiority() {
        int prevcateId = note.getPriority();

        if (prevcateId == NoteConst.DEFAULT_CATE_AND_PIO) {
            importantcb.setChecked(false);
        } else {
            importantcb.setChecked(true);
        }
        important.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (importantcb.isChecked()) {
                    note.setPriority(NoteConst.DEFAULT_CATE_AND_PIO);
                    importantcb.setChecked(false);
                } else {
                    note.setPriority(1);
                    importantcb.setChecked(true);
                }
            }
        });


    }

    private void setUpSpinerCategory() {
        database = new DatabaseHelper(getApplicationContext());
        final ArrayList<Category> categories = new ArrayList<Category>();
        categories.add(new Category(0, getString(R.string.choose_Note_Book),0));
        final ArrayList cates = database.getAllCategories();
        categories.addAll(cates);
        SpinerCategoryAdapter dataAdapter = new SpinerCategoryAdapter(this,
                R.layout.item_spiner_notebook, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpiner.setAdapter(dataAdapter);
        int prevcateId = dataAdapter.getCategoryPosition(note.getCategoryid());
        categorySpiner.setSelection(prevcateId);
        categorySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Category category = categories.get(position);
                Log.i("tttttt", category.getId() + " cateid");
                note.setCategoryid(category.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void timedialog() {

        if (note.getTitle().equals("") && curmeditlist.size() == 0) {
            finish();
        } else {
            Long curtime = System.currentTimeMillis();

            String h = Helper.getDate(curtime, NoteConst.DATE_FORMAT_HOUR);
            mHour = Integer.parseInt(h);
            h = Helper.getDate(curtime, NoteConst.DATE_FORMAT_MINUTES);
            mMinute = Integer.parseInt(h);
            reminderdate = Helper.getDate(curtime, NoteConst.DATE_FORMAT_DAY);

            TimePickerDialog timePickerDialog = new TimePickerDialog(SpeechNote.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            saveandmakeAlarm(reminderdate, hourOfDay, minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.setTitle("Set Reminder for this Note");
            timePickerDialog.show();
        }
    }

    public void saveandmakeAlarm(String reminderdate, int hourOfDay, int minute) {
        String mydate = reminderdate + " " + hourOfDay + ":" + minute;
        Long time = Helper.getTimeMilis(mydate, NoteConst.DATE_FORMAT);
        setRemiderSettingDialog(time);


    }

    private void setRemiderSettingDialog(final Long time) {
        final Dialog dlg = new Dialog(SpeechNote.this);
        dlg.requestWindowFeature(1);
        dlg.setContentView(R.layout.dialog_reminder_option);
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        final ImageView ringtoneimg = (ImageView) dlg.findViewById(R.id.ringtone_img);
        final ImageView vibrateimg = (ImageView) dlg.findViewById(R.id.vibrate_img);
        final ImageView repeatimg = (ImageView) dlg.findViewById(R.id.repeate_img);
        Switch ringtone = (Switch) dlg.findViewById(R.id.ringtone);
        final Switch vibrate = (Switch) dlg.findViewById(R.id.vibrate);
        Switch repeate = (Switch) dlg.findViewById(R.id.repeate);

        final Reminder reminder = new Reminder();
        reminder.setNoteid(note.getId());
        reminder.setNotedes(note.getTitle());
        reminder.setTime(time);
        ringtone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    reminder.setRingtone(1);
                    ringtoneimg.setImageResource(R.drawable.volume_on);
                } else {
                    reminder.setRingtone(0);
                    ringtoneimg.setImageResource(R.drawable.volume_off);
                }
            }
        });

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    reminder.setVibrate(1);
                    vibrateimg.setImageResource(R.drawable.vibrate_on);
                } else {
                    reminder.setVibrate(0);
                    vibrateimg.setImageResource(R.drawable.vibrate_off);
                }
            }
        });
        repeate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    reminder.setRepeatable(1);
                    repeatimg.setImageResource(R.drawable.repeat_on);
                } else {
                    reminder.setRepeatable(0);
                    repeatimg.setImageResource(R.drawable.repeat_off);
                }
            }
        });


        Button cancel = (Button) dlg.findViewById(R.id.cancel);
        Button ok = (Button) dlg.findViewById(R.id.cofirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.dismiss();
                database.addRemider(reminder);
                Intent intent = new Intent(NoteConst.UPDATE_REMINDER);
                sendBroadcast(intent);
                onSave(note);

            }
        });


    }


    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void showSoftKeyboard(Activity activity) {
        closedlg();
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private Medit getCurLastMeditItem() {
        if (curmeditlist.size() != 0) {
            return curmeditlist.get(curmeditlist.size() - 1);
        }
        return new Medit();
    }

    private void setupFirstMeditItem() {
        if (request_code == NoteConst.CODE_EDIT_NOTE) {
            firstMeditItem.setText(note.getTitle());
        }
        final String[] textOnEditText = new String[]{""};
        curEditText = firstMeditItem;
        firstMeditItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    curEditText = firstMeditItem;
                    Log.i("aaaaa", " touch editext");
                }
                return false;
            }
        });
        firstMeditItem.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textOnEditText[0] = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                note.setTitle(textOnEditText[0]);
//                Log.i("ssssss", note.getTitle() + " edittext final result");
            }
        });
        Log.i("ssssss", note.getTitle() + " + " + oldmeditlist.size());
    }

    private void readMeditList(ArrayList<Medit> meditlist) {
        for (int i = 0; i < meditlist.size(); i++) {
            Medit medit = meditlist.get(i);
            createNewMeditItem(medit.getKey(), medit.getText());
        }
    }

    private final String ggapp = "com.google.android.googlequicksearchbox";

    public static boolean isPackageInstalled(final Context ctx, final String packageName) {
        try {
            ctx.getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void startRecognition() {


        // initialise
        Log.i("languuu", " gg support");

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SpeechNote.this);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        reconigerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        reconigerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        reconigerIntent.putExtra(RecognizerIntent.EXTRA_WEB_SEARCH_ONLY, "false");
        reconigerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "2000");
        reconigerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        reconigerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        Log.i("languuuuu", languagePreference);
        if (languagePreference != null && !languagePreference.equals("")) {
            reconigerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePreference);
            reconigerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePreference);
            reconigerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePreference);
        }
        speechRecognizer.startListening(reconigerIntent);
        hideSoftKeyboard(SpeechNote.this);
    }
}
