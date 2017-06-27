package com.androidtask.admin.actions.ban;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;
import com.androidtask.domain.models.User;

/**
 * Created by vova on 25.06.17.
 */

public interface AdminBanContract {
    interface View extends BaseView<Presenter> {

        void showUserDetails(User user);

        void showUserBannedSuccess();
    }

    interface Presenter extends BasePresenter {
        void cancel();
        void updateUser(String banDate, String banReason);
    }
}
