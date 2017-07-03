package com.favoriteplaces.user.places;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.domain.usecases.InsertPlace;
import com.favoriteplaces.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.favoriteplaces.register.RegisterPresenter.FILE_DATE_TEMPLATE;
import static com.favoriteplaces.register.RegisterPresenter.FILE_EXTENSION;
import static com.favoriteplaces.register.RegisterPresenter.FILE_PREFIX;
import static com.favoriteplaces.register.RegisterPresenter.SEPARATOR;
import static com.favoriteplaces.user.data.UserDataPresenter.CORRECTION;
import static com.favoriteplaces.user.data.UserDataPresenter.THUMBNAIL_HEIGHT;
import static com.favoriteplaces.user.data.UserDataPresenter.THUMBNAIL_WIDTH;

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
        // Create an image file name
        String timeStamp = new SimpleDateFormat(FILE_DATE_TEMPLATE).format(new Date());
        String imageFileName = FILE_PREFIX + timeStamp + SEPARATOR;

        File image = null;
        try {

            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    FILE_EXTENSION,         /* suffix */
                    getStorageDirectory()      /* directory */
            );

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

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mView.sendBroadcast(mediaScanIntent);
    }

    @Override
    public Bitmap createImageBitmap(String path, float density) {

        // Get the dimensions of the View
        int targetW = (int) (THUMBNAIL_WIDTH * density + CORRECTION);
        int targetH = (int) (THUMBNAIL_HEIGHT * density + CORRECTION);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        return bitmap;
    }

    @Override
    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    private File getStorageDirectory() {
        return   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    }
}
