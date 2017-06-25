package com.androidtask.repository;

import android.support.annotation.NonNull;

import com.androidtask.domain.models.User;

import java.util.List;

/**
 * Main entry point for accessing users data.
 * <p>
 * For simplicity, only getUsers() and getUser() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new user is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface UsersDataSource {

    interface LoadUsersCallback {

        void onUsersLoaded(List<User> users);

        void onDataNotAvailable();
    }

    interface GetUserCallback {

        void onUserLoaded(User user);

        void onDataNotAvailable();
    }

    void insertUser(@NonNull User user);

    void getUser(@NonNull String email, @NonNull GetUserCallback callback);

    void getUsers(@NonNull LoadUsersCallback callback);

    void markUser(@NonNull User user);

    void uncheckUser(@NonNull User user);

    void deleteCheckedUsers();

    void refreshUsers();

}
