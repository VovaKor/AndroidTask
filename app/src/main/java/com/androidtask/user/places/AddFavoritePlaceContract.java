package com.androidtask.user.places;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;

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

        void addPictureToGallery();

        Bitmap createImageBitmap(ImageView mImageView);


    }
}
