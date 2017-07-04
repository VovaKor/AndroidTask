package com.favoriteplaces.user.places.placeslist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.usecases.GetPlaces;
import com.favoriteplaces.repository.FavoritePlacesRepository;
import com.favoriteplaces.repository.local.FavoritePlaceLocalDataSource;
import com.favoriteplaces.utils.ActivityUtils;

/**
 * Created by vova on 03.07.17.
 */

public class FPListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fp_menu);

//
        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));
        FPListFragment fpListFragment =
                (FPListFragment) getSupportFragmentManager().findFragmentById(R.id.placesContentFrame);
        if (fpListFragment == null) {
            // Create the fragment
            fpListFragment = FPListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fpListFragment, R.id.placesContentFrame);
        }

        // Create the presenter

        new FPListPresenter(
                UseCaseHandler.getInstance(),
                userId,
                fpListFragment,
                new GetPlaces(FavoritePlacesRepository.getInstance(FavoritePlaceLocalDataSource.getInstance(getApplicationContext()))));

    }
}
