package com.androidtask.domain.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

/**
 * Created by vova on 22.06.17.
 *
 * Immutable model class for a User.
 *
 */

public final class User {
    @NonNull
    private final String mEmail;

    @NonNull
    private final String mPassword;

    @NonNull
    private final Roles mRole;

    @NonNull
    private final boolean mMarked;

    public User(@NonNull String mEmail, @NonNull String mPassword, @NonNull Roles mRole, @NonNull boolean mMarked) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mRole = mRole;
        this.mMarked = mMarked;
    }

    @NonNull
    public Roles getRole() {
        return mRole;
    }

    @NonNull
    public String getPassword() {
        return mPassword;
    }

    @NonNull
    public String getId() {
        return mEmail;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mEmail)) {
            return mEmail.substring(0,mEmail.indexOf("@"));
        } else {
            return mRole.toString();
        }
    }

    public boolean isMarked() {
        return mMarked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (mMarked != user.mMarked) return false;
        if (!mEmail.equals(user.mEmail)) return false;
        if (!mPassword.equals(user.mPassword)) return false;
        return mRole == user.mRole;

    }

    @Override
    public int hashCode() {
        int result = mEmail.hashCode();
        result = 31 * result + mPassword.hashCode();
        result = 31 * result + mRole.hashCode();
        result = 31 * result + (mMarked ? 1 : 0);
        return result;
    }
}
