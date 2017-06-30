package com.androidtask.user;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;
import com.androidtask.login.LoginActivity;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface UserActionsContract {

    interface View extends BaseView<Presenter> {

        void showDetailsUI(String id);

        void showUserEmail(String userEmail);

        void showActivity(Class activityClass);

        void showAddPlaceUI(String mUserId);
    }

    interface Presenter extends BasePresenter {

        void openDetailsActivity();

        void cancel();

        void openAddPlaceActivity();
    }
}
