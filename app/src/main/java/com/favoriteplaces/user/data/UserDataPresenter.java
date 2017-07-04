package com.favoriteplaces.user.data;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.models.UserDetails;
import com.favoriteplaces.domain.usecases.GetUser;
import com.favoriteplaces.domain.usecases.UpdateUser;
import com.favoriteplaces.utils.PictureManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by vova on 25.06.17.
 */

public class UserDataPresenter implements UserDataContract.Presenter {

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

        File image = null;
        try {
            image = PictureManager.getInstance().createTempFile();
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
        PictureManager.getInstance().addPictureToGallery(mCurrentPhotoPath,mView);
    }

    @Override
    public void addImageBitmapToView(int width, int height) {
        Bitmap bitmap = PictureManager.getInstance().getBitmap(mCurrentPhotoPath,width,height);
        mView.showImageBitmap(bitmap);
    }

    @Override
    public void addThumbnailToView(String path, float density) {
        Bitmap bitmap = PictureManager.getInstance().createBigImageBitmap(path,density);
        mView.showImageBitmap(bitmap);
    }

}
