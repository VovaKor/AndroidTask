package com.favoriteplaces.register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.Roles;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.usecases.GetUser;
import com.favoriteplaces.domain.usecases.InsertUser;
import com.favoriteplaces.login.LoginActivity;
import com.favoriteplaces.utils.HashGenerator;
import com.favoriteplaces.utils.MD5Generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link RegisterActivity}), retrieves the data and
 * updates the UI as required.
 */
public class RegisterPresenter implements RegisterContract.Presenter {

    private final RegisterActivity mRegisterView;
    private final int EMAIL_MIN_LENGTH = 6;
    private final int EMAIL_MAX_LENGTH = 129;
    private final int PASSWORD_MIN_LENGTH = 4;
    private final String EMAIL_REGEXP = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    public static final String SEPARATOR = "_";
    public static final String FILE_DATE_TEMPLATE = "yyyyMMdd_HHmmss";
    public static final String FILE_PREFIX = "JPEG_";
    public static final String FILE_EXTENSION = ".jpg";
    private String mCurrentPhotoPath;

    private final GetUser mGetUser;
    private InsertUser mInsertUser;
    private final UseCaseHandler mUseCaseHandler;
    private String mPassword;
    private String mEmail;
    private RegisterActivity.ViewHolder mViewHolder;
    private boolean mCancel;
    private HashGenerator mGenerator;
    private String mThumbnail;

    /**
     * Creates a presenter for the login view.
     *
     */
    public RegisterPresenter(@NonNull UseCaseHandler useCaseHandler, @NonNull RegisterActivity.ViewHolder holder,
                             @NonNull RegisterActivity registerView, @NonNull InsertUser insertUser, @NonNull GetUser getUser) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mInsertUser = checkNotNull(insertUser, "insertUser cannot be null!");
        mRegisterView = checkNotNull(registerView, "View cannot be null!");
        mGetUser = checkNotNull(getUser, "loginUser cannot be null!");
        mViewHolder = checkNotNull(holder, "holder cannot be null!");
        mGenerator = new MD5Generator();
        mRegisterView.setPresenter(this);

    }

    @Override
    public void start() {

        mCancel = false;

        String passwordCopy = mViewHolder.passwordCopyView.getText().toString();
        mEmail = mViewHolder.emailView.getText().toString();
        mPassword = mViewHolder.passwordView.getText().toString();

        if (TextUtils.isEmpty(mEmail)) {
            mRegisterView.showEmptyEmailError();
            mCancel = true;
        }else if (mEmail.length()< EMAIL_MIN_LENGTH){
            mRegisterView.showTooShortEmailError();
            mCancel = true;
        }else if (mEmail.length()> EMAIL_MAX_LENGTH){
            mRegisterView.showTooLongEmailError();
            mCancel = true;
        }else if (!mEmail.matches(EMAIL_REGEXP)){
            mRegisterView.showInvalidEmailError();
            mCancel = true;
        } else if (TextUtils.isEmpty(mPassword)) {
            mRegisterView.showEmptyPasswordError();
            mCancel = true;
        }else if (mPassword.length()< PASSWORD_MIN_LENGTH){
            mRegisterView.showTooShortPasswordError();
            mCancel = true;
        }else if (!TextUtils.equals(mPassword, passwordCopy)) {
            mRegisterView.showPasswordMismatchError();
            mCancel = true;
        }
        if (!mCancel) {
            getUser();

        }

    }

    @Override
    public void getUser() {
        mUseCaseHandler.execute(mGetUser, new GetUser.RequestValues(mEmail),
                new UseCase.UseCaseCallback<GetUser.ResponseValue>() {
                    @Override
                    public void onSuccess(GetUser.ResponseValue response) {

                        mRegisterView.showEmailExistError();

                    }

                    @Override
                    public void onError() {
                        saveUser();
                    }
                });
    }

    @Override
    public void saveUser() {
        User user = new User(mEmail,mGenerator.generate(mPassword),Roles.USER,false);
        user.setThumbnail(mThumbnail);
        mUseCaseHandler.execute(mInsertUser, new InsertUser.RequestValues(user),
                new UseCase.UseCaseCallback<InsertUser.ResponseValue>() {
                    @Override
                    public void onSuccess(InsertUser.ResponseValue response) {
                        startActivity(LoginActivity.class);
                    }

                    @Override
                    public void onError() {
                        mRegisterView.showSaveUserError();
                    }
                });
    }

    @Override
    public void startActivity(Class activityClass) {
        mRegisterView.showActivity(activityClass);
    }
    @Override
    public File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(FILE_DATE_TEMPLATE).format(new Date());
        String imageFileName = FILE_PREFIX + timeStamp + SEPARATOR;

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    FILE_EXTENSION,         /* suffix */
                    storageDir      /* directory */
            );

            mCurrentPhotoPath = image.getAbsolutePath();
            mThumbnail = image.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
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
    public void addPictureToGallery() {
            rotateImage(mCurrentPhotoPath);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            mRegisterView.sendBroadcast(mediaScanIntent);

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



}
