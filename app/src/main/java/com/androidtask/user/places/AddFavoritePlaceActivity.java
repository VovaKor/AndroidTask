package com.androidtask.user.places;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.usecases.InsertPlace;
import com.androidtask.repository.FavoritePlacesRepository;
import com.androidtask.repository.local.FavoritePlaceLocalDataSource;

import java.io.File;

import static com.androidtask.register.RegisterActivity.ANDROID_API;
import static com.androidtask.register.RegisterActivity.STORAGE_REQUEST_CODE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 29.06.17.
 */

public class AddFavoritePlaceActivity extends Activity implements AddFavoritePlaceContract.View {
    private static final int LOCATION_REQUEST_CODE = 2;
    private AddFavoritePlaceContract.Presenter mPresenter;
    private LocationManager locationManager;
    final String TAG = getClass().getSimpleName();
    private EditText mTitle;
    private EditText mDescription;
    private EditText mLatitude;
    private EditText mLongitude;
    private ImageView mImageView;
    private EditText mCity;
    private String provider;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //In this way the VM ignores the file URI exposure!!!
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.add_favorite_place);

        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));
        mTitle = (EditText) findViewById(R.id.title_edit);
        mDescription = (EditText) findViewById(R.id.description_edit);
        mLatitude = (EditText) findViewById(R.id.latitude_edit);
        mLongitude = (EditText) findViewById(R.id.longitude_edit);
        mImageView = (ImageView) findViewById(R.id.photo_thumbnail);
        mCity = (EditText) findViewById(R.id.city_name_edit);

        EditTextHolder holder = new EditTextHolder();
        holder.mTitle = mTitle;
        holder.mDescription = mDescription;
        holder.mLatitude = mLatitude;
        holder.mLongitude = mLongitude;
        holder.mCity = mCity;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        provider = locationManager.getBestProvider(criteria, false);


        mPresenter = new AddFavoritePlacePresenter(
                UseCaseHandler.getInstance(),
                this,
                holder,
                userId,
                new InsertPlace(FavoritePlacesRepository.getInstance(FavoritePlaceLocalDataSource.getInstance(getApplicationContext()))));

        mPresenter.start();
        final Button savePlaceB = (Button) findViewById(R.id.btnSavePlace);
        savePlaceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.insertPlace();
            }
        });

        Button exitB = (Button) findViewById(R.id.btnExitToActions);
        exitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });

        Button takePictureB = (Button) findViewById(R.id.btnTakePicture);
        takePictureB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    dispatchTakePictureIntent(STORAGE_REQUEST_CODE);
                }
            }
        });
        Button pickCoordinatesB = (Button) findViewById(R.id.btnPickCoordinates);
        pickCoordinatesB.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (isLocationPermissionGranted()) {
                    getPlaceCoordinates();
                }
            }
        });
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            mLongitude.setError(null);
            mLatitude.setError(null);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                mLatitude.setText(Double.toString(location.getLatitude()));
                mLongitude.setText(Double.toString(location.getLongitude()));
            } else {
                mLatitude.setError("Location not available");
                mLongitude.setError("Location not available");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private void getPlaceCoordinates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);

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

                    getPlaceCoordinates();
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
    public void showPlaceInsertedSuccess() {
        Snackbar.make(mTitle,getString(R.string.place_saved_success),Snackbar.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void setPresenter(AddFavoritePlaceContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showSaveError() {
        Snackbar.make(mTitle,getString(R.string.place_save_error),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showPhotoEmptyError() {
        Snackbar.make(mTitle,getString(R.string.take_picture_error),Snackbar.LENGTH_LONG).show();
    }

    class EditTextHolder {
        public EditText mTitle;
        public EditText mDescription;
        public EditText mLatitude;
        public EditText mLongitude;
        public EditText mCity;
    }
}
