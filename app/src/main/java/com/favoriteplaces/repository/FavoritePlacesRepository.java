package com.favoriteplaces.repository;

import android.support.annotation.NonNull;

import com.favoriteplaces.domain.models.FavoritePlace;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 30.06.17.
 */

public class FavoritePlacesRepository implements FavoritePlaceDataSource{
    private static FavoritePlacesRepository INSTANCE = null;

    private final FavoritePlaceDataSource mFavoritePlaceLocalDataSource;


    // Prevent direct instantiation.
    private FavoritePlacesRepository(@NonNull FavoritePlaceDataSource placeDataSource) {

        mFavoritePlaceLocalDataSource = checkNotNull(placeDataSource);
    }


    public static FavoritePlacesRepository getInstance(FavoritePlaceDataSource favoritePlacesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new FavoritePlacesRepository(favoritePlacesLocalDataSource);
        }
        return INSTANCE;
    }


    @Override
    public void insertFavoritePlace(@NonNull FavoritePlace favoritePlace) {
        checkNotNull(favoritePlace);

        mFavoritePlaceLocalDataSource.insertFavoritePlace(favoritePlace);

    }

    @Override
    public void getFavoritePlaces(@NonNull String userId, @NonNull final LoadFavoritePlacesCallback callback) {
        checkNotNull(callback);

        mFavoritePlaceLocalDataSource.getFavoritePlaces(userId, new LoadFavoritePlacesCallback() {
            @Override
            public void onFavoritePlacesLoaded(List<FavoritePlace> places) {

                callback.onFavoritePlacesLoaded(places);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });

    }

    @Override
    public void getPlace(String placeId, final GetFavoritePlaceCallback callback) {
        checkNotNull(placeId);
        checkNotNull(callback);
        mFavoritePlaceLocalDataSource.getPlace(placeId, new GetFavoritePlaceCallback() {
            @Override
            public void onFavoritePlaceLoaded(FavoritePlace favoritePlace) {
                callback.onFavoritePlaceLoaded(favoritePlace);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }


}
