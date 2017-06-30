package com.androidtask.user.data;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.models.User;
import com.androidtask.domain.models.UserDetails;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.UpdateUser;
import com.androidtask.repository.UsersRepository;
import com.androidtask.repository.local.UsersLocalDataSource;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;

import static com.androidtask.register.RegisterActivity.ANDROID_API;
import static com.androidtask.register.RegisterActivity.STORAGE_REQUEST_CODE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 27.06.17.
 */

public class UserDataActivity extends FragmentActivity implements UserDataContract.View, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_REQUEST_CODE = 2;
    private UserDataContract.Presenter mPresenter;
    private GoogleApiClient mGoogleApiClient;

    final String TAG = getClass().getSimpleName();
    private EditText mNickname;
    private EditText mFirstName;
    private EditText mPatronimic;
    private EditText mLastName;
    private EditText mPhone;
    private ImageView mImageView;
    private EditText mCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //In this way the VM ignores the file URI exposure!!!
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.user_data);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));
        mNickname = (EditText) findViewById(R.id.nickname_edit);
        mFirstName = (EditText) findViewById(R.id.first_name_edit);
        mPatronimic = (EditText) findViewById(R.id.patronimic_edit);
        mLastName = (EditText) findViewById(R.id.last_name_edit);
        mPhone = (EditText) findViewById(R.id.user_phone_edit);
        mImageView = (ImageView) findViewById(R.id.thumbnail);
        mCity = (EditText) findViewById(R.id.user_city_edit);

        EditTextHolder holder = new EditTextHolder();
        holder.mNickname = mNickname;
        holder.mFirstname = mFirstName;
        holder.mPatronimic = mPatronimic;
        holder.mLastname = mLastName;
        holder.mPhone = mPhone;
        holder.mCity = mCity;

        mPresenter = new UserDataPresenter(
                UseCaseHandler.getInstance(),
                this,
                holder,
                userId,
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new UpdateUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));
        mPresenter.start();
        final Button updateB = (Button) findViewById(R.id.btnUpdateUserData);
        updateB.setEnabled(false);
        TextWatcher textWatcher = new UserDataTextWatcher(updateB);
        mFirstName.addTextChangedListener(textWatcher);
        mPatronimic.addTextChangedListener(textWatcher);
        mLastName.addTextChangedListener(textWatcher);
        mPhone.addTextChangedListener(textWatcher);
        mCity.addTextChangedListener(textWatcher);

        updateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.updateUser();
            }
        });

        Button exitB = (Button) findViewById(R.id.btnExitToUserActions);
        exitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });

        Button thumbnailB = (Button) findViewById(R.id.btnCreateThumbnail);
        thumbnailB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    dispatchTakePictureIntent(STORAGE_REQUEST_CODE);
                }
            }
        });
        Button pickPlaceB = (Button) findViewById(R.id.btnPickPlace);
        pickPlaceB.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (isLocationPermissionGranted()) {
                    //to suppress warnings
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                            .getCurrentPlace(mGoogleApiClient, null);
                    result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                        @Override
                        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                Log.v(TAG, String.format("Place '%s' has likelihood: %g",
                                        placeLikelihood.getPlace().getName(),
                                        placeLikelihood.getLikelihood()));
                            }
                            likelyPlaces.release();
                        }
                    });

                   // dispatchPickPlaceIntent(LOCATION_REQUEST_CODE);
                }
            }
        });
    }

    private void dispatchPickPlaceIntent(int locationRequestCode) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), locationRequestCode);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= ANDROID_API) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Location Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Location Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Location Permission is granted");
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            mPresenter.addPictureToGallery();
            Bitmap bitmap = mPresenter.createImageBitmap(mImageView);
            showImageBitmap(bitmap);

        }
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {

            Place place = PlacePicker.getPlace(data, this);
            String toastMsg = String.format("Place: %s", place.getName());
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_REQUEST_CODE:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Storage Permission: "+permissions[0]+ "was "+grantResults[0]);
                    dispatchTakePictureIntent(STORAGE_REQUEST_CODE);

                }

            }break;
            case LOCATION_REQUEST_CODE:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Location Permission: "+permissions[0]+ "was "+grantResults[0]);

                    dispatchPickPlaceIntent(LOCATION_REQUEST_CODE);
                }
            }break;
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
    @Override
    public void showImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }
    @Override
    public void showUserDetails(User user) {
        UserDetails userDetails = user.getUserDetails();
        mNickname.setText(user.getNick_name());
        if (user.getThumbnail()!= null){
            Bitmap bitmap = mPresenter.getThumbnail(user.getThumbnail(), getResources().getDisplayMetrics().density);
            showImageBitmap(bitmap);
          }
        if (userDetails!=null) {
            mFirstName.setText(userDetails.getFirst_name());
            mPatronimic.setText(userDetails.getPatronymic());
            mLastName.setText(userDetails.getLast_name());
            mPhone.setText(userDetails.getPhone());
            mCity.setText(userDetails.getCity());
        }
    }

    @Override
    public void showUserUpdatedSuccess() {
        Snackbar.make(mNickname,getString(R.string.user_updated),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(UserDataContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, connectionResult.getErrorMessage());
    }

    private class UserDataTextWatcher implements TextWatcher{
        private final Button mButton;

        public UserDataTextWatcher(Button button) {
            mButton = button;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean enable = s.length() != 0;
            mButton.setEnabled(enable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            mButton.setEnabled(true);
        }
    }

    class EditTextHolder {
        public EditText mNickname;
        public EditText mFirstname;
        public EditText mPatronimic;
        public EditText mLastname;
        public EditText mPhone;

        public EditText mCity;
    }
}
