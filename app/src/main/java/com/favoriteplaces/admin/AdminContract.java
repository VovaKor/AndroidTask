package com.favoriteplaces.admin;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;
import com.favoriteplaces.domain.models.User;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AdminContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showUsers(List<User> users);

        void showMarkedUser();

        void showCheckedUsersCleared();

        void showLoadingUsersError();

        boolean isActive();

        void showUserUnchecked();

        void showAdminActionsUI(String id);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadUsers(boolean forceUpdate);

        void markUser(@NonNull User completedUser);

        void uncheckUser(@NonNull User activeUser);

        void deleteCheckedUsers();

        void openActionsDialog(User user);

        Bitmap createImageBitmap(String thumbnail, float density);
    }
}
