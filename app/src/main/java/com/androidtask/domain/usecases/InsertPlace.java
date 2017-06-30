package com.androidtask.domain.usecases;

import android.support.annotation.NonNull;

import com.androidtask.UseCase;
import com.androidtask.domain.models.FavoritePlace;
import com.androidtask.repository.FavoritePlacesRepository;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 30.06.17.
 */

public class InsertPlace extends UseCase<InsertPlace.RequestValues, InsertPlace.ResponseValue>{
    private final FavoritePlacesRepository mFavoritePlacesRepository;

    public InsertPlace(@NonNull FavoritePlacesRepository FavoritePlacesRepository) {
        mFavoritePlacesRepository = checkNotNull(FavoritePlacesRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        FavoritePlace place = values.getFavoritePlace();
        mFavoritePlacesRepository.insertFavoritePlace(place);

        getUseCaseCallback().onSuccess(new ResponseValue(place));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final FavoritePlace mFavoritePlace;

        public RequestValues(@NonNull FavoritePlace place) {
            mFavoritePlace = checkNotNull(place, "FavoritePlace cannot be null!");
        }

        public FavoritePlace getFavoritePlace() {
            return mFavoritePlace;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final FavoritePlace mFavoritePlace;

        public ResponseValue(@NonNull FavoritePlace FavoritePlace) {
            mFavoritePlace = checkNotNull(FavoritePlace, "FavoritePlace cannot be null!");
        }

        public FavoritePlace getFavoritePlace() {
            return mFavoritePlace;
        }
    }
}
