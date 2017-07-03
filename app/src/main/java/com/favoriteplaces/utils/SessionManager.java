package com.favoriteplaces.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.favoriteplaces.domain.models.Roles;

/**
 * Created by vova on 17.05.17.
 */

public class SessionManager {
    private static final String PATH = "path";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;
    private static SessionManager INSTANCE = null;
    private static final String PREF_NAME = "CredentialStorage";
    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String EMAIL = "email";
    private static final String ROLE = "role";

    private SessionManager(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }
    public static SessionManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager(context);
        }
        return INSTANCE;
    }
    public void createLoginSession(String email, Roles role){
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.putString(EMAIL, email);
        editor.putString(ROLE, role.toString());
        editor.commit();
    }
    public Roles getUserRole(){
        if (preferences.getString(ROLE, Roles.USER.toString()).equalsIgnoreCase(Roles.ADMIN.toString()))
                return Roles.ADMIN;
        else    return Roles.USER;
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

    public void saveCurrentPhotoPath(String mCurrentPhotoPath) {
        editor.putString(PATH,mCurrentPhotoPath);
        editor.commit();
    }

    public String getPath() {
        return preferences.getString(PATH,null);
    }
}
