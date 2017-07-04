package com.favoriteplaces.user.places.placeslist;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.domain.usecases.GetPlaces;
import com.favoriteplaces.utils.PictureManager;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 03.07.17.
 */

public class FPListPresenter implements FPListContract.Presenter{

    private final FPListContract.View mFragment;
    private final GetPlaces mGetPlaces;
    private final String mUserId;

    private final UseCaseHandler mUseCaseHandler;
    public FPListPresenter(UseCaseHandler useCaseHandler,
                           String userId,
                           FPListFragment fpListFragment,
                           GetPlaces getPlaces) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mUserId = userId;
        mFragment = checkNotNull(fpListFragment, "View cannot be null!");
        mGetPlaces = checkNotNull(getPlaces, "places cannot be null!");

        mFragment.setPresenter(this);
    }
    @Override
    public void start() {
        loadPlaces(mUserId, true);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public Bitmap createImageBitmap(String path, float density) {
        return PictureManager.getInstance().createSmallImageBitmap(path, density);
    }

    @Override
    public void openAddPlaceUI() {
        mFragment.showAddFPUI(mUserId);
    }

    @Override
    public void openPlaceDetails(String placeId) {
        mFragment.showFPDetailsUI(placeId);
    }

    private void loadPlaces(String userId, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mFragment.setLoadingIndicator(true);
        }

        GetPlaces.RequestValues requestValue = new GetPlaces.RequestValues(userId);

        mUseCaseHandler.execute(mGetPlaces, requestValue,
                new UseCase.UseCaseCallback<GetPlaces.ResponseValue>() {
                    @Override
                    public void onSuccess(GetPlaces.ResponseValue response) {
                        List<FavoritePlace> places = response.getFavoritePlaces();
                        // The view may not be able to handle UI updates anymore
                        if (!mFragment.isActive()) {
                            return;
                        }
                        if (showLoadingUI) {
                            mFragment.setLoadingIndicator(false);
                        }

                        processPlaces(places);
                    }

                    @Override
                    public void onError() {
                        // The view may not be able to handle UI updates anymore
                        if (!mFragment.isActive()) {
                            return;
                        }
                        mFragment.showLoadingPlacesError();
                    }
                });
    }

    private void processPlaces(List<FavoritePlace> places) {
        // Show the list of places
        mFragment.showPlaces(places);
    }

}
