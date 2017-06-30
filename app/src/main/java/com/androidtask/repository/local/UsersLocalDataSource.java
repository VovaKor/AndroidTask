package com.androidtask.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.androidtask.domain.models.Roles;
import com.androidtask.domain.models.User;
import com.androidtask.domain.models.UserDetails;
import com.androidtask.repository.UsersDataSource;
import com.androidtask.repository.local.persistence.DaoMaster;
import com.androidtask.repository.local.persistence.DaoSession;
import com.androidtask.repository.local.persistence.FavoritePlaceDao;
import com.androidtask.repository.local.persistence.UserDao;
import com.androidtask.utils.HashGenerator;
import com.androidtask.utils.MD5Generator;

import org.greenrobot.greendao.database.Database;

import java.util.List;
import java.util.UUID;

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
        bootstrapDB();
    }

    public static UsersLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UsersLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void updateUser(@NonNull User user) {
        checkNotNull(user);
        UserDao userDao = mDaoSession.getUserDao();
        userDao.update(user);

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
        List<User> users = userDao.queryBuilder()
                .where(UserDao.Properties.MRole.eq("USER"))
                .build()
                .list();

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

    private void bootstrapDB() {
        HashGenerator generator = new MD5Generator();
        mDaoSession.deleteAll(User.class);
        UserDao userDao = mDaoSession.getUserDao();
        UserDetails userDetails = new UserDetails(UUID.randomUUID().toString(),"Bacя", "Василиевич", "Пупкин", "(000)555-55-55", "Чернигов");
        User user = new User("admin@admin.com", generator.generate("admin"), Roles.ADMIN, false);
        user.setUserDetails(userDetails);
        userDao.insert(user);

        userDetails = new UserDetails(UUID.randomUUID().toString(),"Bacя test", "Василиевич test", "Пупкин test", "(000)test-55-55", "Чернигов");
        user = new User("usertest@admin.com", generator.generate("usertest"), Roles.USER, false);
        user.setUserDetails(userDetails);
        user.setThumbnail("JPEG_20170629_162324_508360288.jpg");
        userDao.insert(user);

        for (int i = 0; i<100;i++) {
            userDetails = new UserDetails(UUID.randomUUID().toString(),"Bacя "+i, "Василиевич "+i, "Пупкин "+i, "(000)"+i+"-55-55", "Чернигов");
            user = new User("user"+i+"@admin.com", generator.generate("user"+i), Roles.USER, false);
            user.setUserDetails(userDetails);
            userDao.insert(user);
        }
    }
}
