package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.repository.FavoritePlaceDataSource;
import com.favoriteplaces.repository.FavoritePlacesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 04.07.17.
 */

public class GetPlace extends UseCase<GetPlace.RequestValues, GetPlace.ResponseValue> {

    private final FavoritePlacesRepository mRepository;

    public GetPlace(@NonNull FavoritePlacesRepository instance) {
        mRepository = checkNotNull(instance, "Repository cannot be null!");
    }
    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.getPlace(requestValues.getmPlaceId(), new FavoritePlaceDataSource.GetFavoritePlaceCallback(){
            @Override
            public void onFavoritePlaceLoaded(FavoritePlace place) {
                if (place != null) {
                    ResponseValue responseValue = new ResponseValue(place);
                    getUseCaseCallback().onSuccess(responseValue);
                } else {
                    getUseCaseCallback().onError();
                }
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mPlaceId;

        public RequestValues(@NonNull String placeId) {
            mPlaceId = checkNotNull(placeId, "placeId cannot be null!");

        }
        public String getmPlaceId() {
            return mPlaceId;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final FavoritePlace mPlace;

        public ResponseValue(@NonNull FavoritePlace place) {
            mPlace = checkNotNull(place, "places cannot be null!");
        }

        public FavoritePlace getPlace() {
            return mPlace;
        }
    }
}
