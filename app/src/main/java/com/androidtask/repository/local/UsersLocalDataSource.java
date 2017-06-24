package com.androidtask.repository.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.androidtask.domain.models.Roles;
import static com.androidtask.repository.local.UsersPersistenceContract.UserEntry.*;
import com.androidtask.domain.models.User;
import com.androidtask.repository.UsersDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class UsersLocalDataSource implements UsersDataSource {

    private static UsersLocalDataSource INSTANCE;

    private UsersDbHelper mDbHelper;

    // Prevent direct instantiation.
    private UsersLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new UsersDbHelper(context);
    }

    public static UsersLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UsersLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getId());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ROLE, user.getRole().toString());
        values.put(COLUMN_MARKED, user.isMarked());

        db.insert(TABLE_NAME, null, values);

        db.close();
    }
    @Override
    public void getUser(@NonNull String email, @NonNull GetUserCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                COLUMN_EMAIL,
                COLUMN_PASSWORD,
                COLUMN_ROLE,
                COLUMN_MARKED
        };

        String selection = COLUMN_EMAIL + " LIKE ?";
        String[] selectionArgs = { email };

        Cursor c = db.query(
                TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        User user = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String id = c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL));
            String pass = c.getString(c.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String role =
                    c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE));
            boolean isMarked =
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_MARKED)) == 1;
            if (role.equalsIgnoreCase(Roles.ADMIN.toString()))
                user = new User(id,pass, Roles.ADMIN, isMarked);
            else user = new User(id,pass, Roles.USER, isMarked);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (user != null) {
            callback.onUserLoaded(user);
        } else {
            callback.onDataNotAvailable();
        }
    }

    /**
     * Note: {@link LoadUsersCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getUsers(@NonNull LoadUsersCallback callback) {
        List<User> users = new ArrayList<User>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                COLUMN_EMAIL,
                COLUMN_PASSWORD,
                COLUMN_ROLE,
                COLUMN_MARKED
        };

        Cursor c = db.query(
                TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String email = c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL));
                String password = c.getString(c.getColumnIndexOrThrow(COLUMN_PASSWORD));
                String role =
                        c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE));
                boolean marked =
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_MARKED)) == 1;
                User user;
                if (role.equalsIgnoreCase(Roles.ADMIN.toString()))
                    user = new User(email,password, Roles.ADMIN, marked);
                else user = new User(email,password, Roles.USER, marked);

                users.add(user);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (users.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onUsersLoaded(users);
        }

    }

    @Override
    public void markUser(@NonNull User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MARKED, true);

        String selection = COLUMN_EMAIL + " LIKE ?";
        String[] selectionArgs = { user.getId() };

        db.update(TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void uncheckUser(@NonNull User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MARKED, false);

        String selection = COLUMN_EMAIL + " LIKE ?";
        String[] selectionArgs = { user.getId() };

        db.update(TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void deleteCheckedUsers() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = COLUMN_MARKED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshUsers() {
        // Not required because the {@link UsersRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

}
