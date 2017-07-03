package com.favoriteplaces.repository;

import android.support.annotation.NonNull;

import com.favoriteplaces.domain.models.FavoritePlace;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 30.06.17.
 */

public class FavoritePlacesRepository implements FavoritePlaceDataSource{
    private static FavoritePlacesRepository INSTANCE = null;

    private final FavoritePlaceDataSource mFavoritePlaceLocalDataSource;


    // Prevent direct instantiation.
    private FavoritePlacesRepository(@NonNull FavoritePlaceDataSource FavoritePlacesLocalDataSource) {

        mFavoritePlaceLocalDataSource = checkNotNull(FavoritePlacesLocalDataSource);
    }


    public static FavoritePlacesRepository getInstance(FavoritePlaceDataSource FavoritePlacesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new FavoritePlacesRepository(FavoritePlacesLocalDataSource);
        }
        return INSTANCE;
    }


    @Override
    public void insertFavoritePlace(@NonNull FavoritePlace FavoritePlace) {
        checkNotNull(FavoritePlace);

        mFavoritePlaceLocalDataSource.insertFavoritePlace(FavoritePlace);

    }

}
