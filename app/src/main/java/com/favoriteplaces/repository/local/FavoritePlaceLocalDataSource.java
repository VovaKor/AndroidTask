package com.favoriteplaces.repository.local;


import android.content.Context;
import android.support.annotation.NonNull;

import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.repository.FavoritePlaceDataSource;
import com.favoriteplaces.repository.local.persistence.DaoMaster;
import com.favoriteplaces.repository.local.persistence.DaoSession;
import com.favoriteplaces.repository.local.persistence.FavoritePlaceDao;

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

    @Override
    public void getFavoritePlaces(@NonNull String userId, LoadFavoritePlacesCallback loadFavoritePlacesCallback) {
        FavoritePlaceDao favoritePlaceDao = mDaoSession.getFavoritePlaceDao();
        List<FavoritePlace> places = favoritePlaceDao._queryUser_Places(userId);

        if (places.isEmpty()) {
            // This will be called if the table is new or just empty.
            loadFavoritePlacesCallback.onDataNotAvailable();
        } else {
            loadFavoritePlacesCallback.onFavoritePlacesLoaded(places);
        }
    }

    @Override
    public void getPlace(String placeId, GetFavoritePlaceCallback callback) {
        FavoritePlaceDao favoritePlaceDao = mDaoSession.getFavoritePlaceDao();
        FavoritePlace place = favoritePlaceDao.load(placeId);
        if (place != null){
            callback.onFavoritePlaceLoaded(place);
        }else {
            callback.onDataNotAvailable();
        }
    }
}
