package com.androidtask.user;

import android.content.Context;

import com.androidtask.login.LoginActivity;
import com.androidtask.utils.SessionManager;

/**
 * Created by vova on 25.06.17.
 */

public class UserActionsPresenter implements UserActionsContract.Presenter {

    private UserActionsContract.View mUserActionsView;
    private String mUserId;
    private SessionManager mSession;

    public UserActionsPresenter(UserActionsContract.View mUserActionsView, SessionManager manager) {
        this.mUserActionsView = mUserActionsView;
        this.mUserId = manager.getUserEmail();
        this.mSession = manager;
    }

    @Override
    public void openDetailsActivity() {
        mUserActionsView.showDetailsUI(mUserId);
    }

    @Override
    public void cancel() {
        mSession.logoutUser();
        mUserActionsView.showActivity(LoginActivity.class);
    }

    @Override
    public void openAddPlaceActivity() {
        mUserActionsView.showAddPlaceUI(mUserId);
    }

    @Override
    public void start() {
        mUserActionsView.showUserEmail(mUserId);
    }
}
