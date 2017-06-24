
package com.androidtask.repository.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.androidtask.domain.models.Roles;
import com.androidtask.utils.HashGenerator;
import com.androidtask.utils.MD5Generator;

import static com.androidtask.repository.local.UsersPersistenceContract.UserEntry.*;

public class UsersDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "user.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_EMAIL + TEXT_TYPE + " PRIMARY KEY," +
                    COLUMN_PASSWORD + TEXT_TYPE + COMMA_SEP +
                    COLUMN_ROLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_MARKED + BOOLEAN_TYPE +
            " )";

    public UsersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);

        bootstrapDatabase(db);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
    private void bootstrapDatabase(SQLiteDatabase db) {
        HashGenerator generator = new MD5Generator();
        ContentValues values = new ContentValues();
        //admin
        values.put(COLUMN_EMAIL, "admin@admin.com");
        values.put(COLUMN_PASSWORD, generator.generate("admin"));
        values.put(COLUMN_ROLE, Roles.ADMIN.toString());
        values.put(COLUMN_MARKED, false);
        db.insert(TABLE_NAME, null, values);
        //four users
        values.clear();
        values.put(COLUMN_EMAIL, "user1@admin.com");
        values.put(COLUMN_PASSWORD, generator.generate("user1"));
        values.put(COLUMN_ROLE, Roles.USER.toString());
        values.put(COLUMN_MARKED, false);
        db.insert(TABLE_NAME, null, values);

        values.clear();
        values.put(COLUMN_EMAIL, "user2@admin.com");
        values.put(COLUMN_PASSWORD, generator.generate("user2"));
        values.put(COLUMN_ROLE, Roles.USER.toString());
        values.put(COLUMN_MARKED, false);
        db.insert(TABLE_NAME, null, values);

        values.clear();
        values.put(COLUMN_EMAIL, "user3@admin.com");
        values.put(COLUMN_PASSWORD, generator.generate("user3"));
        values.put(COLUMN_ROLE, Roles.USER.toString());
        values.put(COLUMN_MARKED, false);
        db.insert(TABLE_NAME, null, values);

        values.clear();
        values.put(COLUMN_EMAIL, "user4@admin.com");
        values.put(COLUMN_PASSWORD, generator.generate("user4"));
        values.put(COLUMN_ROLE, Roles.USER.toString());
        values.put(COLUMN_MARKED, false);
        db.insert(TABLE_NAME, null, values);

    }
}
