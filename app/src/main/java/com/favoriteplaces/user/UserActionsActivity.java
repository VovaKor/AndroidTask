package com.favoriteplaces.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.favoriteplaces.R;
import com.favoriteplaces.user.data.UserDataActivity;
import com.favoriteplaces.user.places.AddFavoritePlaceActivity;
import com.favoriteplaces.utils.SessionManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 25.06.17.
 */

public class UserActionsActivity extends Activity implements UserActionsContract.View{

    private UserActionsContract.Presenter mPresenter;
    private TextView mEmail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_actions);


        mEmail = (TextView) findViewById(R.id.email);
        mPresenter = new UserActionsPresenter(this, SessionManager.getInstance(getApplicationContext()));
        mPresenter.start();

        Button dataButton = (Button) findViewById(R.id.btnUserData);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openDetailsActivity();
            }
        });
        Button cancelButton = (Button) findViewById(R.id.btnUserCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });
        Button addPlaceB = (Button) findViewById(R.id.btnAddPlace);
        addPlaceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openAddPlaceActivity();
            }
        });
    }


    @Override
    public void showDetailsUI(String id) {
        Intent intent = new Intent(getApplicationContext(), UserDataActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USER_ID), id);
        startActivity(intent);
    }

    @Override
    public void showUserEmail(String userEmail) {
        mEmail.setText(userEmail);
    }

    @Override
    public void showActivity(Class activityClass) {
        Intent i = new Intent(getApplicationContext(), activityClass);
        startActivity(i);
        finish();
    }

    @Override
    public void showAddPlaceUI(String mUserId) {
        Intent intent = new Intent(getApplicationContext(), AddFavoritePlaceActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USER_ID), mUserId);
        startActivity(intent);
    }

    @Override
    public void setPresenter(UserActionsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
