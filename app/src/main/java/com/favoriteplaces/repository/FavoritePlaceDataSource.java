package com.favoriteplaces.repository;

import android.support.annotation.NonNull;

import com.favoriteplaces.domain.models.FavoritePlace;

import java.util.List;

/**
 * Created by vova on 30.06.17.
 */

public interface FavoritePlaceDataSource {
    void insertFavoritePlace(@NonNull FavoritePlace FavoritePlace);

    interface LoadFavoritePlacesCallback {

        void onFavoritePlacesLoaded(List<FavoritePlace> favoritePlaces);

        void onDataNotAvailable();
    }

    interface GetFavoritePlaceCallback {

        void onFavoritePlaceLoaded(FavoritePlace favoritePlace);

        void onDataNotAvailable();
    }

}
