package com.icebear.speechnote.notefile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String tag = "SQLite";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NoteManager";



    //Table 1
    private static final String TABLE_NOTE = "Note";
    private static final String ID_NOTE ="NoteId";
    private static final String COLUMN_TITLE ="Title";
    private static final String COLUMN_DES = "Description";
    private static final String COLUMN_PIORITY = "Done"; // muc do khan cap 1-4
    private static final String COLUMN_CREATED_DATE= "Createdate";
    private static final String CATEGORY_ID = "CategoryId";
    private static final String COLUMN_ALL_TASK = "Alltask";
    private static final String COLUMN_CURTASK = "Curtask";
    private static final String COLUMN_DES_PREVIEW = "Preview";



    //Table 2
    private static final String TABLE_CATEGORY = "Category";
    private static final String ID_CATEGORY = "CategoryId";
    private static final String COLUMN_CATEGORY ="Category";//7

    //Table 3
    private static final String TABLE_REMINDER = "Reminder";
    private static final String ID_REMINDER = "ReminderId";
    private static final String NOTE_ID = "NoteId";
    private static final String COLUMN_NOTE_DES = "NoteDes";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_RINGTONE = "Ringtone";
    private static final String COLUMN_VIBRATE = "Vibrate";
    private static final String COLUMN_REPEATABLE = "Repeatable";





    public DatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "create table " + TABLE_NOTE+ " ("
                + ID_NOTE + " integer primary key autoincrement, "
                + COLUMN_TITLE + " text, "
                + COLUMN_DES + " text, "
                + COLUMN_PIORITY + " integer, "
                + COLUMN_CREATED_DATE+ " integer, "
                + CATEGORY_ID + " integer, "
                + COLUMN_ALL_TASK + " integer, "
                + COLUMN_CURTASK + " integer, "
                + COLUMN_DES_PREVIEW + " text "
                + ")";
        db.execSQL(script);

        String script2 = "create table " + TABLE_CATEGORY + " ("
                + ID_CATEGORY + " integer primary key autoincrement, "
                + COLUMN_CATEGORY + " text "
                + ")";
        db.execSQL(script2);

        String script3 = "create table " + TABLE_REMINDER + " ("
                + ID_REMINDER + " integer primary key autoincrement, "
                + NOTE_ID + " integer, "
                + COLUMN_NOTE_DES + " text, "
                + COLUMN_TIME+ " long, "
                + COLUMN_RINGTONE + " integer, "
                + COLUMN_VIBRATE + " integer, "
                + COLUMN_REPEATABLE + " integer"
                +")";
        db.execSQL(script3);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.i(tag, "MyDatabaseHelper.onUpgrade ... ");
        String script = "drop table if exists " + TABLE_NOTE;
        db.execSQL(script);
        String script2 = "drop table if exists " + TABLE_CATEGORY;
        db.execSQL(script2);
        String script3 = "drop table if exists " + TABLE_REMINDER;
        db.execSQL(script3);
        onCreate(db);
    }


    public void addNote(Noteib note) {
        //Log.i(tag, "MyDatabaseHelper.addNote ... " + note.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_DES, note.getDes());
        values.put(COLUMN_PIORITY, note.getPriority());
        values.put(COLUMN_CREATED_DATE, note.getCreatedtime());
        values.put(CATEGORY_ID, note.getCategoryid());
        values.put(COLUMN_ALL_TASK, note.getAlltask());
        values.put(COLUMN_CURTASK, note.getCurtask());
        values.put(COLUMN_DES_PREVIEW, note.getDespreview());
        db.insert(TABLE_NOTE, null, values);

        // Đóng kết nối database.
        db.close();
    }
    public ArrayList<Noteib> getAllNotes() {
        ArrayList<Noteib> noteList = new ArrayList<Noteib>();
        // Select All Query
//        + " inner join "+ TABLE_CATEGORY
//                +" on " + TABLE_NOTE + "."+ COLUMN_CATEGORYID + " = " + TABLE_CATEGORY +"."+ COLUMN_ID
        String selectQuery = "select * from " + TABLE_NOTE
                +" order by " + TABLE_NOTE + "." + NOTE_ID +" desc";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Noteib note = new Noteib();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }
