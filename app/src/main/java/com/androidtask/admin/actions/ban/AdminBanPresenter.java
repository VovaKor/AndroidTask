package com.androidtask.admin.actions.ban;


import android.text.TextUtils;

import java.util.TimeZone;

import com.androidtask.UseCase;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.models.User;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.UpdateUser;
import com.androidtask.utils.DateManager;

import hirondelle.date4j.DateTime;

/**
 * Created by vova on 26.06.17.
 */

public class AdminBanPresenter implements AdminBanContract.Presenter {
    private final UseCaseHandler mUseCaseHandler;
    private AdminBanActivity mView;
    private String mUserId;
    private GetUser mGetUser;
    private UpdateUser mUpdateUser;
    private User mUser;

    public AdminBanPresenter(UseCaseHandler mUseCaseHandler, AdminBanActivity mView, String mUserId, GetUser mGetUser, UpdateUser mUpdateUser) {
        this.mUseCaseHandler = mUseCaseHandler;
        this.mView = mView;
        this.mUserId = mUserId;
        this.mGetUser = mGetUser;
        this.mUpdateUser = mUpdateUser;
    }

    @Override
    public void cancel() {
        mView.finish();
    }

    @Override
    public void updateUser(String banDay, String banReason) {
        if (!TextUtils.isEmpty(banDay)&&TextUtils.isDigitsOnly(banDay)) {
            mUser.setBan_date(DateManager.createDate(banDay));
        }
        mUser.setBan_reason(banReason);
        mUseCaseHandler.execute(mUpdateUser, new UpdateUser.RequestValues(mUser), new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
            @Override
            public void onSuccess(UpdateUser.ResponseValue response) {
                mUser = response.getUser();
                mView.showUserBannedSuccess();
                mView.showUserDetails(mUser);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void start() {
        mUseCaseHandler.execute(mGetUser, new GetUser.RequestValues(mUserId), new UseCase.UseCaseCallback<GetUser.ResponseValue>() {
            @Override
            public void onSuccess(GetUser.ResponseValue response) {
                mUser = response.getUser();
                mView.showUserDetails(mUser);
            }

            @Override
            public void onError() {

            }
        });

    }
}
