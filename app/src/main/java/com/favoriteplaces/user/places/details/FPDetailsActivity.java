package com.favoriteplaces.user.places.details;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.domain.usecases.GetPlace;
import com.favoriteplaces.repository.FavoritePlacesRepository;
import com.favoriteplaces.repository.local.FavoritePlaceLocalDataSource;
import com.favoriteplaces.weathermap.CoordResponse;

import io.reactivex.disposables.CompositeDisposable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 03.07.17.
 */

public class FPDetailsActivity extends Activity implements FPDetailsContract.View{
    private FPDetailsContract.Presenter mPresenter;
    private TextView mTitle;
    private TextView mDescription;
    private TextView mCity;
    private TextView mLatitude;
    private TextView mLongitude;
    private ImageView mPhoto;
    private TextView mTemp;
    private TextView mWind;
    private TextView mClouds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_details);

        String placeId = getIntent().getStringExtra(getString(R.string.EXTRA_PLACE_ID));
        mPhoto = (ImageView) findViewById(R.id.placePhoto);
        mTitle = (TextView) findViewById(R.id.fp_details_title);
        mDescription = (TextView) findViewById(R.id.fp_details_description);
        mCity = (TextView) findViewById(R.id.fp_details_city);
        mLatitude = (TextView) findViewById(R.id.fp_details_latitude);
        mLongitude = (TextView) findViewById(R.id.fp_details_longitude);
        mTemp = (TextView) findViewById(R.id.fp_details_temp);
        mWind = (TextView) findViewById(R.id.fp_details_wind);
        mClouds = (TextView) findViewById(R.id.fp_details_clouds);

        mPresenter = new FPDetailsPresenter(
                UseCaseHandler.getInstance(),
                this,
                placeId,
                getString(R.string.weather_appid),
                new GetPlace(FavoritePlacesRepository.getInstance(FavoritePlaceLocalDataSource.getInstance(getApplicationContext()))),
                new CompositeDisposable());

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void setPresenter(FPDetailsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showPlaceDetails(FavoritePlace place) {
        mPresenter.createBitmap(place.getPhoto(),mPhoto.getHeight(),mPhoto.getWidth());
        mTitle.setText(place.getTitle());
        if (!TextUtils.isEmpty(place.getDescription())){
            mDescription.setText(place.getDescription());
        }
        mCity.setText(place.getCity());
        mLatitude.setText(place.getLatitude().toString());
        mLongitude.setText(place.getLongitude().toString());
    }

    @Override
    public void showPicture(Bitmap bitmap) {
        if (bitmap!=null) {
            mPhoto.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this, R.string.deleted_photo_error, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void showForecast(CoordResponse response) {
        mTemp.setText(response.getIndicators().getTemp().toString());
        mWind.setText(response.getWind().getSpeed().toString());
        mClouds.setText(response.getClouds().getAll().toString());
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.out_of_network_error), Toast.LENGTH_LONG).show();
    }
}
