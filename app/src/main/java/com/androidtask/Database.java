package com.androidtask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by vova on 17.05.17.
 */

class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user_credentials";
    private static final String TABLE_NAME = "credentials";

    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private SQLiteDatabase database;

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_EMAIL + " VARCHAR(129) PRIMARY KEY, "
                + COL_PASSWORD + " TEXT);";
        db.execSQL(CREATE_LOGIN_TABLE);
        this.database = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public void put(String email, String password) {
        database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public boolean containsKey(String key) {
        String selectQuery = "SELECT "+ COL_EMAIL +" FROM " + TABLE_NAME;

        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (TextUtils.equals(key, cursor.getString(0)))
                    return true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return false;
    }

    public String get(String key){
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (TextUtils.equals(key, cursor.getString(0)))
                    return cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return "";
    }
}
