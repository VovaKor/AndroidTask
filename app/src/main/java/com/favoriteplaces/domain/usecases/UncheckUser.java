package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Removes mark from given user.
 */
public class UncheckUser extends UseCase<UncheckUser.RequestValues, UncheckUser.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public UncheckUser(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        User markedUser = values.getMarkedUser();
        markedUser.setMMarked(false);
        mUsersRepository.uncheckUser(markedUser);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final User mMarkedUser;

        public RequestValues(@NonNull User markedUser) {
            mMarkedUser = checkNotNull(markedUser, "markedUser cannot be null!");
        }

        public User getMarkedUser() {
            return mMarkedUser;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue { }
}
