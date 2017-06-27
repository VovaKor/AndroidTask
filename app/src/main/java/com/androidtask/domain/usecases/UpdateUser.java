package com.androidtask.domain.usecases;

import android.support.annotation.NonNull;

import com.androidtask.UseCase;
import com.androidtask.domain.models.User;
import com.androidtask.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 26.06.17.
 */

public class UpdateUser extends UseCase<UpdateUser.RequestValues, UpdateUser.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public UpdateUser(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        User user = values.getUser();
        mUsersRepository.updateUser(user);

        getUseCaseCallback().onSuccess(new ResponseValue(user));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final User mUser;

        public RequestValues(@NonNull User user) {
            mUser = checkNotNull(user, "user cannot be null!");
        }

        public User getUser() {
            return mUser;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final User mUser;

        public ResponseValue(@NonNull User user) {
            mUser = checkNotNull(user, "user cannot be null!");
        }

        public User getUser() {
            return mUser;
        }
    }
}
