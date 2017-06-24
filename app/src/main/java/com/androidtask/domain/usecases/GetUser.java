package com.androidtask.domain.usecases;

import android.support.annotation.NonNull;

import com.androidtask.UseCase;
import com.androidtask.domain.models.User;
import com.androidtask.repository.UsersDataSource;
import com.androidtask.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 23.06.17.
 */

public class GetUser extends UseCase<GetUser.RequestValues, GetUser.ResponseValue> {
    private final UsersRepository mUsersRepository;

    public GetUser(@NonNull UsersRepository repository) {
        mUsersRepository = checkNotNull(repository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mUsersRepository.getUser(requestValues.getEmail(), new UsersDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (user != null) {
                    ResponseValue responseValue = new ResponseValue(user);
                    getUseCaseCallback().onSuccess(responseValue);
                } else {
                    getUseCaseCallback().onError();
                }
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mEmail;

        public RequestValues(@NonNull String email) {
            mEmail = checkNotNull(email, "email cannot be null!");

        }
        public String getEmail() {
            return mEmail;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final User mUser;

        public ResponseValue(@NonNull User user) {
            mUser = checkNotNull(user, "users cannot be null!");
        }

        public User getUser() {
            return mUser;
        }
    }
}
