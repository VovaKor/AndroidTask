package com.androidtask;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vova on 17.05.17.
 */

public class SessionManager {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "CredentialStorage";
    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String EMAIL = "email";

    public SessionManager(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void createLoginSession(String email){
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public String getUserEmail(){
        return preferences.getString(EMAIL, null);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn(){
        return preferences.getBoolean(IS_LOGGEDIN, false);
    }
}
