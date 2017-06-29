package com.androidtask.register;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;

import java.io.File;
import java.io.IOException;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface RegisterContract {

    interface View extends BaseView<Presenter> {

        void showPasswordMismatchError();

        void showTooShortPasswordError();

        void showEmptyPasswordError();

        void showTooShortEmailError();

        void showTooLongEmailError();

        void showInvalidEmailError();

        void showEmailExistError();

        void showEmptyEmailError();

        void showActivity(Class clazz);

        void showSaveUserError();

        void showImageBitmap(Bitmap bitmap);
    }

    interface Presenter extends BasePresenter {

        void getUser();

        void saveUser();

        void startActivity(Class activityClass);

        File createImageFile();

        Bitmap createImageBitmap(ImageView mImageView);

        void addPictureToGallery();
    }
}
