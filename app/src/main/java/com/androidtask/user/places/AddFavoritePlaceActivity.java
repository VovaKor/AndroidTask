package com.androidtask.user.places;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.usecases.InsertPlace;
import com.androidtask.repository.FavoritePlacesRepository;
import com.androidtask.repository.local.FavoritePlaceLocalDataSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
    private Location mLocation;
    private LocationListener mLocationListener;


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
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        mLocationListener = new CityLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            isLocationPermissionGranted();
            return;
        }
        mLocation = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 400, 1, mLocationListener);

        mPresenter = new AddFavoritePlacePresenter(
                UseCaseHandler.getInstance(),
                this,
                holder,
                userId,
                new InsertPlace(FavoritePlacesRepository.getInstance(FavoritePlaceLocalDataSource.getInstance(getApplicationContext()))));


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

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void showPlaceCoordinates() {

        // Initialize the location fields
        if (mLocation != null) {
            mLatitude.setText(Double.toString(mLocation.getLatitude()));
            mLongitude.setText(Double.toString(mLocation.getLongitude()));
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.out_of_coord_error), Toast.LENGTH_LONG).show();

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
    protected void onResume() {
        super.onResume();
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

        locationManager.requestLocationUpdates(provider, 400, 1, mLocationListener);
        showPlaceCoordinates();
        showCity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            mPresenter.addPictureToGallery();
            Bitmap bitmap = mPresenter.createImageBitmap(mImageView);
            showImageBitmap(bitmap);

        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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

                    showPlaceCoordinates();
                    showCity();
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

    @Override
    public void showCity() {
        if (mLocation!=null){
            if (isOnline()){
            new AsyncTask<Void, Integer, List<Address>>() {
                @Override
                protected List<Address> doInBackground(Void... arg0) {
                    Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> results = null;
                    try {
                        results = coder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        // nothing
                    }
                    return results;
                }

                @Override
                protected void onPostExecute(List<Address> results) {


                    if (results != null) {
                        String city = results.get(0).getLocality();
                        if (!TextUtils.isEmpty(city)){
                            mCity.setText(city);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.out_of_city_error), Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.out_of_address_error), Toast.LENGTH_LONG).show();

                    }
                }
            }.execute();
            }else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.out_of_network_error), Toast.LENGTH_LONG).show();
            }
        }else{
        Toast.makeText(getApplicationContext(),
                getString(R.string.out_of_coord_error), Toast.LENGTH_LONG).show();

        }

    }

    class EditTextHolder {
        public EditText mTitle;
        public EditText mDescription;
        public EditText mLatitude;
        public EditText mLongitude;
        public EditText mCity;
    }
    private class CityLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
