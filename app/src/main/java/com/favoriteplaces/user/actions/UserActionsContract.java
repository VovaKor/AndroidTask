package com.favoriteplaces.user.actions;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface UserActionsContract {

    interface View extends BaseView<Presenter> {

        void showDetailsUI(String id);

        void showUserEmail(String userEmail);

        void showActivity(Class activityClass);

        void showPlaceListUI(String mUserId);
    }

    interface Presenter extends BasePresenter {

        void openDetailsActivity();

        void cancel();

        void openPlaceListActivity();
    }
}
