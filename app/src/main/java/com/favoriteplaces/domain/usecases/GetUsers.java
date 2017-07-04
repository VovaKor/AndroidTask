package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.repository.UsersDataSource;
import com.favoriteplaces.repository.UsersRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of users.
 */
public class GetUsers extends UseCase<GetUsers.RequestValues, GetUsers.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public GetUsers(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
       }

    @Override
    protected void executeUseCase(final RequestValues values) {
        if (values.isForceUpdate()) {
            mUsersRepository.refreshUsers();
        }

        mUsersRepository.getUsers(new UsersDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
//
                ResponseValue responseValue = new ResponseValue(users);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate) {
            mForceUpdate = forceUpdate;
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<User> mUsers;

        public ResponseValue(@NonNull List<User> users) {
            mUsers = checkNotNull(users, "users cannot be null!");
        }

        public List<User> getUsers() {
            return mUsers;
        }
    }
}
