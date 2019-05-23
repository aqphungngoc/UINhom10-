package com.icebear.speechnote;

import android.util.JsonWriter;

import com.icebear.speechnote.model.Medit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class NoteConst {

    public static String languagePreference;

    public static final String REQUEST_CODE = "ActivityType";
    public static final int CODE_EDIT_NOTE = 1;
    public static final int CODE_CREATE_TEXT_NOTE = 100;
    public static final int CODE_CREATE_VOICE_NOTE = 101;
    public static final int CODE_CREATE_TODOLIST = 102;
    public static final int CODE_CREATE_REMINDER = 103;


    public static final String OBJECT = "Object";
    public static final String CATEGORY = "Notebook";
    public static final String UPDATE_NOTE = "UpdateNote";
    public static final String UPDATE_CATEGORY = "UpdateCategory";
    public static final String UPDATE_REMINDER = "UpdateReminder";
    public static final String DELETE_NOTE = "DeleteNote";
    public static final String DELETE_CATEGORY = "DeleteCategory";
    public static final String DELETE_REMINDER = "DeleteReminder";
    public static final String ADD_CATEGORY = "AddNewCategory";
    public static final String SEARCH_TITLE = "SearchTitle";
    public static final String SEARCH_PIO = "FilterPior";

    //Do not Change this value
    public static final int DEFAULT_CATE_AND_PIO = 0;


    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_HOUR = "HH";
    public static final String DATE_FORMAT_MINUTES = "mm";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_HOUR_MINUTES = "HH:mm";

    //    Noti
    public static final String CHANNEL_ID = "channel_id";
    //   Edittext json
    public static final String MEDIT_OBJECT = "medit";
    public static final String MEDIT_LIST = "meditlist";
    public static final String MEDIT_JSON_KEY = "key";
    public static final String MEDIT_JSON_TEXT = "text";

    public static final int TEXT_JS = 1;

    public static final int CHECKBOX_CHECKED = 2;

    public static final int CHECKBOX_UNCHECK = 3;

    public static final String REMINDER_DES = "reminderdes";
    public static final String REMINDER_RINGTONE = "reminderringtone";
    public static final String REMINDER_VIBRATE = "remindervibrate";

    public static final String LANGUAGE = "Language" ;


    public static void writeJsonMeditList(Writer output, ArrayList<Medit> medits) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(output);
//        jsonWriter.beginObject();// begin root
        jsonWriter.beginArray();
        for (Medit medit : medits) {
            jsonWriter.beginObject();
            jsonWriter.name(MEDIT_JSON_KEY).value(medit.getKey());
            jsonWriter.name(MEDIT_JSON_TEXT).value(medit.getText());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
//        jsonWriter.endObject();

//        jsonWriter.name("websites").beginArray(); // begin websites
//        for(String website: websites) {
//            jsonWriter.value(website);
//        }
//        jsonWriter.endArray();// end websites
    }

    public static ArrayList<Medit> readJsonMeditList(String jsonText) {
        ArrayList<Medit> medits = new ArrayList<>();
        try {


            JSONArray jsonArray = new JSONArray(jsonText);

            for(int i=0;i < jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int key = jsonObject.getInt(MEDIT_JSON_KEY);
                String text = jsonObject.getString(MEDIT_JSON_TEXT);
                Medit medit = new Medit(i,key,text);
                medits.add(medit);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return medits;
    }

    public static String readJsonMeditText(String jsonText) {
        String res = "";
        ArrayList<Medit> medits = readJsonMeditList(jsonText);
        for (Medit medit : medits){
            res += medit.getText()+ " ";
        }
        return res;
    }



}
