package com.favoriteplaces.user.places.add;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.domain.usecases.InsertPlace;
import com.favoriteplaces.utils.PictureManager;
import com.favoriteplaces.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by vova on 30.06.17.
 */

public class AddFavoritePlacePresenter implements AddFavoritePlaceContract.Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final InsertPlace mInsertPlace;
    private final AddFavoritePlaceActivity.EditTextHolder mHolder;
    private AddFavoritePlaceActivity mView;
    private String mUserId;
    private String mPhotoName;
    private String mCurrentPhotoPath;


    public AddFavoritePlacePresenter(UseCaseHandler instance,
                                     AddFavoritePlaceActivity view,
                                     AddFavoritePlaceActivity.EditTextHolder holder,
                                     String userId,
                                     InsertPlace insertPlace) {
        mUseCaseHandler = instance;
        mView = view;
        mUserId = userId;
        mHolder = holder;
        mInsertPlace = insertPlace;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showPlaceCoordinates();
        mView.showCity();
    }

    @Override
    public void insertPlace() {
        String title = mHolder.mTitle.getText().toString();
        String description = mHolder.mDescription.getText().toString();
        String latitude = mHolder.mLatitude.getText().toString();
        String longitude = mHolder.mLongitude.getText().toString();
        String city = mHolder.mCity.getText().toString();

        String error = mView.getString(R.string.error_field_empty);
        if (TextUtils.isEmpty(title)){
            mHolder.mTitle.setError(error);
        }else if (TextUtils.isEmpty(latitude)){
            mHolder.mLatitude.setError(error);
        }else if (TextUtils.isEmpty(longitude)){
            mHolder.mLongitude.setError(error);
        }else if (TextUtils.isEmpty(city)){
            mHolder.mCity.setError(error);
        }else if (TextUtils.isEmpty(mPhotoName)){
            mView.showPhotoEmptyError();
        }else {

            FavoritePlace favoritePlace = new FavoritePlace();
            favoritePlace.setId(UUID.randomUUID().toString());
            favoritePlace.setTitle(title);
            favoritePlace.setDescription(description);
            favoritePlace.setCity(city);
            favoritePlace.setLatitude(Double.parseDouble(latitude));
            favoritePlace.setLongitude(Double.parseDouble(longitude));
            favoritePlace.setPhoto(mPhotoName);
            favoritePlace.setUserId(mUserId);

            mUseCaseHandler.execute(mInsertPlace, new InsertPlace.RequestValues(favoritePlace), new UseCase.UseCaseCallback<InsertPlace.ResponseValue>() {
                @Override
                public void onSuccess(InsertPlace.ResponseValue response) {

                    mView.showPlaceInsertedSuccess();

                }

                @Override
                public void onError() {
                    mView.showSaveError();
                }
            });
        }

    }

    @Override
    public void cancel() {
        mView.finish();
    }

    @Override
    public File createImageFile() {

        File image = null;
        try {

            image = PictureManager.getInstance().createTempFile();
            mCurrentPhotoPath = image.getAbsolutePath();
            mPhotoName = image.getName();
            SessionManager manager = SessionManager.getInstance(mView.getApplicationContext());
            manager.saveCurrentPhotoPath(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    public void addPictureToGallery(String photoPath) {
        PictureManager.getInstance().addPictureToGallery(photoPath,mView);
    }

    @Override
    public void createImagePreview(String path, float density) {
        Bitmap bitmap = PictureManager.getInstance().createBigImageBitmap(path, density);
        mView.showImageBitmap(bitmap);
    }

}
