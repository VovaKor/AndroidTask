package com.androidtask.repository.local;


import android.content.Context;
import android.support.annotation.NonNull;

import com.androidtask.domain.models.FavoritePlace;
import com.androidtask.repository.FavoritePlaceDataSource;
import com.androidtask.repository.local.persistence.DaoMaster;
import com.androidtask.repository.local.persistence.DaoSession;
import com.androidtask.repository.local.persistence.FavoritePlaceDao;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 30.06.17.
 */

public class FavoritePlaceLocalDataSource implements FavoritePlaceDataSource {

    private static FavoritePlaceLocalDataSource INSTANCE;
    private static final String DATABASE_NAME = "user.db";
    private DaoSession mDaoSession;
    // Prevent direct instantiation.
    private FavoritePlaceLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();

    }

    public static FavoritePlaceLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FavoritePlaceLocalDataSource(context);
        }
        return INSTANCE;
    }


    @Override
    public void insertFavoritePlace(@NonNull FavoritePlace favoritePlace) {
        checkNotNull(favoritePlace);
        FavoritePlaceDao favoritePlaceDao = mDaoSession.getFavoritePlaceDao();
        favoritePlaceDao.insert(favoritePlace);
    }
}
