package com.androidtask.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.androidtask.domain.models.User;
import com.androidtask.repository.UsersDataSource;
import com.androidtask.repository.local.persistence.DaoMaster;
import com.androidtask.repository.local.persistence.DaoSession;
import com.androidtask.repository.local.persistence.UserDao;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class UsersLocalDataSource implements UsersDataSource {

    private static UsersLocalDataSource INSTANCE;
    private static final String DATABASE_NAME = "user.db";
    private DaoSession mDaoSession;
    // Prevent direct instantiation.
    private UsersLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    public static UsersLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UsersLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void insertUser(@NonNull User user) {
        checkNotNull(user);
        UserDao userDao = mDaoSession.getUserDao();
        userDao.insert(user);
    }
    @Override
    public void getUser(@NonNull String email, @NonNull GetUserCallback callback) {
        UserDao userDao = mDaoSession.getUserDao();
        User user = userDao.load(email);

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

        UserDao userDao = mDaoSession.getUserDao();
        List<User> users = userDao.loadAll();

        if (users.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onUsersLoaded(users);
        }

    }

    @Override
    public void markUser(@NonNull User user) {
        UserDao userDao = mDaoSession.getUserDao();
        userDao.update(user);
    }

    @Override
    public void uncheckUser(@NonNull User user) {
        UserDao userDao = mDaoSession.getUserDao();
        userDao.update(user);
    }

    @Override
    public void deleteCheckedUsers() {
        UserDao userDao = mDaoSession.getUserDao();
        userDao.queryBuilder()
                .where(UserDao.Properties.MMarked.eq(true))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        mDaoSession.clear();
    }

    @Override
    public void refreshUsers() {
        // Not required because the {@link UsersRepository} handles the logic of refreshing the
        // users from all the available data sources.
    }

}
