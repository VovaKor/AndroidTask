package com.favoriteplaces.user.data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.models.UserDetails;
import com.favoriteplaces.domain.usecases.GetUser;
import com.favoriteplaces.domain.usecases.UpdateUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.favoriteplaces.register.RegisterPresenter.FILE_DATE_TEMPLATE;
import static com.favoriteplaces.register.RegisterPresenter.FILE_EXTENSION;
import static com.favoriteplaces.register.RegisterPresenter.FILE_PREFIX;
import static com.favoriteplaces.register.RegisterPresenter.SEPARATOR;

/**
 * Created by vova on 25.06.17.
 */

public class UserDataPresenter implements UserDataContract.Presenter {
    public static final String SLASH = "/";
    public static final int THUMBNAIL_WIDTH = 150;
    public static final int THUMBNAIL_HEIGHT = 150;
    public static final float CORRECTION = 0.5f;
    private final UseCaseHandler mUseCaseHandler;
    private final UpdateUser mUpdateUser;
    private final UserDataActivity.EditTextHolder mHolder;
    private UserDataActivity mView;
    private String mUserId;
    private GetUser mGetUser;
    private User mUser;
    private String mThumbnail;
    private String mCurrentPhotoPath;


    public UserDataPresenter(UseCaseHandler instance, UserDataActivity view, UserDataActivity.EditTextHolder holder, String userId, GetUser getUser, UpdateUser updateUser) {
        mUseCaseHandler = instance;
        mView = view;
        mUserId = userId;
        mHolder = holder;
        mGetUser = getUser;
        mUpdateUser = updateUser;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mUseCaseHandler.execute(mGetUser, new GetUser.RequestValues(mUserId), new UseCase.UseCaseCallback<GetUser.ResponseValue>() {
            @Override
            public void onSuccess(GetUser.ResponseValue response) {
                mUser = response.getUser();
                mView.showUserDetails(mUser);
            }

            @Override
            public void onError() {

            }
        });

    }

    @Override
    public void updateUser() {
        String nickname = mHolder.mNickname.getText().toString();
        String fname = mHolder.mFirstname.getText().toString();
        String patronimic = mHolder.mPatronimic.getText().toString();
        String lname = mHolder.mLastname.getText().toString();
        String phone = mHolder.mPhone.getText().toString();
        String city = mHolder.mCity.getText().toString();
        mUser.setNick_name(nickname);
        mUser.setThumbnail(mThumbnail);

        UserDetails userDetails = new UserDetails();
        userDetails.setFirst_name(fname);
        userDetails.setPatronymic(patronimic);
        userDetails.setLast_name(lname);
        userDetails.setPhone(phone);
        userDetails.setCity(city);

        if (mUser.getId_user_details()!=null) {
            userDetails.setId(mUser.getId_user_details());
        }else{
            userDetails.setId(UUID.randomUUID().toString());
        }
        mUser.setUserDetails(userDetails);

        mUseCaseHandler.execute(mUpdateUser, new UpdateUser.RequestValues(mUser), new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
            @Override
            public void onSuccess(UpdateUser.ResponseValue response) {
                mUser = response.getUser();
                mView.showUserUpdatedSuccess();
                mView.showUserDetails(mUser);
            }

            @Override
            public void onError() {

            }
        });
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
            mThumbnail = image.getName();
            Log.v(mView.TAG, mCurrentPhotoPath);
            Log.v(mView.TAG, mThumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    public void addPictureToGallery() {
       // rotateImage(mCurrentPhotoPath);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mView.sendBroadcast(mediaScanIntent);
    }

    private void rotateImage(String mCurrentPhotoPath) {
        String photopath = mCurrentPhotoPath;
        Bitmap bmp = BitmapFactory.decodeFile(photopath);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(mCurrentPhotoPath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Bitmap createImageBitmap(ImageView mImageView) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        return bitmap;
    }

    @Override
    public Bitmap getThumbnail(String thumbnail, float density) {
        mThumbnail = thumbnail;
        mCurrentPhotoPath = getStorageDirectory().getAbsolutePath()+SLASH+mThumbnail;
        // Get the dimensions of the View

        int targetW = (int) (THUMBNAIL_WIDTH * density + CORRECTION);
        int targetH = (int) (THUMBNAIL_HEIGHT * density + CORRECTION);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        return bitmap;
    }

    private File getStorageDirectory() {
      return   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    }
}
