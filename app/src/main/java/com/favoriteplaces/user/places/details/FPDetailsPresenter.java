package com.favoriteplaces.user.places.details;

import android.graphics.Bitmap;

import com.favoriteplaces.App;
import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.domain.usecases.GetPlace;
import com.favoriteplaces.utils.PictureManager;
import com.favoriteplaces.weathermap.CoordResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by vova on 04.07.17.
 */

public class FPDetailsPresenter implements FPDetailsContract.Presenter {
    private final UseCaseHandler mUseCaseHandler;
    private final String mAppId;
    private FPDetailsContract.View mView;
    private String mPlaceId;
    private GetPlace mGetPlace;
    private CompositeDisposable mCompositeDisposable;
    
    public FPDetailsPresenter(UseCaseHandler instance,
                              FPDetailsContract.View fpDetailsActivity, 
                              String placeId,
                              String appId,
                              GetPlace getPlace, 
                              CompositeDisposable compositeDisposable) {
        mUseCaseHandler = instance;
        mView = fpDetailsActivity;
        mPlaceId = placeId;
        mAppId = appId;
        mGetPlace = getPlace;
        mCompositeDisposable = compositeDisposable;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mUseCaseHandler.execute(mGetPlace, new GetPlace.RequestValues(mPlaceId), new UseCase.UseCaseCallback<GetPlace.ResponseValue>() {
            @Override
            public void onSuccess(GetPlace.ResponseValue response) {
                FavoritePlace place = response.getPlace();
                mView.showPlaceDetails(place);
                getWeatherForecastByCoords(place.getLatitude(),place.getLongitude());
            }

            @Override
            public void onError() {

            }
        });

    }
    @Override
    public void getWeatherForecastByCoords(Double latitude, Double longitude) {
        if (mView.isOnline()) {
            mCompositeDisposable.add(App.getWeatherMapApi()
                    .getForecastByCoord(latitude, longitude, mAppId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CoordResponse>() {
                        @Override
                        public void onSuccess(CoordResponse value) {
                            mView.showForecast(value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    })
            );
        }else{
            mView.showNetworkError();
        }
    }

    @Override
    public void createBitmap(String photo, int height, int width) {
        Bitmap bitmap = PictureManager.getInstance().createImageBitmap(photo,height,width);
        mView.showPicture(bitmap);
    }

}
