package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;


import com.favoriteplaces.UseCase;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates a new {@link User} in the {@link UsersRepository}.
 */
public class InsertUser extends UseCase<InsertUser.RequestValues, InsertUser.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public InsertUser(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        User user = values.getUser();
        mUsersRepository.insertUser(user);

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
