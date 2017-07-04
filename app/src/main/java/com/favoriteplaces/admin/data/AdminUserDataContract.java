package com.favoriteplaces.admin.data;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;
import com.favoriteplaces.domain.models.User;

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
