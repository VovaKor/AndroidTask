package com.androidtask.repository;

import android.support.annotation.NonNull;

import com.androidtask.domain.models.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load users from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class UsersRepository implements UsersDataSource {

    private static UsersRepository INSTANCE = null;

    private final UsersDataSource mUsersLocalDataSource;

    Map<String, User> mCachedUsers;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private UsersRepository(@NonNull UsersDataSource usersLocalDataSource) {

        mUsersLocalDataSource = checkNotNull(usersLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param usersLocalDataSource  the device storage data source
     * @return the {@link UsersRepository} instance
     */
    public static UsersRepository getInstance(UsersDataSource usersLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new UsersRepository(usersLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void updateUser(@NonNull User user) {
        checkNotNull(user);

        mUsersLocalDataSource.updateUser(user);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), user);
    }

    @Override
    public void insertUser(@NonNull User user) {
        checkNotNull(user);

        mUsersLocalDataSource.insertUser(user);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), user);
    }
    @Override
    public void getUser(@NonNull String email, @NonNull final GetUserCallback callback) {
        checkNotNull(email);
        checkNotNull(callback);

        User cachedUser = getUserWithId(email);

        // Respond immediately with cache if available
        if (cachedUser != null) {
            callback.onUserLoaded(cachedUser);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mUsersLocalDataSource.getUser(email, new GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                callback.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Gets users from cache, local data source (SQLite)
     * Note: {@link LoadUsersCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getUsers(@NonNull final LoadUsersCallback callback) {
        checkNotNull(callback);
        // Respond immediately with cache if available and not dirty
        if (mCachedUsers != null && !mCacheIsDirty) {
            callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
            return;
        }
            mUsersLocalDataSource.getUsers(new LoadUsersCallback() {
                @Override
                public void onUsersLoaded(List<User> users) {
                    refreshCache(users);
                    callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
                }

                @Override
                public void onDataNotAvailable() {

                }
            });

    }

    @Override
    public void markUser(@NonNull User user) {
        checkNotNull(user);

        mUsersLocalDataSource.markUser(user);

        User markedUser = new User(user.getId(), user.getPassword(), user.getRole(), true);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), markedUser);
    }

    @Override
    public void uncheckUser(@NonNull User user) {
        checkNotNull(user);

        mUsersLocalDataSource.uncheckUser(user);

        User uncheckedUser = new User(user.getId(), user.getPassword(), user.getRole(), false);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), uncheckedUser);
    }

    @Override
    public void deleteCheckedUsers() {

        mUsersLocalDataSource.deleteCheckedUsers();

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, User>> it = mCachedUsers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            if (entry.getValue().isMarked()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshUsers() {
        mCacheIsDirty = true;
    }

    private void refreshCache(List<User> users) {
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.clear();
        for (User user : users) {
            mCachedUsers.put(user.getId(), user);
        }
        mCacheIsDirty = false;
    }

    private User getUserWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedUsers == null || mCachedUsers.isEmpty()) {
            return null;
        } else {
            return mCachedUsers.get(id);
        }
    }

}
