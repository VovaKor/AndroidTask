package com.androidtask.login;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;
import com.androidtask.WelcomeActivity;

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

    }

    interface Presenter extends BasePresenter {

        void getUser();

        void startActivity(Class activityClass);
    }
}
