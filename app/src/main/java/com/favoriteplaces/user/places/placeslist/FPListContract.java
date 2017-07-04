package com.favoriteplaces.user.places.placeslist;

import android.graphics.Bitmap;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;
import com.favoriteplaces.domain.models.FavoritePlace;

import java.util.List;

/**
 * Created by vova on 03.07.17.
 */

public interface FPListContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showPlaces(List<FavoritePlace> places);

        void showLoadingPlacesError();

        boolean isActive();

        void showFPDetailsUI(String id);

        void showAddFPUI(String mUserId);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        Bitmap createImageBitmap(String thumbnail, float density);

        void openAddPlaceUI();

        void openPlaceDetails(String placeId);

    }
}