//        for (int i=0; i<noteList.size(); i++){
//            Log.d("xxxx", noteList.get(i).getType()+"");
//        }
        db.close();
        return noteList;
    }

    public Noteib getNotebyId(int id){
       // Log.i(tag, "MyDatabaseHelper.getNotebyID ... " );
        // Select All Query
        String selectQuery = "select * from " + TABLE_NOTE + " where " + ID_NOTE + " =?" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Noteib note = new Noteib();
        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor == null) {
            return note;
        }
        if (cursor.moveToFirst()) {
            do {
                //Log.d("xxxxxxxx", cursor.getString(1)+" say HI " + cursor.getInt(0)+" pp " + cursor.getString(2));
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
            } while (cursor.moveToNext());
        }
            //Log.d("xxxx", note.getTitle()+"");
        db.close();
        return note;
    }

    public Noteib getNotebyCategory(String category){
        Noteib note = new Noteib();
        String selectQuery = "select * from " + TABLE_NOTE + "inner join "+ TABLE_CATEGORY
                +" on " + TABLE_NOTE + "."+ CATEGORY_ID + " = " + TABLE_CATEGORY +"."+ ID_CATEGORY
                + " where " + COLUMN_CATEGORY + " =? " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {category});
        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor == null) {
            return note;
        }
        if (cursor.moveToFirst()) {
            do {
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
            } while (cursor.moveToNext());
        }


        //Log.d("xxxx", note.getTitle()+"");
        db.close();
        return note;
    }



    public ArrayList<Noteib> getListNotebyDes(String title){
        //Log.i(tag, "MyDatabaseHelper.getListNotebyTitle ... " );
        ArrayList<Noteib> noteList = new ArrayList<Noteib>();
        // Select All Query
        String selectQuery = "select * from " + TABLE_NOTE + " where " +
                COLUMN_TITLE + " like '%"+ title+"%' or "+ COLUMN_DES+ " like '%"+ title+"%' order by "+ ID_NOTE  + " desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor == null) {
            return noteList;
        }
        if (cursor.moveToFirst()) {
            do {
                Noteib note = new Noteib();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }

//        for (int i=0; i<noteList.size(); i++){
//            Log.d("xxxx", noteList.get(i).getTitle()+"");
//        }
        db.close();
        return noteList;
    }

    //filter
    public ArrayList<Noteib> getListNotebyPior(String piorpos){
        //Log.i(tag, "MyDatabaseHelper.getListNotebyTitle ... " );
        ArrayList<Noteib> noteList = new ArrayList<Noteib>();
        // Select All Query
        String selectQuery = "select * from " + TABLE_NOTE + " where " +
                COLUMN_PIORITY + " = " +  piorpos + " order by "+ ID_NOTE  + " desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor == null) {
            return noteList;
        }
        if (cursor.moveToFirst()) {
            do {
                Noteib note = new Noteib();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }

//        for (int i=0; i<noteList.size(); i++){
//            Log.d("xxxx", noteList.get(i).getTitle()+"");
//        }
        db.close();
        return noteList;
    }

    public ArrayList<Noteib> getListNotebyCate(String cateid) {

        //Log.i(tag, "MyDatabaseHelper.getListNotebyTitle ... " );
        ArrayList<Noteib> noteList = new ArrayList<Noteib>();
        // Select All Query
        String selectQuery = "select * from " + TABLE_NOTE + " where " +

                CATEGORY_ID + " = " +  cateid + " order by "+ ID_NOTE  + " desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor == null) {
            return noteList;
        }
        if (cursor.moveToFirst()) {
            do {
                Noteib note = new Noteib();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return noteList;
    }

    //Search in CategoryDetails
    public ArrayList<Noteib> getListNoteDesbyCatePior(String piorpos, String cateid, String title) {
        //Log.i(tag, "MyDatabaseHelper.getListNotebyTitle ... " );
        ArrayList<Noteib> noteList = new ArrayList<Noteib>();
        // Select All Query
        String selectQuery;
        String searchquery = "( " + COLUMN_TITLE + " like '%"+ title+"%' or "+ COLUMN_DES+ " like '%"+ title+"%' ) " ;
        String cateQuery = cateid.equals("")? "" : ( " and " + CATEGORY_ID + " = " +  cateid ) ;
        String piorQuery = piorpos.equals("")? "" : ( " and " + COLUMN_PIORITY + " = " +  piorpos + " " ) ;

        selectQuery = "select * from " + TABLE_NOTE + " where " +
                searchquery+
                cateQuery +
                piorQuery +
                " order by "+ ID_NOTE  + " desc";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor == null) {
            return noteList;
        }
        if (cursor.moveToFirst()) {
            do {
                Noteib note = new Noteib();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDes(cursor.getString(2));
                note.setPriority(cursor.getInt(3));
                note.setCreatedtime(cursor.getLong(4));
                note.setCategoryid(cursor.getInt(5));
                note.setAlltask(cursor.getInt(6));
                note.setCurtask(cursor.getInt(7));
                note.setDespreview(cursor.getString(8));
                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return noteList;
    }

    public int getNotesCount() {
        //Log.i(tag, "MyDatabaseHelper.getNotesCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count

        return count;
    }

    public int updateNote(Noteib note) {
        //Log.i(tag, "MyDatabaseHelper.updateNote ... "  + note.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_DES, note.getDes());
        values.put(COLUMN_PIORITY, note.getPriority());
        values.put(COLUMN_CREATED_DATE, note.getCreatedtime());
        values.put(CATEGORY_ID, note.getCategoryid());
        values.put(COLUMN_ALL_TASK, note.getAlltask());
        values.put(COLUMN_CURTASK, note.getCurtask());
        values.put(COLUMN_DES_PREVIEW, note.getDespreview());
        // updating row
        return db.update(TABLE_NOTE, values, ID_NOTE + " = ?",
                new String[] {String.valueOf(note.getId())});
    }

    public void deleteNote(Noteib note) {

        SQLiteDatabase db = this.getWritableDatabase();

        String script = "delete from "+TABLE_NOTE+" where "+ID_NOTE+" = ?";
        db.execSQL(script, new String[]{String.valueOf(note.getId())});
        db.close();
    }


    public void addCategory(Category category) {
        //Log.i(tag, "MyDatabaseHelper.addNote ... " + note.getTitle());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, category.getCategory());

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TABLE_CATEGORY, null, values);
        // Đóng kết nối database.
        db.close();
    }

    public ArrayList<Category> getAllCategories() {
        //Log.i(tag, "MyDatabaseHelper.getAllNotes ... " );


        ArrayList<Category> categories = new ArrayList<Category>();
        // Select All Query "n."+ ID_NOTE +
        String selectQuery = "select count(" +"n."+ ID_NOTE + ") as notecount, " + "c."+ ID_CATEGORY +" , " + "c."+COLUMN_CATEGORY
                + " from " + TABLE_CATEGORY +" c left join " + TABLE_NOTE  + " n on " + " c." + ID_CATEGORY + " = n." + CATEGORY_ID
                + " group by " + " c." + ID_CATEGORY + " order by " + " c." + ID_CATEGORY + " asc";
//                +" order by " + ID_CATEGORY +" desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.i("notebook" , selectQuery);
        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(1));
                category.setCategory(cursor.getString(2));
                category.setNotecount(cursor.getInt(0));
                Log.i("notebook" , category.toString());
                // Thêm vào danh sách.
                categories.add(category);
            } while (cursor.moveToNext());
        }

        db.close();

        return categories;
    }


    public int updateCategory(Category category) {
        //Log.i(tag, "MyDatabaseHelper.updateNote ... "  + note.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_CATEGORY, category.getCategory());

        // updating row
        return db.update(TABLE_CATEGORY, values, ID_CATEGORY + " = ?",
                new String[] {String.valueOf(category.getId())});
    }

    public void deleteCategory(Category category) {
        // Log.i(tag, "MyDatabaseHelper.deleteNote ... " + note.getTitle() );

        SQLiteDatabase db = this.getWritableDatabase();

        String script = "delete from "+TABLE_CATEGORY+" where "+ID_CATEGORY+"=?";

        db.execSQL(script, new String[]{String.valueOf(category.getId())});
        db.close();
    }

    public int getCategoryCount(){
        String countQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        db.close();
        return count;
    }

    public ArrayList<Reminder> getAllReminder() {
        ArrayList<Reminder> reminders = new ArrayList<Reminder>();
        // Select All Query
        String selectQuery = "select * from " + TABLE_REMINDER
                +" order by " + ID_REMINDER +" desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(0));
                reminder.setNoteid(cursor.getInt(1));
                reminder.setNotedes(cursor.getString(2));
                reminder.setTime(cursor.getLong(3));
                Log.i("xxxxx", cursor.getLong(3)+ " time:");
                reminder.setRingtone(cursor.getInt(4));
                reminder.setVibrate(cursor.getInt(5));
                reminder.setRepeatable(cursor.getInt(6));
                // Thêm vào danh sách.
                reminders.add(reminder);
            } while (cursor.moveToNext());
        }
        db.close();
        return reminders;
    }

    public void addRemider(Reminder reminder) {
        //Log.i(tag, "MyDatabaseHelper.addNote ... " + note.getTitle());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_ID, reminder.getNoteid());
        values.put(COLUMN_NOTE_DES, reminder.getNotedes());
        values.put(COLUMN_TIME, reminder.getTime());
        Log.i("xxxxx", reminder.getTime()+ " time:");
        values.put(COLUMN_RINGTONE, reminder.getRingtone());
        values.put(COLUMN_VIBRATE, reminder.getVibrate());
        values.put(COLUMN_REPEATABLE, reminder.getRepeatable());

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TABLE_REMINDER, null, values);
        // Đóng kết nối database.
        db.close();
    }

    public int updateReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_ID, reminder.getNoteid());
        values.put(COLUMN_NOTE_DES, reminder.getNotedes());
        values.put(COLUMN_TIME, reminder.getTime());
        values.put(COLUMN_RINGTONE, reminder.getRingtone());
        values.put(COLUMN_VIBRATE, reminder.getVibrate());
        values.put(COLUMN_REPEATABLE, reminder.getRepeatable());
        return db.update(TABLE_REMINDER, values, ID_REMINDER + " = ?",
                new String[] {String.valueOf(reminder.getId())});
    }

    public void deleteReminder(Reminder reminder) {
        // Log.i(tag, "MyDatabaseHelper.deleteNote ... " + note.getTitle() );

        SQLiteDatabase db = this.getWritableDatabase();

        String script = "delete from "+TABLE_REMINDER+" where "+ID_REMINDER+" = ?";

        db.execSQL(script, new String[]{String.valueOf(reminder.getId())});
        db.close();
    }



}
