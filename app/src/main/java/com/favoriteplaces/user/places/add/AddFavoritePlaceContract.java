package com.favoriteplaces.user.places.add;

import android.graphics.Bitmap;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;

import java.io.File;

/**
 * Created by vova on 30.06.17.
 */

interface AddFavoritePlaceContract {

    interface View extends BaseView<Presenter> {
        void showPlaceCoordinates();

        void showImageBitmap(Bitmap bitmap);

        void showPlaceInsertedSuccess();

        void setPresenter(Presenter presenter);

        void showSaveError();

        void showPhotoEmptyError();

        void showCity();
    }
    interface Presenter extends BasePresenter {

        void insertPlace();

        void cancel();

        File createImageFile();

        void addPictureToGallery(String photoPath);

        void createImagePreview(String path, float density);

    }
}