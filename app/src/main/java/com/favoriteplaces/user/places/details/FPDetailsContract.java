package com.favoriteplaces.user.places.details;

import android.graphics.Bitmap;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.weathermap.CoordResponse;

/**
 * Created by vova on 04.07.17.
 */

public interface FPDetailsContract {
    interface View extends BaseView<Presenter> {

        void showPlaceDetails(FavoritePlace place);

        void showPicture(Bitmap bitmap);

        boolean isOnline();

        void showForecast(CoordResponse value);

        void showNetworkError();
    }

    interface Presenter extends BasePresenter {

        void getWeatherForecastByCoords(Double latitude, Double longitude);

        void createBitmap(String photo, int height, int width);
    }
}
