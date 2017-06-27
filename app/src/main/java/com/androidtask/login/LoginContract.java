package com.androidtask.login;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showEmptyEmailError();

        void showEmptyPasswordError();

        void showInvalidEmailError();

        void showInvalidPasswordError();

        void showActivity(Class clazz);

        void showBanUI(String id, String ban_reason);
    }

    interface Presenter extends BasePresenter {

        void loginUser(String id);

        void restartSession(String id);

        void startActivity(Class activityClass);

        void login();
    }
}
