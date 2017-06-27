package com.androidtask.admin.actions.data;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;
import com.androidtask.domain.models.User;

/**
 * Created by vova on 25.06.17.
 */

public interface AdminUserDataContract  {
    interface View extends BaseView<Presenter> {

        void showUserDetails(User user);
    }

    interface Presenter extends BasePresenter {

    }
}
