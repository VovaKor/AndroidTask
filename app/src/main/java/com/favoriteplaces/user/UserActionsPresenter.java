package com.favoriteplaces.user;

import com.favoriteplaces.login.LoginActivity;
import com.favoriteplaces.utils.SessionManager;

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
