package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a user.
 */
public class MarkUser extends UseCase<MarkUser.RequestValues, MarkUser.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public MarkUser(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        User user = values.getUser();
        user.setMMarked(true);
        mUsersRepository.markUser(user);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final User user;

        public RequestValues(@NonNull User user) {
            this.user = checkNotNull(user, "user cannot be null!");
        }

        public User getUser() {
            return user;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
