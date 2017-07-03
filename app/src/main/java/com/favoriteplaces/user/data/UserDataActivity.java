package com.favoriteplaces.user.data;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.models.UserDetails;
import com.favoriteplaces.domain.usecases.GetUser;
import com.favoriteplaces.domain.usecases.UpdateUser;
import com.favoriteplaces.repository.UsersRepository;
import com.favoriteplaces.repository.local.UsersLocalDataSource;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.favoriteplaces.register.RegisterActivity.ANDROID_API;
import static com.favoriteplaces.register.RegisterActivity.STORAGE_REQUEST_CODE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 27.06.17.
 */

public class UserDataActivity extends FragmentActivity implements UserDataContract.View{
    private static final int LOCATION_REQUEST_CODE = 2;
    private UserDataContract.Presenter mPresenter;
    private LocationManager locationManager;
    private String provider;
    final String TAG = getClass().getSimpleName();
    private EditText mNickname;
    private EditText mFirstName;
    private EditText mPatronimic;
    private EditText mLastName;
    private EditText mPhone;
    private ImageView mImageView;
    private EditText mCity;
    private LocationListener mLocationListener;
    private Location mLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //In this way the VM ignores the file URI exposure!!!
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.user_data);

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        mLocationListener = new UserDataLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = locationManager.getLastKnownLocation(provider);
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
                    dispatchPickPlaceTask(mLocation);

                }
            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(mLocationListener);
    }

    private void dispatchPickPlaceTask(final Location location) {
             if (location!=null){
                 if (isOnline()){
                     new AsyncTask<Void, Integer, List<Address>>() {
                         @Override
                         protected List<Address> doInBackground(Void... arg0) {
                             Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
                             List<Address> results = null;
                             try {
                                 results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
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

//
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= ANDROID_API) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Location Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Location Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Location Permission is granted");
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
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Storage Permission: " + permissions[0] + "was " + grantResults[0]);
                    dispatchTakePictureIntent(STORAGE_REQUEST_CODE);

                }

            }
            break;
            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Location Permission: " + permissions[0] + "was " + grantResults[0]);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                }
            }break;
        }


    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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

    private class UserDataLocationListener implements LocationListener{
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
