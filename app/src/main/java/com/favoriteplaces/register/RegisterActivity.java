package com.favoriteplaces.register;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.usecases.GetUser;
import com.favoriteplaces.domain.usecases.InsertUser;
import com.favoriteplaces.repository.UsersRepository;
import com.favoriteplaces.repository.local.UsersLocalDataSource;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 15.05.17.
 */

public class RegisterActivity extends Activity implements RegisterContract.View {
    public static final int ANDROID_API = 23;
    private ViewHolder mHolder;
      public static final int STORAGE_REQUEST_CODE = 1;

    private RegisterContract.Presenter mPresenter;
      private ImageView mImageView;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //In this way the VM ignores the file URI exposure!!!
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.register);
        mHolder = new ViewHolder();
        mHolder.emailView = (EditText) findViewById(R.id.reg_email);
        mHolder.passwordView = (EditText) findViewById(R.id.reg_password);
        mHolder.passwordCopyView = (EditText) findViewById(R.id.repeat_password);
        mImageView = (ImageView) findViewById(R.id.picture);

        Button submit = (Button) findViewById(R.id.btnSubmitRegister);
        Button captureBtn = (Button)findViewById(R.id.capture_btn);


        // Create the presenter
        mPresenter = new RegisterPresenter(UseCaseHandler.getInstance(), mHolder, this,
                new InsertUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()){
                    dispatchTakePictureIntent(STORAGE_REQUEST_CODE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Reset errors.
                mHolder.emailView.setError(null);
                mHolder.passwordView.setError(null);
                mHolder.passwordCopyView.setError(null);
               // focusView = null;
                mPresenter.start();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            mPresenter.addPictureToGallery();
            Bitmap bitmap = mPresenter.createImageBitmap(mImageView);
            showImageBitmap(bitmap);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            dispatchTakePictureIntent(STORAGE_REQUEST_CODE);
        }
    }
    @Override
    public void showEmptyEmailError() {
        mHolder.emailView.setError(getString(R.string.error_field_empty));

    }

    @Override
    public void showPasswordMismatchError() {
        mHolder.passwordCopyView.setError(getString(R.string.error_password_mismatch));
    }

    @Override
    public void showTooShortPasswordError() {
        mHolder.passwordView.setError(getString(R.string.error_short_password));
    }

    @Override
    public void showEmptyPasswordError() {
        mHolder.passwordView.setError(getString(R.string.error_field_empty));
    }

    @Override
    public void showTooShortEmailError() {
        mHolder.emailView.setError(getString(R.string.error_short_email));
    }

    @Override
    public void showTooLongEmailError() {
        mHolder.emailView.setError(getString(R.string.error_long_email));
    }

    @Override
    public void showInvalidEmailError() {
        mHolder.emailView.setError(getString(R.string.error_email_invalid));
    }

    @Override
    public void showEmailExistError() {
        mHolder.emailView.setError(getString(R.string.error_email_exist));
    }

    @Override
    public void showActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
        finish();
    }

    @Override
    public void showSaveUserError() {
        Snackbar.make(mHolder.emailView, getString(R.string.save_user_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = mPresenter.createImageFile();

        if (f != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, actionCode);
            }
        }


    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= ANDROID_API) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    class ViewHolder{

         EditText emailView;
         EditText passwordView;
         EditText passwordCopyView;
    }

}
