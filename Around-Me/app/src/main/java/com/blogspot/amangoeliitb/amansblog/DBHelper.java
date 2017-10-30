package com.blogspot.amangoeliitb.amansblog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "location_data" ;
    public static final String TABLE_NAME = "location_table" ;

    public static final String COLUMN_ID = "id" ;

    public static final String COLUMN_MESSAGE = "message" ;
    public static final String COLUMN_LATITUDE = "latitude" ;
    public static final String COLUMN_LONGITUDE = "longitude" ;
    public static final String COLUMN_DATE = "date" ;
    public static final String COLUMN_TYPE = "type";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1) ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_LATITUDE + " TEXT, " +
                COLUMN_LONGITUDE + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertMessage(String message, String latitude, String longitude, String date , String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MESSAGE, message);
        contentValues.put(COLUMN_LATITUDE, latitude);
        contentValues.put(COLUMN_LONGITUDE, longitude);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TYPE, type);
        db.insert(TABLE_NAME, null, contentValues);
        return true ;
    }

    public void clearDatabase() {
        this.getReadableDatabase().execSQL("DELETE FROM " + TABLE_NAME);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase() ;
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public int getSOScount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE TYPE = ?", new String[]{"sos"});
        int cnt = res.getCount();
        res.close();
        return cnt;
    }

    public int getGeneralCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE TYPE = ?", new String[]{"general"});
        int cnt = res.getCount();
        res.close();
        return cnt;
    }

    public ArrayList <Message> getAllMessages(String type) {
        ArrayList <Message> each_message = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if(type.equalsIgnoreCase("sos"))
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE TYPE = ?", new String[]{"sos"});
        else
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE TYPE = ?", new String[]{"general"});
        Log.v("Fetched:", "From the database1");
        res.moveToFirst();
        while(!res.isAfterLast()) {
            Message message = new Message();
            message.message = res.getString(res.getColumnIndex(COLUMN_MESSAGE));
            message.latitude = res.getString(res.getColumnIndex(COLUMN_LATITUDE));
            message.longitude = res.getString(res.getColumnIndex(COLUMN_LONGITUDE));
            message.date = res.getString(res.getColumnIndex(COLUMN_DATE));
            each_message.add(message);
            res.moveToNext();
        }
        res.close();
        Log.v("Fetched:", "From the database");
        return each_message;
    }
}
