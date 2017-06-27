package com.androidtask.user.data;

import com.androidtask.UseCase;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.models.User;
import com.androidtask.domain.models.UserDetails;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.UpdateUser;

import java.util.UUID;

/**
 * Created by vova on 25.06.17.
 */

public class UserDataPresenter implements UserDataContract.Presenter {
    private final UseCaseHandler mUseCaseHandler;
    private final UpdateUser mUpdateUser;
    private final UserDataActivity.EditTextHolder mHolder;
    private UserDataContract.View mView;
    private String mUserId;
    private GetUser mGetUser;
    private User mUser;

    public UserDataPresenter(UseCaseHandler instance, UserDataContract.View view, UserDataActivity.EditTextHolder holder, String userId, GetUser getUser, UpdateUser updateUser) {
        mUseCaseHandler = instance;
        mView = view;
        mUserId = userId;
        mHolder = holder;
        mGetUser = getUser;
        mUpdateUser = updateUser;
        mView.setPresenter(this);
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

    @Override
    public void updateUser() {
        String nickname = mHolder.mNickname.getText().toString();
        String fname = mHolder.mFirstname.getText().toString();
        String patronimic = mHolder.mPatronimic.getText().toString();
        String lname = mHolder.mLastname.getText().toString();
        String phone = mHolder.mPhone.getText().toString();

        mUser.setNick_name(nickname);
        UserDetails userDetails = new UserDetails();
        userDetails.setFirst_name(fname);
        userDetails.setPatronymic(patronimic);
        userDetails.setLast_name(lname);
        userDetails.setPhone(phone);

        if (mUser.getId_user_details()!=null) {
            userDetails.setId(mUser.getId_user_details());
        }else{
            userDetails.setId(UUID.randomUUID().toString());
        }
        mUser.setUserDetails(userDetails);

        mUseCaseHandler.execute(mUpdateUser, new UpdateUser.RequestValues(mUser), new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
            @Override
            public void onSuccess(UpdateUser.ResponseValue response) {
                mUser = response.getUser();
                mView.showUserUpdatedSuccess();
                mView.showUserDetails(mUser);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void cancel() {
        mView.finish();
    }
}
