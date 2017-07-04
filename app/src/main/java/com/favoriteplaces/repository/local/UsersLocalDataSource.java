package com.favoriteplaces.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.domain.models.Roles;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.models.UserDetails;
import com.favoriteplaces.repository.UsersDataSource;
import com.favoriteplaces.repository.local.persistence.DaoMaster;
import com.favoriteplaces.repository.local.persistence.DaoSession;
import com.favoriteplaces.repository.local.persistence.FavoritePlaceDao;
import com.favoriteplaces.repository.local.persistence.UserDao;
import com.favoriteplaces.utils.HashGenerator;
import com.favoriteplaces.utils.MD5Generator;

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
        if (user!=null){
            callback.onUserLoaded(user);
        }else {
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
        mDaoSession.deleteAll(FavoritePlace.class);
        mDaoSession.deleteAll(UserDetails.class);
        mDaoSession.clear();
        UserDao userDao = mDaoSession.getUserDao();
        FavoritePlaceDao favoritePlaceDao = mDaoSession.getFavoritePlaceDao();

        UserDetails userDetails = new UserDetails(UUID.randomUUID().toString(),"Bacя", "Василиевич", "Пупкин", "(000)555-55-55", "Чернигов");
        User user = new User("admin@admin.com", generator.generate("admin"), Roles.ADMIN, false);
        user.setUserDetails(userDetails);
        userDao.insert(user);

        userDetails = new UserDetails(UUID.randomUUID().toString(),"Bacя test", "Василиевич test", "Пупкин test", "(000)test-55-55", "Чернигов");
        user = new User("usertest@admin.com", generator.generate("usertest"), Roles.USER, false);
        user.setUserDetails(userDetails);
        user.setThumbnail("JPEG_20170629_162324_508360288.jpg");
        userDao.insert(user);

        FavoritePlace favoritePlace ;
        for (int i = 0; i<100;i++) {
            favoritePlace = new FavoritePlace();
            favoritePlace.setPhoto("JPEG_20170629_162324_508360288.jpg");
            favoritePlace.setId(UUID.randomUUID().toString());
            favoritePlace.setCity("Киев");
            favoritePlace.setDescription("Description "+i);
            favoritePlace.setLatitude(50.433334);
            favoritePlace.setLongitude(30.516666);
            favoritePlace.setUserId(user.getId());
            favoritePlace.setTitle("Test title "+i);
            favoritePlaceDao.insert(favoritePlace);
        }

        for (int i = 0; i<100;i++) {
            userDetails = new UserDetails(UUID.randomUUID().toString(),"Bacя "+i, "Василиевич "+i, "Пупкин "+i, "(000)"+i+"-55-55", "Чернигов");
            user = new User("user"+i+"@admin.com", generator.generate("user"+i), Roles.USER, false);
            user.setUserDetails(userDetails);
            userDao.insert(user);
        }
    }
}
