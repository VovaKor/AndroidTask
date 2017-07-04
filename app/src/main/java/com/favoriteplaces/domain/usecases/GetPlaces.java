package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.repository.FavoritePlaceDataSource;
import com.favoriteplaces.repository.FavoritePlacesRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 03.07.17.
 */

public class GetPlaces extends UseCase<GetPlaces.RequestValues, GetPlaces.ResponseValue> {

    private final FavoritePlacesRepository placesRepository;

    public GetPlaces(@NonNull FavoritePlacesRepository placesRepository) {
        this.placesRepository = checkNotNull(placesRepository, "placesRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {

        placesRepository.getFavoritePlaces(values.getmUserId(), new FavoritePlaceDataSource.LoadFavoritePlacesCallback() {
            @Override
            public void onFavoritePlacesLoaded(List<FavoritePlace> places) {
//
                ResponseValue responseValue = new ResponseValue(places);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mUserId;

        public RequestValues(String userId) {
            mUserId = userId;
        }

        public String getmUserId() {
            return mUserId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<FavoritePlace> mFavoritePlaces;

        public ResponseValue(@NonNull List<FavoritePlace> places) {
            mFavoritePlaces = checkNotNull(places, "places cannot be null!");
        }

        public List<FavoritePlace> getFavoritePlaces() {
            return mFavoritePlaces;
        }
    }

}
