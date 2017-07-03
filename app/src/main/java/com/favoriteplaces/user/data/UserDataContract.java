package com.favoriteplaces.user.data;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;
import com.favoriteplaces.domain.models.User;

import java.io.File;

/**
 * Created by vova on 25.06.17.
 */

public interface UserDataContract {
    interface View extends BaseView<Presenter> {

        void showImageBitmap(Bitmap bitmap);

        void showUserDetails(User user);

        void showUserUpdatedSuccess();

        void finish();
    }

    interface Presenter extends BasePresenter {

        void updateUser();

        void cancel();

        File createImageFile();

        void addPictureToGallery();

        Bitmap createImageBitmap(ImageView mImageView);

        Bitmap getThumbnail(String thumbnail, float density);

    }
}
