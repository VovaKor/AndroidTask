package com.androidtask.user.data;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;
import com.androidtask.domain.models.User;

/**
 * Created by vova on 25.06.17.
 */

public interface UserDataContract {
    interface View extends BaseView<Presenter> {

        void showUserDetails(User user);

        void showUserUpdatedSuccess();

        void finish();
    }

    interface Presenter extends BasePresenter {

        void updateUser();

        void cancel();
    }
}
