package com.androidtask.register;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface RegisterContract {

    interface View extends BaseView<Presenter> {

        void showPasswordMismatchError();

        void showTooShortPasswordError();

        void showEmptyPasswordError();

        void showTooShortEmailError();

        void showTooLongEmailError();

        void showInvalidEmailError();

        void showEmailExistError();

        void showEmptyEmailError();

        void showActivity(Class clazz);

        void showSaveUserError();
    }

    interface Presenter extends BasePresenter {

        void getUser();

        void saveUser();

        void startActivity(Class activityClass);
    }
}
