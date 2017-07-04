package com.favoriteplaces.admin;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.usecases.DeleteUsers;
import com.favoriteplaces.domain.usecases.GetUsers;
import com.favoriteplaces.domain.usecases.MarkUser;
import com.favoriteplaces.domain.usecases.UncheckUser;
import com.favoriteplaces.repository.UsersDataSource;
import com.favoriteplaces.utils.PictureManager;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link AdminFragment}), retrieves the data and updates the
 * UI as required.
 */
public class AdminPresenter implements AdminContract.Presenter {

    private final AdminContract.View mAdminFragment;
    private final GetUsers mGetUsers;
    private final MarkUser mMarkUser;
    private final UncheckUser mUncheckUser;
    private final DeleteUsers mDeleteUsers;

    private boolean mFirstLoad = true;

    private final UseCaseHandler mUseCaseHandler;

    public AdminPresenter(@NonNull UseCaseHandler useCaseHandler,
                          @NonNull AdminContract.View adminView, @NonNull GetUsers getUsers,
                          @NonNull MarkUser markUser, @NonNull UncheckUser uncheckUser,
                          @NonNull DeleteUsers deleteUsers) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mAdminFragment = checkNotNull(adminView, "View cannot be null!");
        mGetUsers = checkNotNull(getUsers, "Users cannot be null!");
        mMarkUser = checkNotNull(markUser, "markUser cannot be null!");
        mUncheckUser = checkNotNull(uncheckUser, "uncheckUser cannot be null!");
        mDeleteUsers = checkNotNull(deleteUsers,
                "deleteUsers cannot be null!");


        mAdminFragment.setPresenter(this);
    }

    @Override
    public void start() {
        loadUsers(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadUsers(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadUsers(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link UsersDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadUsers(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mAdminFragment.setLoadingIndicator(true);
        }

        GetUsers.RequestValues requestValue = new GetUsers.RequestValues(forceUpdate
                //, mCurrentFiltering
        );

        mUseCaseHandler.execute(mGetUsers, requestValue,
                new UseCase.UseCaseCallback<GetUsers.ResponseValue>() {
                    @Override
                    public void onSuccess(GetUsers.ResponseValue response) {
                        List<User> users = response.getUsers();
                        // The view may not be able to handle UI updates anymore
                        if (!mAdminFragment.isActive()) {
                            return;
                        }
                        if (showLoadingUI) {
                            mAdminFragment.setLoadingIndicator(false);
                        }

                        processUsers(users);
                    }

                    @Override
                    public void onError() {
                        // The view may not be able to handle UI updates anymore
                        if (!mAdminFragment.isActive()) {
                            return;
                        }
                        mAdminFragment.showLoadingUsersError();
                    }
                });
    }

    private void processUsers(List<User> users) {

            // Show the list of users
            mAdminFragment.showUsers(users);

    }

    @Override
    public void markUser(@NonNull User user) {
        checkNotNull(user, "user cannot be null!");

        mUseCaseHandler.execute(mMarkUser, new MarkUser.RequestValues(
                        user),
                new UseCase.UseCaseCallback<MarkUser.ResponseValue>() {
                    @Override
                    public void onSuccess(MarkUser.ResponseValue response) {
                        mAdminFragment.showMarkedUser();
                        loadUsers(false, false);
                    }

                    @Override
                    public void onError() {
                        mAdminFragment.showLoadingUsersError();
                    }
                });
    }

    @Override
    public void uncheckUser(@NonNull User markedUser) {
        checkNotNull(markedUser, "activeUser cannot be null!");
        mUseCaseHandler.execute(mUncheckUser, new UncheckUser.RequestValues(markedUser),
                new UseCase.UseCaseCallback<UncheckUser.ResponseValue>() {
                    @Override
                    public void onSuccess(UncheckUser.ResponseValue response) {
                        mAdminFragment.showUserUnchecked();
                        loadUsers(false, false);
                    }

                    @Override
                    public void onError() {
                        mAdminFragment.showLoadingUsersError();
                    }
                });
    }

    @Override
    public void deleteCheckedUsers() {
        mUseCaseHandler.execute(mDeleteUsers, new DeleteUsers.RequestValues(),
                new UseCase.UseCaseCallback<DeleteUsers.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteUsers.ResponseValue response) {
                        mAdminFragment.showCheckedUsersCleared();
                        loadUsers(false, false);
                    }

                    @Override
                    public void onError() {
                        mAdminFragment.showLoadingUsersError();
                    }
                });
    }

    @Override
    public void openActionsDialog(User user) {
        checkNotNull(user,"user cannot be null");
        mAdminFragment.showAdminActionsUI(user.getId());
    }

    @Override
    public Bitmap createImageBitmap(String thumbnail, float density) {
        Bitmap bitmap = PictureManager.getInstance().createSmallImageBitmap(thumbnail, density);
        return bitmap;
    }


}
