package com.favoriteplaces.admin.actions.data;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.usecases.GetUser;

/**
 * Created by vova on 25.06.17.
 */

public class AdminUserDataPresenter implements AdminUserDataContract.Presenter {
    private final UseCaseHandler mUseCaseHandler;
    private AdminUserDataContract.View mView;
    private String mUserId;
    private GetUser mGetUser;
    public AdminUserDataPresenter(UseCaseHandler instance, AdminUserDataContract.View view, String userId, GetUser getUser) {
        mUseCaseHandler = instance;
        mView = view;
        mUserId = userId;
        mGetUser = getUser;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mUseCaseHandler.execute(mGetUser, new GetUser.RequestValues(mUserId), new UseCase.UseCaseCallback<GetUser.ResponseValue>() {
            @Override
            public void onSuccess(GetUser.ResponseValue response) {

                mView.showUserDetails(response.getUser());
            }

            @Override
            public void onError() {

            }
        });

    }
}
