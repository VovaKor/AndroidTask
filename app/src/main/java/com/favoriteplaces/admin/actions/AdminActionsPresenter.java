package com.favoriteplaces.admin.actions;

import com.favoriteplaces.admin.actions.AdminActionsContract.Presenter;

/**
 * Created by vova on 25.06.17.
 */

public class AdminActionsPresenter implements Presenter {

    private AdminActionsActivity mAdminActionsView;
    private String mUserId;

    public AdminActionsPresenter(AdminActionsActivity mAdminActionsView, String mUserId) {
        this.mAdminActionsView = mAdminActionsView;
        this.mUserId = mUserId;
    }

    @Override
    public void openBanActivity() {
        mAdminActionsView.showBanUI(mUserId);
    }

    @Override
    public void openDetailsActivity() {
        mAdminActionsView.showDetailsUI(mUserId);
    }

    @Override
    public void cancel() {
        mAdminActionsView.finish();
    }

    @Override
    public void start() {

    }
}
